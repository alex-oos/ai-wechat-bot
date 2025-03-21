package com.wechat.bot.service.impl;

import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.factory.AiServiceFactory;
import com.wechat.ai.service.AIService;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.service.ReplyMsgService;
import com.wechat.gewechat.service.MessageApi;
import com.wechat.util.IpUtil;
import com.wechat.util.VideoDuration;
import com.wechat.util.VideoScreenshotUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * @author Alex
 * @since 2025/2/18 21:37
 * <p></p>
 */
@Slf4j
@Service
public class ReplyMsgServiceImpl implements ReplyMsgService {


    @Resource(name = "commonThreadPool")
    private TaskExecutor executor;

    @Resource
    private BotConfig botconfig;

    private AIService aiService;

    private Session session;


    @Override
    public void replyType(ChatMessage chatMessage, Session session1) {

        session = session1;
        aiService = chooseAiService();
        // 判断类型
        switch (chatMessage.getCtype()) {
            case TEXT:
                this.replyTextMsg(chatMessage);
                break;
            case IMAGE:
                // TODO 先过滤掉，图片识别，后期再来做
                if (chatMessage.getContent().contains("xml")) {
                    return;
                }
                this.replyImageMsg(chatMessage);
                break;
            case VOICE:
                this.replyAudioMsg(chatMessage);
                break;
            case VIDEO:
                this.replyVideoMsg(chatMessage);
                break;
            case IMAGERECOGNITION:
                this.imageRecognition(chatMessage);
                break;
            default:
                break;
        }
    }

    @Override
    public void replyTextMsg(ChatMessage chatMessage) {

        String replayMsg = aiService.textToText(session);
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), replayMsg, chatMessage.getToUserId());
        log.info("消息回复成功，回复人：{}，回复内容为：{}", chatMessage.getFromUserNickname(), replayMsg);
    }

    @Override
    public void replyTextMsg(ChatMessage chatMessage, Session session) {

        aiService = chooseAiService();
        String replayMsg = aiService.textToText(session);
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), replayMsg, chatMessage.getToUserId());
        log.info("消息回复成功，回复人：{}，回复内容为：{}", chatMessage.getFromUserNickname(), replayMsg);
    }

    @Override
    public void replyImageMsg(ChatMessage chatMessage) {

        Map<String, String> map = aiService.textToImage(chatMessage.getContent());
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), map.get("actual_prompt"), chatMessage.getToUserId());
        MessageApi.postImage(chatMessage.getAppId(), chatMessage.getFromUserId(), map.get("url"));
        chatMessage.setPrepared(true);

    }

    @Override
    public void replyVideoMsg(ChatMessage chatMessage) {

        Map<String, Object> map = aiService.textToVideo(chatMessage.getContent());
        // 生成首图
        String videoUrl = (String) map.get("videoUrl");
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyddMM"));
        Path thumbPath = Path.of("data", "images", date, UUID.randomUUID().toString().concat(".jpg"));
        thumbPath.toFile().getParentFile().mkdirs();
        VideoScreenshotUtil.useJavacv(videoUrl, thumbPath.toString());
        String thumbUrl = "http://" + IpUtil.getIp() + ":" + 9919 + "/" + thumbPath;
        MessageApi.postVideo(chatMessage.getAppId(), chatMessage.getFromUserId(), videoUrl, thumbUrl, (Integer) map.get("videoDuration"));
        chatMessage.setPrepared(true);

    }

    @Override
    public void replyFileMsg(ChatMessage chatMessage) {

    }

    @Override
    public void replyAudioMsg(ChatMessage chatMessage) {

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyddMM"));
        Path audioPath = Path.of("data", "audio", date, UUID.randomUUID().toString().concat(".silk"));
        audioPath.getParent().toFile().mkdirs();
        aiService.textToVoice(chatMessage.getContent(), audioPath.toString());
        String voiceUrl = "http://" + IpUtil.getIp() + ":" + 9919 + "/" + audioPath;
        int audioDurationMs = VideoDuration.getAudioDurationMs(audioPath.toString());

        MessageApi.postVoice(chatMessage.getAppId(), chatMessage.getFromUserId(), voiceUrl, audioDurationMs);
        chatMessage.setPrepared(true);
        //audioPath.toFile().deleteOnExit();
    }

    @Override
    public void replyLocationMsg(ChatMessage chatMessage) {

    }

    @Override
    public void replyLinkMsg(ChatMessage chatMessage) {

    }

    @Override
    public void imageRecognition(ChatMessage chatMessage) {

        String s = aiService.imageToText(session);
        log.info("图片识别成功，图片内容：{}", s);
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), s, chatMessage.getToUserId());
        chatMessage.setPrepared(true);
    }

    @Override
    public AIService chooseAiService() {

        // 找到正常的服务，然后取出枚举值
        //AiEnum aiEnum = AiEnum.getByBotType(Objects.requireNonNull(FileUtil.readFile()).getAiType());
        AiEnum aiEnum = null;
        if (botconfig != null) {
            aiEnum = AiEnum.getByBotType(botconfig.getAiType());
        }
        return AiServiceFactory.getAiService(aiEnum);
    }

}
