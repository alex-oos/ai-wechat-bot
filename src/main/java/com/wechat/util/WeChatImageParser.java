package com.wechat.util;

/**
 * @author Alex
 * @since 2025/3/12 21:15
 * <p></p>
 */

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class WeChatImageParser {

    public static void main(String[] args) throws Exception {
        String xml =  "<?xml version=\"1.0\"?>\n<msg>\n\t<img aeskey=\"9b7011c38d1af088f579eda23e3b9cad\" encryver=\"1\" cdnthumbaeskey=\"9b7011c38d1af088f579eda23e3b9cad\" cdnthumburl=\"3057020100044b304902010002043904752002032f7e350204aa0dd83a020465a0e6de042438323365313535662d373035372d343264632d383132302d3861323332316131646334660204011418020201000405004c4ec500\" cdnthumblength=\"2146\" cdnthumbheight=\"76\" cdnthumbwidth=\"120\" cdnmidheight=\"0\" cdnmidwidth=\"0\" cdnhdheight=\"0\" cdnhdwidth=\"0\" cdnmidimgurl=\"3057020100044b304902010002043904752002032f7e350204aa0dd83a020465a0e6de042438323365313535662d373035372d343264632d383132302d3861323332316131646334660204011418020201000405004c4ec500\" length=\"2998\" md5=\"2a4cb28868b9d450a135b1a85b5ba3dd\" />\n\t<platform_signature></platform_signature>\n\t<imgdatahash></imgdatahash>\n</msg>\n";

        // 1. 解析 XML
        Element imgElement = parseXml(xml);
        String aesKeyHex = imgElement.getAttribute("aeskey");
        String cdnUrlHex = imgElement.getAttribute("cdnthumburl");
        String md5Expected = imgElement.getAttribute("md5");

        // 2. 解码 CDN URL（十六进制转字节）
        byte[] cdnUrlBytes = hexStringToByteArray(cdnUrlHex);
        String cdnUrl = new String(cdnUrlBytes, "UTF-8"); // 根据实际编码调整

        // 3. 下载加密图片
        byte[] encryptedData = downloadFromUrl(cdnUrl);

        // 4. AES 解密
        byte[] aesKey = hexStringToByteArray(aesKeyHex);
        byte[] decryptedData = decryptAesCbc(encryptedData, aesKey);

        // 5. 校验 MD5
        String md5Actual = bytesToHex(MessageDigest.getInstance("MD5").digest(decryptedData));
        if (!md5Expected.equals(md5Actual)) {
            throw new RuntimeException("MD5 校验失败");
        }

        // 6. 写入文件
        Files.write(Paths.get("decrypted_image.jpg"), decryptedData);
        System.out.println("图片已保存至 decrypted_image.jpg");
    }

    // XML 解析
    private static Element parseXml(String xml) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        return (Element) doc.getElementsByTagName("img").item(0);
    }

    // 十六进制字符串转字节数组
    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("十六进制字符串长度必须为偶数");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("包含无效的十六进制字符: " + hex);
            }
            data[i / 2] = (byte) ((high << 4) + low);
        }
        return data;
    }

    // 字节数组转十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    // 下载文件
    private static byte[] downloadFromUrl(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream()) {
            return in.readAllBytes();
        }
    }

    // AES-CBC 解密
    private static byte[] decryptAesCbc(byte[] data, byte[] key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(key, 0, 16); // 使用前16字节作为IV

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(data);
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
}
