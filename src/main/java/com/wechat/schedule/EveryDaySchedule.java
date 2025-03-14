package com.wechat.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.MessageService;
import com.wechat.bot.service.ReplyMsgService;
import com.wechat.gewechat.service.MessageApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alex
 * @since 2025/3/14 15:01
 * <p>
 * 早安寄语定时任务
 * </p>
 */
@Slf4j
@Component
public class EveryDaySchedule {

    @Resource
    private MessageService messageService;

    @Resource
    private ReplyMsgService replyMsgService;

    @Resource
    private BotConfig botConfig;
    @Async
    @Scheduled(cron = "0 0 8 * * ?")
    public void morningReport() {

        List<String> contactList = new ArrayList<>();
        // 早安寄语的制定人
        Collections.addAll(contactList, "爸爸", "妈妈", "爷爷", "奶奶");
        Map<String, String> contactMap = messageService.getContactMap();
        Set<String> cantactSet = contactMap.entrySet()
                .stream()
                .filter(entry -> contactList.contains(entry.getValue()))
                .map(e -> e.getKey() + "-" + e.getValue())
                .collect(Collectors.toSet());
        if (cantactSet.isEmpty()) {
            return;
        }
        cantactSet.forEach(contact -> {
            String[] split = contact.split("-");
            String content = "生成以" + split[1] + "开头的早安寄语，幽默，风趣一些";
            ChatMessage chatMessage = ChatMessage.builder()
                    .fromUserId(split[0])
                    .toUserId(null)
                    .ctype(MsgTypeEnum.TEXT)
                    .content(content)
                    .appId(botConfig.getAppId())
                    .build();
            Session session = new Session(contact, null);
            session.addQuery(chatMessage.getContent());
            replyMsgService.replyTextMsg(chatMessage, session);
        });
        log.info("早安寄语发送成功！");

    }

    @Async
    @Scheduled(cron = "0 0 22 * * ?")
    public void eveningReport() {

        List<String> contactList = new ArrayList<>();
        // 早安寄语的制定人
        Collections.addAll(contactList, "爸爸", "妈妈", "爷爷", "奶奶");
        Map<String, String> contactMap = messageService.getContactMap();
        Set<String> cantactSet = contactMap.entrySet()
                .stream()
                .filter(entry -> contactList.contains(entry.getValue()))
                .map(e -> e.getKey() + "-" + e.getValue())
                .collect(Collectors.toSet());
        if (cantactSet.isEmpty()) {
            return;
        }
        cantactSet.forEach(contact -> {
            String[] split = contact.split("-");
            String content = "生成以" + split[1] + "开头的晚安寄语，幽默，风趣一些";
            ChatMessage chatMessage = ChatMessage.builder()
                    .fromUserId(split[0])
                    .toUserId(null)
                    .ctype(MsgTypeEnum.TEXT)
                    .content(content)
                    .appId(botConfig.getAppId())
                    .build();
            Session session = new Session(contact, null);
            session.addQuery(chatMessage.getContent());
            replyMsgService.replyTextMsg(chatMessage, session);
        });
    }

    /**
     * 每日天气提醒
     * https://www.alapi.cn
     */
    @Async
    @Scheduled(cron = "0 30 8 * * ?")
    public void weatherReminder() throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://v3.alapi.cn/api/tianqi?token=token")
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        JSONObject responseJsonObject = JSONObject.parse(body.string());
        if (responseJsonObject.getInteger("code") != 200) {
            return;
        }
        JSONObject data = responseJsonObject.getJSONObject("data");


        String replayMsg = null;
        MessageApi.postText(botConfig.getAppId(), null, replayMsg, null);

    }

}
