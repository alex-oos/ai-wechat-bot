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

        sendGreetingMessage("生成以%s开头的早安寄语，幽默，风趣一些", "早安寄语发送成功！");
    }

    @Scheduled(cron = "0 0 22 * * ?")
    public void goodNight() {

        sendGreetingMessage("生成以%s开头的晚安寄语，幽默，风趣一些", "晚安寄语发送成功！");
    }

    private void sendGreetingMessage(String contentTemplate, String logMessage) {
        List<String> contactList = new ArrayList<>();
        // 早安寄语的制定人
        Collections.addAll(contactList, "爸爸", "妈妈", "爷爷", "奶奶");
        Map<String, String> contactMap = messageService.getContactMap();
        Set<String> contactSet = contactMap.entrySet()
                .stream()
                .filter(entry -> contactList.contains(entry.getValue()))
                .map(e -> e.getKey() + "-" + e.getValue())
                .collect(Collectors.toSet());
        if (contactSet.isEmpty()) {
            return;
        }
        contactSet.forEach(contact -> {
            String[] split = contact.split("-");
            String content = String.format(contentTemplate, split[1]);
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
        log.info(logMessage);
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
        String replayMsg = "【天气预报】\n" +
                "\uD83D\uDD52 日期:" + data.getString("date") + "\n" +
                "\uD83C\uDF26️ 天气:" + data.getString("weather") + "\n" +
                "\uD83C\uDFD9️ 城市:" + data.getString("province") + " " + data.getString("city") + "\n" +
                "\uD83C\uDF21️ 温度：" + data.getString("min_temp") + "℃" + "\n" +
                "\uD83C\uDF2C️ 风向：" + data.getString("wind") + "\n" +
                "\uD83C\uDF05 日出/日落:：" + data.getString("sunrise") + "/" + data.getString("sunset") + "\n" +
                //.append("风向：").append(data.getString("win")).append("\n")
                //.append("风力：").append(data.getString("win_speed")).append("\n")
                //.append("湿度：").append(data.getString("humidity")).append("\n")
                "空气质量：" + data.getString("air_level") + "\n";
        //.append("空气质量指数：").append(data.getString("air_index")).append("\n");
        //.append("空气质量描述：").append(data.getString("air_tips")).append("\n")
        //.append("紫外线指数：").append(data.getString("air_index_24h")).append("\n")
        //.append()


        MessageApi.postText(botConfig.getAppId(), null, replayMsg, null);

    }

}
