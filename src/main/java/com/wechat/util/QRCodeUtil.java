package com.wechat.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex
 * @since 2025/1/24 15:04
 * <p>
 *     二维码生成工具
 * </p>
 */
public class QRCodeUtil {

    public static void generateQRCodeBase64(String url, int width, int height) {

        try {
            // 生成位矩阵
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, "H"); // 高容错率
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    url, BarcodeFormat.QR_CODE, width, height, hints
            );
            // 转换为 ASCII 并打印
            printQRCodeToTerminal(bitMatrix, true); // invert=true 适配深色背景终端

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将 BitMatrix 转换为终端可打印的 ASCII 字符
     *
     * @param bitMatrix 二维码位矩阵
     * @param invert    是否反转颜色（深色背景建议设为 true）
     */
    public static void printQRCodeToTerminal(BitMatrix bitMatrix, boolean invert) {
        // 定义字符映射（黑色区域用 ██，白色区域用空格）
        String black = "██";
        String white = "  ";
        if (invert) {
            // 反转颜色（深色背景终端更清晰）
            String temp = black;
            black = white;
            white = temp;
        }

        // 遍历每个像素点并转换为字符
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                boolean isBlack = bitMatrix.get(x, y);
                line.append(isBlack ? black : white);
            }
            System.out.println(line);
        }
    }

/*     public static void main(String[] args) {
        String url ="http://weixin.qq.com/x/A6a8tkeZJYxoFh5uIlah";
        generateQRCodeBase64(url, 1, 1);
    } */

}
