package com.wechat.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.ReplyMsgService;
import com.wechat.bot.service.UserInfoService;
import com.wechat.gewechat.service.MessageApi;
import com.wechat.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

/**
 * @author Alex
 * @since 2025/3/14 17:40
 * <p></p>
 */
@Slf4j
@Component
public class GoodSchedule {

    @Resource
    private ReplyMsgService replyMsgService;


    @Resource
    private BotConfig botConfig;

    @Resource
    private UserInfoService userInfoService;

    @Async
    @Scheduled(cron = "0 0 8 * * ?")
    //@Scheduled(cron = "0/10 * * * * ?")
    public void goodMorning() {

        sendGreetingMessage("生成以宝宝开头的早安寄语", "早安寄语发送成功！");
    }

    @Async
    @Scheduled(cron = "0 0 23 * * ?")
    public void goodNight() {

        sendGreetingMessage("生成以宝宝开头的晚安寄语", "晚安寄语发送成功！");
    }

    @Async
    @Scheduled(cron = "0 30 11 * * ?")
    public void goodAfternoon() {

        sendGreetingMessage("提醒宝宝中午可以吃饭了", "中午可以吃饭了");
    }

    private void sendGreetingMessage(String content, String logMessage) {

        Map<String, String> contactMap = userInfoService.getUserInfo();
        String toUserId = contactMap.entrySet().stream().filter(entry -> entry.getValue().equals("将哥私人小助理")).map(Map.Entry::getKey).findFirst().orElse(null);
        if (toUserId == null) {
            return;
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .fromUserId("wxid_l5j1r9mnkigj22")
                //.fromUserId(fromUserId)
                .toUserId(toUserId)
                .ctype(MsgTypeEnum.TEXT)
                .content(content)
                .appId(botConfig.getAppId())
                .build();

        Session session = new Session(UUID.randomUUID().toString(), null);
        session.addQuery(chatMessage.getContent());
        replyMsgService.replyTextMsg(chatMessage, session);

        log.info(logMessage);
    }

    @Async
    @Scheduled(cron = "0 30 8 * * ?")
    public void weatherReminder() {


        String response = OkHttpUtil.builder()
                .url("https://v3.alapi.cn/api/tianqi?token=e8sv56k9jym88c8papmxfk1n0a5f7l")
                .get().async();

        JSONObject responseJsonObject = JSONObject.parse(response);
        if (responseJsonObject.getInteger("code") != 200) {
            log.error("Failed to get weather data: error code {}", responseJsonObject.getInteger("code"));
            return;
        }
        JSONObject data = responseJsonObject.getJSONObject("data");
        String replayMsg = "【天气预报】\n" +
                "\uD83D\uDD52 日期:" + data.getString("date") + "\n" +
                "\uD83C\uDF26️ 天气:" + data.getString("weather") + "\n" +
                "\uD83C\uDFD9️ 城市:" + data.getString("province") + " " + data.getString("city") + "\n" +
                "\uD83C\uDF21️ 温度：" + data.getString("min_temp") + "℃" + "\n" +
                "\uD83C\uDF2C️ 风向：" + data.getString("wind") + "\n" +
                "\uD83C\uDF05 日出/日落:：" + data.getString("sunrise") + "/" + data.getString("sunset") + "\n";

        MessageApi.postText(botConfig.getAppId(), "wxid_l5j1r9mnkigj22", replayMsg, "wxid_7rwgmva15rlf22");

    }


}
