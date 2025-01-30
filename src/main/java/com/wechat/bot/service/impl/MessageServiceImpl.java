package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.ali.config.ALiConfig;
import com.wechat.ai.contant.AiEnum;
import com.wechat.ai.factory.AiServiceFactory;
import com.wechat.ai.service.AIService;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.service.MessageService;
import com.wechat.bot.contant.MsgTypeEnum;
import com.wechat.gewechat.service.ContactApi;
import com.wechat.gewechat.service.MessageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alex
 * @since 2025/1/26 20:00
 * <p></p>
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {


    @Resource
    private ThreadPoolTaskExecutor executor;

    @Resource
    private AiServiceFactory aiServiceFactory;

    @Resource
    private ALiConfig aliConfig;

    @Resource
    BotConfig botconfig;

    @Async
    @Override
    public void receiveMsg(JSONObject requestBody) {

        String appid = requestBody.getString("Appid");
        String wxid = requestBody.getString("Wxid");
        JSONObject data = requestBody.getJSONObject("Data");
        String fromUserName = data.getJSONObject("FromUserName").getString("string");
        String toUserName = data.getJSONObject("ToUserName").getString("string");
        String content = data.getJSONObject("Content").getString("string");
        String msgSource = data.getString("MsgSource");
        String msgId = data.getString("'NewMsgId'");
        // 消息类型
        Integer msgType = data.getInteger("MsgType");
        boolean isGroup = fromUserName.contains("@chatroom");
        ChatMessage chatMessage = ChatMessage.builder()
                .msgId(msgId)
                .createTime(data.getLong("CreateTime"))
                .ctype(MsgTypeEnum.getMsgTypeEnum(msgType))
                .content(content)
                .fromUserId(fromUserName)
                .toUserId(toUserName)
                .isMyMsg(wxid.equals(fromUserName))
                .isGroup(isGroup)
                .isAt(content.contains("@" + wxid))
                .actualUserId(wxid)
                .appId(appid)
                .rawMsg(requestBody)
                .build();

        // 过滤掉非用户信息
        Boolean isFilter = this.filterNotUserMessage(chatMessage, msgSource);
        if (isFilter) {
            return;
        }

        // 获取好友的信息
        JSONObject briefInfo = ContactApi.getBriefInfo(appid, Collections.singletonList(chatMessage.getFromUserId()));
        if (briefInfo.getInteger("ret") == 200) {
            JSONArray dataList = briefInfo.getJSONArray("data");
            JSONObject userInfo = dataList.getJSONObject(0);
            String remark = userInfo.getString("remark");
            String nickname = remark != null ? remark : userInfo.getString("nickName");
            chatMessage.setFromUserNickname(nickname);
        }


        // 判断一下消息的类型
        this.sendMsgType(chatMessage);

    }

    /**
     * 过滤掉一些消息
     * 检查消息是否来自非用户账号（如公众号、腾讯游戏、微信团队等）
     * <p>
     * Args:
     * msg_source: 消息的MsgSource字段内容
     * from_user_id: 消息发送者的ID
     * <p>
     * Returns:
     * bool: 如果是非用户消息返回True，否则返回False
     * <p>
     * Note:
     * 通过以下方式判断是否为非用户消息：
     * 1. 检查MsgSource中是否包含特定标签
     * 2. 检查发送者ID是否为特殊账号或以特定前缀开头
     *
     * @return true代表是过滤的消息，false代表不是过滤的消息
     */
    public Boolean filterNotUserMessage(ChatMessage chatMessage, String msgSource) {

        // 防止给自己发消息
        if (chatMessage.getIsMyMsg()) {
            return true;
        }
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, "Tencent-Games", "weixin");
        //过滤公众号与腾讯团队
        if (list1.contains(chatMessage.getFromUserId()) || chatMessage.getFromUserId().startsWith("gh_")) {
            return true;
        }
        // 添加过滤标签
        List<String> list = new ArrayList<>();
        Collections.addAll(list, "<tips>3</tips>", "<bizmsgshowtype>", "</bizmsgshowtype>", "<bizmsgfromuser>", "</bizmsgfromuser>");
        if (list.contains(msgSource)) {
            return true;
        }

        // 过滤掉5分钟前的消息
        if (chatMessage.getCreateTime() - (System.currentTimeMillis() / 1000) > 60 * 5) {
            return true;
        }
        return false;


    }

    @Override
    public Boolean filterErrorMessage(String requestBody) {
        //gewechat服务发送的回调测试消息
        JSONObject response = JSONObject.parseObject(requestBody);
        if (response.containsKey("testMsg") && response.containsKey("token")) {
            return true;
        }
        // 过滤掉不包含Data字段的信息
        if (!response.containsKey("Data")) {
            return true;
        }
        if (!response.getJSONObject("Data").containsKey("NewMsgId")) {
            return true;
        }
        return false;
    }

    @Override
    public void replyTextMsg(ChatMessage chatMessage) {

        AIService aiService = chooseAiService();

        CompletableFuture.supplyAsync(() -> {
            log.info("请求AI服务");
            return aiService.textToText(chatMessage.getContent());
        }, executor).thenApplyAsync((res) -> {
            res.forEach(msg -> {
                log.info("请求gewechat服务：{}", msg);
                JSONObject jsonObject = MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), msg, chatMessage.getToUserId());
                if (jsonObject.getInteger("ret") == 200) {
                    log.info("gewechat服务回复成功");
                }
            });
            return null;
        }, executor);

    }

    /**
     * 个人消息
     */
    @Override
    public void personalMsg(ChatMessage chatMessage) {
        // 聊天前缀过滤
        List<String> singleChatPrefix = botconfig.getSingleChatPrefix();
        if (!singleChatPrefix.isEmpty()) {
            // 单独聊天前缀过滤
            for (String chatPrefix : singleChatPrefix) {
                if (!chatMessage.getContent().startsWith(chatPrefix)) {
                    return;
                }
            }
        }
        this.replyTextMsg(chatMessage);


    }

    /**
     * 发送消息类型，主要是组装好，各种类型的消息体，以及消息类型
     */
    @Override
    public void sendMsgType(ChatMessage chatMessage) {
        // 判断类型
        switch (chatMessage.getCtype()) {
            case TEXT:
                // 判断是否是群，或个人，如果是群的话，是需要怎么样回复，如果是个人的话，需要怎么样回复
                if (chatMessage.getIsGroup()) {
                    log.info("群消息类型");
                    this.groupMsg(chatMessage);
                    return;
                } else {
                    log.info("个人消息");
                    this.personalMsg(chatMessage);
                }

                break;
            case IMAGE:
                break;
            case VOICE:
                break;
            case VIDEO:
                break;
            default:
                break;
        }
    }

    @Override
    public AIService chooseAiService() {

        if (aliConfig.getEnabled()) {
            // 找到正常的服务，然后取出枚举值
            AiEnum aiEnum = AiEnum.getByName(aliConfig.getName());
            AIService aiService = AiServiceFactory.getAiService(aiEnum);
            return aiService;
        }
        return null;

    }

    /**
     * 群消息，如何回复
     */
    public void groupMsg(ChatMessage chatMessage) {

        // 黑名单过滤
        List<String> groupNameWhiteList = botconfig.getGroupNameWhiteList();
        if (!groupNameWhiteList.isEmpty()) {
            // 判断群名是否在白名单中
            if (!groupNameWhiteList.contains(chatMessage.getToUserNickname())) {
                return;
            }


            //TODO(群消息，如何回复)
            //消息如何拼接，是否需要艾特人，等等之类的，还有各种各样的欢迎语

            //this.sendMsgType(null, null, null, null, null);

            return;
        }


    }


}
