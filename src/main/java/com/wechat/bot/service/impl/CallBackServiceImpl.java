package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.ali.config.ALiConfig;
import com.wechat.ai.contant.AiEnum;
import com.wechat.ai.factory.AiServiceFactory;
import com.wechat.ai.service.AIService;
import com.wechat.bot.service.CallBackService;
import com.wechat.bot.contant.MsgTypeEnum;
import com.wechat.bot.entity.message.reply.ReplyTextMessage;
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
public class CallBackServiceImpl implements CallBackService {

    //@Resource
    //private AliService aliService;

    //@Resource(name = "common")
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
        Integer msgType = data.getInteger("MsgType");


        // 过滤
        Boolean isFilter = this.filterOther(wxid, fromUserName, toUserName, msgSource, receiveMsg);
        if (isFilter) {
            return;
        }

        //TODO 判断一下，是否单人聊天，还是群里面聊天
        // 代表是群里面的消息
        if (fromUserName.contains("@")) {
            //TODO(群消息，如何回复)
            this.groupMsg(requestBody);
            return;
        }
        this.personalMsg(msgType, receiveMsg, appid, toUserName, fromUserName);


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
    public Boolean filterOther(String wxid, String fromUsername, String toUserName, String msgSource, String content) {

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
        log.info("收到消息：{}", content);

        if (content.contains(toUserName) || content.contains(fromUsername)) {
            return true;
        }
        //TODO代表目前是群消息，目前先关闭掉，后期先去想法子打开
        if (fromUsername.contains("@")) {
            return true;
        }
        return false;


    }

    @Override
    public void replyTextMsg(String receiveMsg, ReplyTextMessage replyTextMessage) {

        AIService aiService = chooseAiService();

        CompletableFuture.supplyAsync(() -> {
            log.info("请求阿里云");
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
     * @param msgType
     * @param receiveMsg
     * @param appid
     * @param toUserName
     * @param fromUserName
     */
    public void personalMsg(Integer msgType, String receiveMsg, String appid, String toUserName, String fromUserName) {

        this.sendMsgType(msgType, receiveMsg, appid, toUserName, fromUserName);

    }

    public void sendMsgType(Integer msgType, String receiveMsg, String appid, String toUserName, String fromUserName) {
        // 判断类型
        switch (MsgTypeEnum.getMsgTypeEnum(msgType)) {
            case TEXT:
                ReplyTextMessage replyTextMessage = ReplyTextMessage.builder().appId(appid).toWxid(toUserName).toWxid(fromUserName).build();
                this.replyTextMsg(receiveMsg, replyTextMessage);
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

        this.sendMsgType(null, null, null, null, null);


    }


}
