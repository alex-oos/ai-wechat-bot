package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.config.AlapiConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.service.RecreationService;
import com.wechat.bot.service.ReplyMsgService;
import com.wechat.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/4/15 16:34
 * <p>
 * 娱乐服务
 * </p>
 */
@Slf4j
@Service
public class RecreationServiceImpl implements RecreationService {

    @Resource
    private ReplyMsgService replyMsgService;

    @Resource
    private AlapiConfig alapiConfig;

    @Override
    public void weatherReminder(ChatMessage chatMessage) {

        String url = alapiConfig.getHost() + "/api/tianqi?token=" + alapiConfig.getToken();
        String response = OkHttpUtil.builder().url(url).get().async();

        JSONObject responseJsonObject = JSONObject.parse(response);
        if (responseJsonObject.getInteger("code") != 200) {
            log.error("Failed to get weather data: error code {}", responseJsonObject.getInteger("code"));
            return;
        }
        JSONObject data = responseJsonObject.getJSONObject("data");
        String replayMsg = "【天气预报】\n" + "\uD83D\uDD52 日期:" + data.getString("date") + "\n" + "\uD83C\uDF26️ 天气:" + data.getString("weather") + "\n" + "\uD83C\uDFD9️ 城市:" + data.getString("province") + " " + data.getString("city") + "\n" + "\uD83C\uDF21️ 温度：" + data.getString("min_temp") + "℃" + "\n" + "\uD83C\uDF2C️ 风向：" + data.getString("wind") + "\n" + "\uD83C\uDF05 日出/日落:：" + data.getString("sunrise") + "/" + data.getString("sunset") + "\n";
        chatMessage.setReplayContent(replayMsg);
        replyMsgService.sendTextMessage(chatMessage);
    }

    /**
     * 早报
     *
     * @param chatMessage
     */
    @Override
    public void morning(ChatMessage chatMessage) {

        String url = alapiConfig.getHost() + "/api/zaobao?token=" + alapiConfig.getToken();
        String response = OkHttpUtil.builder().url(url).get().async();

        JSONObject responseJsonObject = JSONObject.parse(response);
        if (responseJsonObject.getInteger("code") != 200) {
            log.error("Failed to get weather data: error code {}", responseJsonObject.getInteger("code"));
            return;
        }
        JSONObject data = responseJsonObject.getJSONObject("data");
        String date = data.getString("date");
        JSONArray news = data.getJSONArray("news");
        String weiyu = data.getString("weiyu");
        StringBuilder sb = new StringBuilder();
        sb.append("【今日早报】" + date).append("\n");
        for (int i = 0; i < news.size(); i++) {
            sb.append(news.getString(i)).append("\n");
        }
        sb.append(weiyu).append("\n")
                .append("图片url:" + data.getString("image"));
        chatMessage.setReplayContent(sb.toString());
        replyMsgService.sendTextMessage(chatMessage);


    }

}
