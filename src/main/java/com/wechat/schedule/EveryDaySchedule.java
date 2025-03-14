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

    @Scheduled(cron = "0 0 8 * * ?")
    public void goodMorning() {

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

    @Scheduled(cron = "0 0 22 * * ?")
    public void goodNight() {

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
     * 每日天气提醒:
     * 调用接口
     * https://www.alapi.cn
     */
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
        StringBuilder replayMsg = new StringBuilder();
        replayMsg.append("【天气预报】\n")
                .append("\uD83D\uDD52 日期:").append(data.getString("date")).append("\n")
                .append("\uD83C\uDF26️ 天气:").append(data.getString("weather")).append("\n")
                .append("\uD83C\uDFD9️ 城市:").append(data.getString("province")).append(" ").append(data.getString("city")).append("\n")
                .append("\uD83C\uDF21️ 温度：").append(data.getString("min_temp")).append("℃").append("\n")
                .append("\uD83C\uDF2C️ 风向：").append(data.getString("wind")).append("\n")
                .append("\uD83C\uDF05 日出/日落:：").append(data.getString("sunrise")).append("/").append(data.getString("sunset")).append("\n")
                //.append("风向：").append(data.getString("win")).append("\n")
                //.append("风力：").append(data.getString("win_speed")).append("\n")
                //.append("湿度：").append(data.getString("humidity")).append("\n")
                .append("空气质量：").append(data.getString("air_level")).append("\n");
        //.append("空气质量指数：").append(data.getString("air_index")).append("\n");
        //.append("空气质量描述：").append(data.getString("air_tips")).append("\n")
        //.append("紫外线指数：").append(data.getString("air_index_24h")).append("\n")
        //.append()


        MessageApi.postText(botConfig.getAppId(), null, replayMsg.toString(), null);

    }

}
