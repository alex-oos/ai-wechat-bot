package com.wechat.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

/**
 * @author Alex
 * @since 2025/3/13 20:01
 * <p></p>
 */

@Slf4j
public class ImageUtil {

    /**
     * 从请求地址，提取图片，转成base64字符串
     *
     * @param imageUrl
     * @return
     * @throws IOException
     */
    public static String convertImageToBase64(String imageUrl) throws IOException {

        URL url = new URL(imageUrl);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (InputStream inputStream = url.openStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static String downloadImageFromBase64(String imageUrl) {

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

    /**
     * 从URL下载图片并保存到本地
     *
     * @param imageUrl  图片的URL地址
     * @param imagePath 保存图片的本地路径
     * @throws IOException 如果下载或保存过程中发生错误
     */
    public static void downloadImage(String imageUrl, String imagePath) {

        try {
            URL url = new URL(imageUrl);

            try (InputStream inputStream = url.openStream()) {
                // 将输入流中的数据复制到本地文件
                Files.copy(inputStream, Paths.get(imagePath), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            log.error("图片转化失败,url地址为：{},本地路径为：{}，异常信息为：{}", imageUrl, imagePath, e.getMessage());
            throw new RuntimeException("图片转化异常", e);
        }
    }


}
