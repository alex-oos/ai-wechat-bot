package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.ali.config.ALiConfig;
import com.wechat.ai.contant.AiEnum;
import com.wechat.ai.factory.AiServiceFactory;
import com.wechat.ai.service.AIService;
import com.wechat.bot.service.MessageService;
import com.wechat.bot.contant.MsgTypeEnum;
import com.wechat.bot.entity.message.reply.ReplyTextMessage;
import com.wechat.gewechat.service.MessageApi;
import kotlin.contracts.ReturnsNotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.metadata.aggregated.rule.ReturnValueMayOnlyBeMarkedOnceAsCascadedPerHierarchyLine;
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

    @Async
    @Override
    public void receiveMsg(JSONObject requestBody) {

        String appid = requestBody.getString("Appid");
        String wxid = requestBody.getString("Wxid");
        JSONObject data = requestBody.getJSONObject("Data");
        String fromUserName = data.getJSONObject("FromUserName").getString("string");
        String toUserName = data.getJSONObject("ToUserName").getString("string");
        String receiveMsg = data.getJSONObject("Content").getString("string");
        String msgSource = data.getString("MsgSource");
        String msgId = data.getString("'NewMsgId'");
        // 消息类型
        Integer msgType = data.getInteger("MsgType");
        boolean isGroup = fromUserName.contains("@chatroom");

        // 过滤掉非用户信息
        Boolean isFilter = this.filterNotUserMessage(wxid, fromUserName, toUserName, msgSource, receiveMsg);
        if (isFilter) {
            return;
        }
        // 判断一下消息的类型
        this.sendMsgType(msgType, receiveMsg, appid, toUserName, fromUserName, isGroup);

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
     * @param wxid         微信id
     * @param fromUsername 发送者id
     * @param toUserName   接收者id
     * @param msgSource    消息来源
     * @param content      消息内容
     * @return true代表是过滤的消息，false代表不是过滤的消息
     */
    @Override
    public Boolean filterNotUserMessage(String wxid, String fromUsername, String toUserName, String msgSource, String content) {

        // 防止给自己发消息
        if (wxid.equals(fromUsername)) {
            return true;
        }
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, "Tencent-Games", "weixin");
        //过滤公众号与腾讯团队
        if (list1.contains(fromUsername) || fromUsername.startsWith("gh_")) {
            return true;
        }
        // 添加过滤标签
        List<String> list = new ArrayList<>();
        Collections.addAll(list, "<tips>3</tips>", "<bizmsgshowtype>", "</bizmsgshowtype>", "<bizmsgfromuser>", "</bizmsgfromuser>");
        if (list.contains(msgSource)) {
            return true;
        }
        if (content.contains(toUserName) || content.contains(fromUsername)) {
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
    public void replyTextMsg(String receiveMsg, ReplyTextMessage replyTextMessage) {

        AIService aiService = chooseAiService();

        CompletableFuture.supplyAsync(() -> {
            log.info("请求AI服务");
            return aiService.textToText(receiveMsg);
        }, executor).thenApplyAsync((res) -> {
            res.forEach(msg -> {
                log.info("请求gewechat服务：{}", msg);
                replyTextMessage.setContent(msg);
                JSONObject jsonObject = MessageApi.postText(replyTextMessage);
                if (jsonObject.getInteger("ret") == 200) {
                    log.info("gewechat服务回复成功");
                }
            });
            return null;
        }, executor);

    }

    /**
     * 个人消息
     *
     * @param msgType
     * @param receiveMsg
     * @param appid
     * @param toUserName
     * @param fromUserName
     */
    public void personalMsg(String receiveMsg, ReplyTextMessage replyTextMessage) {

        this.replyTextMsg(receiveMsg, replyTextMessage);


    }

    /**
     * 发送消息类型，主要是组装好，各种类型的消息体，以及消息类型
     *
     * @param msgType
     * @param receiveMsg
     * @param appid
     * @param toUserName
     * @param fromUserName
     */
    @Async
    public void sendMsgType(Integer msgType, String receiveMsg, String appid, String toUserName, String fromUserName, Boolean isGroup) {
        // 判断类型
        switch (MsgTypeEnum.getMsgTypeEnum(msgType)) {
            case TEXT:
                ReplyTextMessage replyTextMessage = ReplyTextMessage.builder().appId(appid).toWxid(toUserName).toWxid(fromUserName).build();
                // 判断是否是群，或个人，如果是群的话，是需要怎么样回复，如果是个人的话，需要怎么样回复
                if (isGroup) {
                    log.info("群消息类型{}", appid);
                    //TODO(群消息，如何回复)
                    return;
                } else {
                    log.info("个人消息");
                    this.replyTextMsg(receiveMsg, replyTextMessage);
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
     *
     * @param requestBody
     */
    @Override
    public void groupMsg(JSONObject requestBody) {
        //TODO(群消息，如何回复)
        //消息如何拼接，是否需要艾特人，等等之类的，还有各种各样的欢迎语

        //this.sendMsgType(null, null, null, null, null);

        return;

    }


}
