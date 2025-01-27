package com.wechat.bot.bot.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.ai.ali.service.impl.AliService;
import com.wechat.bot.ai.contant.AiEnum;
import com.wechat.bot.ai.factory.AiServiceFactory;
import com.wechat.bot.ai.service.AIService;
import com.wechat.bot.bot.service.CallBackService;
import com.wechat.bot.contant.MsgTypeEnum;
import com.wechat.bot.entity.message.reply.ReplyTextMessage;
import com.wechat.bot.gewechat.service.MessageApi;
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

    @Resource
    private AliService aliService;

    //@Resource(name = "common")
    @Resource
    private ThreadPoolTaskExecutor executor;

    @Resource
    private AiServiceFactory aiServiceFactory;

    @Override
    public Boolean filterOther(String wxid, String fromUsername, String toUserName, String msgSource, String content) {
        /**
         *   """检查消息是否来自非用户账号（如公众号、腾讯游戏、微信团队等）
         *
         *         Args:
         *             msg_source: 消息的MsgSource字段内容
         *             from_user_id: 消息发送者的ID
         *
         *         Returns:
         *             bool: 如果是非用户消息返回True，否则返回False
         *
         *         Note:
         *             通过以下方式判断是否为非用户消息：
         *             1. 检查MsgSource中是否包含特定标签
         *             2. 检查发送者ID是否为特殊账号或以特定前缀开头
         */
        // 防止给自己发消息
        if (wxid.equals(fromUsername)) {
            return true;
        }
        //TODO(当前bug，总是莫名奇妙向其他群发消息，目前仍然未解决)
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, "Tencent-Games", "weixin");
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
        //代表目前是群消息，目前先关闭掉，后期先去想法子打开
        if (fromUsername.contains("@")) {
            return true;
        }
        return false;


    }

    @Override
    public void replyTextMsg(String receiveMsg, ReplyTextMessage replyTextMessage) {

        CompletableFuture.supplyAsync(() -> {
            log.info("请求阿里云");
            return aliService.textToText(receiveMsg);
        }, executor).thenApplyAsync((res) -> {
            res.forEach(msg -> {
                log.info("请求gewechat服务：{}", msg);
                replyTextMessage.setContent(msg);
                MessageApi.postText(replyTextMessage);
            });

            return res.listIterator();
        }, executor);

        //List<String> msgList = aliService.textToText(receiveMsg);
        //
        //msgList.forEach(msg -> {
        //    replyTextMessage.setContent(msg);
        //    MessageApi.postText(replyTextMessage);
        //});
        log.info("回复消息：{}", replyTextMessage);
    }

    @Async
    @Override
    public void receiveMsg(JSONObject requestBody) {

        String appid = requestBody.getString("Appid");
        String wxid = requestBody.getString("Wxid");
        JSONObject data = requestBody.getJSONObject("Data");
        String fromUsername = data.getJSONObject("FromUserName").getString("string");
        String toUserName = data.getJSONObject("ToUserName").getString("string");
        String receiveMsg = data.getJSONObject("Content").getString("string");
        String msgSource = data.getString("MsgSource");
        Integer msgType = data.getInteger("MsgType");


        // 过滤
        Boolean isFilter = this.filterOther(wxid, fromUsername, toUserName, msgSource, receiveMsg);
        if (isFilter) {
            return;
        }

        // 判断类型
        switch (MsgTypeEnum.getMsgTypeEnum(msgType)) {
            case TEXT:
                ReplyTextMessage replyTextMessage = ReplyTextMessage.builder().appId(appid).toWxid(toUserName).toWxid(fromUsername).build();
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
    public void chooseAiService() {

        AiEnum aiEnum = AiEnum.getById(1);
        AIService aiService = AiServiceFactory.getAiService(aiEnum);
    }


}
