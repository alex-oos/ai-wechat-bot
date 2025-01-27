package com.wechat.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Alex
 * @since 2025/1/26 23:20
 * <p></p>
 */
public class IpUtil {

    public static String getIp() {

        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("本地ip获取失败！");
        }
        return ip;
    }

}
