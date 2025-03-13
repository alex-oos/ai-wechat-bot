package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.MessageService;
import com.wechat.bot.service.MsgSourceService;
import com.wechat.gewechat.service.ContactApi;
import com.wechat.gewechat.service.DownloadApi;
import com.wechat.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alex
 * @since 2025/1/26 20:00
 * <p></p>
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    Map<String, String> contactMap = new ConcurrentHashMap<>();

    @Resource
    private MsgSourceService msgSourceService;

    @Resource
    private BotConfig botConfig;

    //@Async
    @Override
    public void receiveMsg(JSONObject requestBody) {

        String appid = requestBody.getString("Appid");
        String wxid = requestBody.getString("Wxid");
        JSONObject data = requestBody.getJSONObject("Data");
        String fromUserName = data.getJSONObject("FromUserName").getString("string");
        String toUserName = data.getJSONObject("ToUserName").getString("string");
        String content = data.getJSONObject("Content").getString("string");
        String msgSource = data.getString("MsgSource");
        String msgId = data.getString("'NewMsgId'");
        // 消息类型
        Integer msgType = data.getInteger("MsgType");
        ChatMessage chatMessage = ChatMessage.builder()
                .msgId(msgId)
                .createTime(data.getLong("CreateTime"))
                .ctype(MsgTypeEnum.getMsgTypeEnum(msgType))
                .content(content)
                .fromUserId(fromUserName)
                .toUserId(toUserName)
                .isMyMsg(wxid.equals(fromUserName))
                .isGroup(fromUserName.contains("@chatroom"))
                .isAt(content.contains("@"))
                .actualUserId(wxid)
                .appId(appid)
                .rawMsg(requestBody)
                .build();

        // 过滤掉非用户信息
        Boolean isFilter = this.filterNotUserMessage(chatMessage, msgSource);
        if (isFilter) {
            return;
        }
        //TODO 先过滤掉所有的群消息,等个人开发完毕之后，再去处理群消息
        if (chatMessage.getIsGroup()) {
            //log.info("收到群消息");
            return;
        }
        // 消息内容进行处理
        this.messageContentProcessing(chatMessage);
        if (!contactMap.containsKey(chatMessage.getFromUserId())) {
            // 存到一个map里面不用每次都重新获取，降低请求次数
            // 获取好友的信息
            String nickName = null;
            JSONObject briefInfo = ContactApi.getBriefInfo(appid, Collections.singletonList(chatMessage.getFromUserId()));
            if (briefInfo.getInteger("ret") == 200) {
                JSONArray dataList = briefInfo.getJSONArray("data");
                JSONObject userInfo = dataList.getJSONObject(0);
                String remark = userInfo.getString("remark");
                nickName = remark != null ? remark : userInfo.getString("nickName");
            }
            contactMap.put(chatMessage.getFromUserId(), nickName);
        }
        chatMessage.setFromUserNickname(contactMap.get(chatMessage.getFromUserId()));
        if (chatMessage.getIsGroup()) {
            log.info("收到群消息");
            return;
            //msgSourceService.groupMsg(chatMessage);
        } else {
            log.info("收到个人消息：来自：{}，消息内容为：{}", chatMessage.getFromUserNickname(), chatMessage.getContent());
            msgSourceService.personalMsg(chatMessage);
        }


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
     * @return true代表是过滤的消息，false代表不是过滤的消息
     */
    public Boolean filterNotUserMessage(ChatMessage chatMessage, String msgSource) {

        // 防止给自己发消息
        if (chatMessage.getIsMyMsg()) {
            return true;
        }
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, "Tencent-Games", "weixin");
        //过滤公众号与腾讯团队
        if (list1.contains(chatMessage.getFromUserId()) || chatMessage.getFromUserId().startsWith("gh_")) {
            return true;
        }
        // 添加过滤标签
        List<String> list = new ArrayList<>();
        Collections.addAll(list, "<tips>3</tips>", "<bizmsgshowtype>", "</bizmsgshowtype>", "<bizmsgfromuser>", "</bizmsgfromuser>");
        if (list.contains(msgSource)) {
            return true;
        }

        // 过滤掉5分钟前的消息
        if (chatMessage.getCreateTime() - (System.currentTimeMillis() / 1000) > 60 * 5) {
            return true;
        }
        return false;


    }

    @Override
    public Boolean filterErrorMessage(String requestBody) {
        //gewechat服务发送的回调测试消息
        JSONObject response = JSONObject.parseObject(requestBody);
        if (response.containsKey("testMsg") && response.containsKey("token")) {
            return true;
        }
        // 过滤掉不包含Data字段的信息
        if (!response.containsKey("Data")) {
            return true;
        }
        // 过滤掉不包含NewMsgId字段的信息
        if (!response.getJSONObject("Data").containsKey("NewMsgId")) {
            return true;
        }
        return false;
    }

    /**
     * 消息内容进行一系列的处理，处理逻辑比较复杂
     *
     * @param chatMessage
     */
    private void messageContentProcessing(ChatMessage chatMessage) {
        //判断消息类型，进行一系列的操作
        switch (chatMessage.getCtype()) {
            case TEXT:
                // 个人的文本消息，进行一系列的处理，群的文本消息是否需要处理，这里想想后面如何校验一下
                String content = chatMessage.getContent();
                List<String> imageCreatePrefix = botConfig.getImageCreatePrefix();
                for (String createPrefix : imageCreatePrefix) {
                    if (content.contains(createPrefix)) {
                        chatMessage.setCtype(MsgTypeEnum.IMAGE);
                        return;
                    }
                }
                if (content.contains("视频") && content.contains("生成")) {
                    chatMessage.setCtype(MsgTypeEnum.VIDEO);
                    return;
                }
                if (content.contains("语音") && content.contains("生成")) {
                    chatMessage.setCtype(MsgTypeEnum.VOICE);
                    return;
                }
                break;
            case IMAGE:
                // 图片下载处理为base64位
                JSONObject jsonObject = DownloadApi.downloadImage(chatMessage.getAppId(), chatMessage.getContent(), 2);
                if (jsonObject.getInteger("ret") != 200) {
                    break;
                }
                String imagePath = jsonObject.getJSONObject("data").getString("fileUrl");
                imagePath = "http://" + IpUtil.getIp() + ":2532/download/" + imagePath;
                String base64Image = download(imagePath);
                // 图片下载可能会出现下载失败，而报错，请检查一下你的容器，容器内是否有问题
                chatMessage.setContent(base64Image);
                break;
            case VOICE:
                break;
            case LINK:
                break;
            case VIDEO:
                break;
            default:
                break;

        }


    }

    private String download(String imageUrl) {

        try {

            // 创建连接
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // 下载图片
            BufferedImage originalImage = ImageIO.read(connection.getInputStream());
            if (originalImage == null) {
                throw new IOException("Failed to read image from URL");
            }

            // 创建一个新的RGB格式的BufferedImage
            BufferedImage newImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // 创建Graphics2D对象并设置背景色
            Graphics2D g2d = newImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());

            // 将原图绘制到新图上
            g2d.drawImage(originalImage, 0, 0, null);
            g2d.dispose();

            // 转换为JPEG格式
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 使用"jpg"而不是"JPEG"
            boolean success = ImageIO.write(newImage, "jpg", baos);


            if (!success) {
                throw new IOException("Failed to convert image to JPEG format");
            }

            baos.flush();
            byte[] imageBytes = baos.toByteArray();

            // 检查图片字节数组
            if (imageBytes == null || imageBytes.length == 0) {
                throw new IOException("Image bytes are empty");
            }


            // 获取MIME类型
            String mimeType = "image/jpeg";

            // 转换为Base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 关闭资源
            baos.close();

            //return MessageImg.builder()
            //        .mimeType(mimeType)
            //        .base64String(base64Image)
            //        .build();
            return base64Image;

        } catch (Exception e) {
            log.error("图片下载失败,地址为：{},异常信息为：{}", imageUrl, e.getMessage());
            throw new RuntimeException(String.format("图片下载失败，地址为：%s", imageUrl), e);
        }
    }


}
