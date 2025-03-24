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

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractSchedule {


    @Resource
    protected ReplyMsgService replyMsgService;

    @Resource
    protected BotConfig botConfig;

    @Resource
    protected UserInfoService userInfoService;

    @Async
    @Scheduled(cron = "0 0 8 * * ?")
    public void goodMorning() {

        sendGreetingMessage("生成以%s开头的早安寄语，幽默，风趣一些", "早安寄语发送成功！");
    }

    @Async
    @Scheduled(cron = "0 0 22 * * ?")
    public void goodNight() {

        sendGreetingMessage("生成以%s开头的晚安寄语，幽默，风趣一些", "晚安寄语发送成功！");
    }

    protected abstract List<String> getContactList();

    private void sendGreetingMessage(String contentTemplate, String logMessage) {

        List<String> contactList = getContactList();
        Map<String, String> contactMap = userInfoService.getUserInfo();
        Set<String> contactSet = contactMap.entrySet()
                .stream()
                .filter(entry -> contactList.contains(entry.getValue()))
                .map(e -> e.getKey() + "-" + e.getValue())
                .collect(Collectors.toSet());
        String toUserId = contactMap.entrySet().stream().filter(entry -> entry.getValue().equals("助理")).map(Map.Entry::getKey).findFirst().orElse(null);

        if (contactSet.isEmpty() || toUserId == null) {
            return;
        }
        contactSet.forEach(contact -> {
            String[] split = contact.split("-");
            String content = String.format(contentTemplate, split[1]);
            ChatMessage chatMessage = ChatMessage.builder()
                    .fromUserId(split[0])
                    .toUserId(toUserId)
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

    @Async
    @Scheduled(cron = "0 30 8 * * ?")
    public void weatherReminder() throws IOException {

        List<String> contactList = getContactList();
        for (String userId : contactList) {
            String response = OkHttpUtil.builder()
                    .url("https://v3.alapi.cn/api/tianqi?token=token")
                    .get().async();
            JSONObject responseJsonObject = JSONObject.parse(response);
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
                    "空气质量：" + data.getString("air_level") + "\n";

            MessageApi.postText(botConfig.getAppId(), userId, replayMsg, null);
        }
    }


}
