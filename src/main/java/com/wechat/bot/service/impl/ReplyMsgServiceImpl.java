package com.wechat.bot.service.impl;

import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.factory.AiServiceFactory;
import com.wechat.ai.service.AIService;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.ReplyMsgService;
import com.wechat.gewechat.service.MessageApi;
import com.wechat.search.serivce.AliAiSearchService;
import com.wechat.util.AudioFormatConversionSilk;
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


    @Resource
    AliAiSearchService aliAiSearchService;

    @Resource(name = "commonThreadPool")
    private TaskExecutor executor;

    @Resource
    private BotConfig botconfig;

    private AIService aiService;


    private void handleGroupMessage(ChatMessage chatMessage, String replayMsg) {

        this.replayAitMsg(chatMessage);
        if (!chatMessage.getPrepared()) {
            this.replayQuoteMsg(chatMessage);
        }
    }

    private void sendTextMessage(ChatMessage chatMessage, String replayMsg) {

        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), replayMsg, chatMessage.getToUserId());
        log.info("消息回复成功，回复人：{}，回复内容为：{}", chatMessage.getFromUserNickname(), replayMsg);
    }

    @Override
    public AIService chooseAiService() {

        AiEnum aiEnum = null;
        if (botconfig != null) {
            aiEnum = AiEnum.getByBotType(botconfig.getAiType());
        }
        return AiServiceFactory.getAiService(aiEnum);
    }

    @Override
    public void replyTextMsg(ChatMessage chatMessage) {

        String replayMsg = null;
        if (chatMessage.getSession().getIsActiveSearch()) {
            replayMsg = aliAiSearchService.searchAndAI(chatMessage.getSession().getTextMessages());
        } else {
            replayMsg = aiService.textToText(chatMessage.getSession());
        }
        chatMessage.setReplayContent(replayMsg);
        if (chatMessage.getIsGroup()) {
            handleGroupMessage(chatMessage, replayMsg);
            return;
        }

        if (chatMessage.getCtype() == MsgTypeEnum.APPMSG) {
            this.replayQuoteMsg(chatMessage);
            return;
        }

        sendTextMessage(chatMessage, replayMsg);

    }

    @Override
    public void replayMessage(ChatMessage chatMessage) {

        aiService = chooseAiService();
        // 判断类型
        switch (chatMessage.getCtype()) {
            case TEXT:
                this.replyTextMsg(chatMessage);
                break;
            case IMAGE:
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
            case APPMSG:
                this.replyTextMsg(chatMessage);
                break;
            case TAKESHOT:
                this.replyTextMsg(chatMessage);
                break;
            default:
                break;
        }
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
        String thumbUrl = "http://" + botconfig.getLocalhostIp() + ":" + 9919 + "/" + thumbPath;
        MessageApi.postVideo(chatMessage.getAppId(), chatMessage.getFromUserId(), videoUrl, thumbUrl, (Integer) map.get("videoDuration"));
        chatMessage.setPrepared(true);
        thumbPath.toFile().deleteOnExit();

    }

    @Override
    public void replyFileMsg(ChatMessage chatMessage) {

    }

    @Override
    public void replyAudioMsg(ChatMessage chatMessage) {

        String replayMsg = aiService.textToText(chatMessage.getSession());
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path audioPath = Path.of("data", "audio", date, UUID.randomUUID().toString().concat(".pcm"));
        audioPath.getParent().toFile().mkdirs();
        Integer voiceDuration = aiService.textToVoice(replayMsg, audioPath.toString());

        // 替换文件后缀从 .pcm 到 .silk
        Path silkPath = audioPath.resolveSibling(audioPath.getFileName().toString().replace(".pcm", ".silk"));

        //  实现将 .pcm 文件转换为 .silk 文件的逻辑
        // 参考方案：https://github.com/kn007/silk-v3-decoder/tree/master
        AudioFormatConversionSilk.convertToAudioFormat(audioPath.toString(), silkPath.toString());

        String voiceUrl = "http://" + botconfig.getLocalhostIp() + ":" + 9919 + "/" + silkPath;

        MessageApi.postVoice(chatMessage.getAppId(), chatMessage.getFromUserId(), voiceUrl, voiceDuration);
        chatMessage.setPrepared(true);
        audioPath.toFile().deleteOnExit();
        silkPath.toFile().deleteOnExit();


    }

    @Override
    public void replyLocationMsg(ChatMessage chatMessage) {

    }

    @Override
    public void replyLinkMsg(ChatMessage chatMessage) {

    }

    @Override
    public void imageRecognition(ChatMessage chatMessage) {

        String s = aiService.imageToText(chatMessage.getSession());
        log.info("图片识别成功，图片内容：{}", s);
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), s, chatMessage.getToUserId());
        chatMessage.setPrepared(true);
    }

    @Override
    public void replayQuoteMsg(ChatMessage chatMessage) {

        // 构建引用消息XML
        String appMsg = String.format(
                "<appmsg appid=\"\" sdkver=\"0\">" +
                        " <title>%s</title>\n" +
                        "    <des />\n" +
                        "    <action />\n" +
                        "    <type>57</type>\n" +
                        "    <showtype>0</showtype>\n" +
                        "    <soundtype>0</soundtype>\n" +
                        "    <mediatagname />\n" +
                        "    <messageext />\n" +
                        "    <messageaction />\n" +
                        "    <content />\n" +
                        "    <contentattr>0</contentattr>\n" +
                        "    <url />\n" +
                        "    <lowurl />\n" +
                        "    <dataurl />\n" +
                        "    <lowdataurl />\n" +
                        "    <songalbumurl />\n" +
                        "    <songlyric />\n" +
                        "    <appattach>\n" +
                        "      <totallen>0</totallen>\n" +
                        "      <attachid />\n" +
                        "      <emoticonmd5 />\n" +
                        "      <fileext />\n" +
                        "      <aeskey />\n" +
                        "    </appattach>\n" +
                        "    <extinfo />\n" +
                        "    <sourceusername />\n" +
                        "    <sourcedisplayname />\n" +
                        "    <thumburl />\n" +
                        "    <md5 />\n" +
                        "    <statextstr />\n" +
                        "    <refermsg>\n" +
                        "      <type>%d</type>\n" +
                        "      <svrid>%s</svrid>\n" +
                        "      <fromusr>%s</fromusr>\n" +
                        "      <chatusr>%s</chatusr>\n" +
                        "      <displayname />\n" +
                        "      <content>%s</content>\n" +
                        "    </refermsg>\n" +
                        "  </appmsg>`", escapeHtml(chatMessage.getReplayContent()), chatMessage.getCtype().getMsgType(), chatMessage.getMsgId(), chatMessage.getFromUserId(), chatMessage.getFromUserId(), escapeHtml(chatMessage.getContent()));

        MessageApi.postAppMsg(chatMessage.getAppId(), chatMessage.getFromUserId(), appMsg);
        chatMessage.setPrepared(true);
    }

    @Override
    public void replayAitMsg(ChatMessage chatMessage) {
        // 群聊必须增加@,这样子可以很好的区分每个人聊天
        // 判断消息类型是否是群聊消息
        if (!(chatMessage.getIsGroup() && chatMessage.getIsAt())) {
            return;
        }
        //拼接艾特类型的消息
        String replayMsg = "@" + chatMessage.getGroupMemberUserNickname() + " " + chatMessage.getReplayContent();
        // 发送文本消息
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), replayMsg, chatMessage.getGroupMembersUserId());
        chatMessage.setPrepared(true);


    }

    public String escapeHtml(String input) {

        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}
