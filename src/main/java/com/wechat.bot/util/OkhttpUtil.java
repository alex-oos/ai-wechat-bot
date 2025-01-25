package com.wechat.bot.util;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.config.UserInfoConfig;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkhttpUtil {

    // docker 容器的ip地址，也可以使用其他的，写一个默认的今后再也不用修改了
    //private final static String baseUrl = "http://172.17.0.3:2531/v2/api";
    //默认地址，host.docker.internal
    private final static String baseUrl = "http://host.docker.internal:2531/v2/api";
    private static String token = "";

    public static OkHttpClient okHttpClient() {

        TrustManager[] trustManagers = buildTrustManagers();

        return new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).sslSocketFactory(createSSLSocketFactory(trustManagers), (X509TrustManager) trustManagers[0]).hostnameVerifier((hostName, sessino) -> true).retryOnConnectionFailure(false)//是否开启缓存
                .build();
    }

    private static TrustManager[] buildTrustManagers() {

        return new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {

                return new X509Certificate[]{};
            }
        }};
    }

    private static SSLSocketFactory createSSLSocketFactory(TrustManager[] trustAllCerts) {

        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    public static JSONObject postJSON(String route, JSONObject param) {

        //读取文件，看看文件中是否有值，如果文件中有值，直接覆盖
        if (token == null) {
            UserInfoConfig userInfoConfig = FileUtil.readFile("src/main/resources/static/config.json");
            String token1 = userInfoConfig.getToken();
            if (!token1.isEmpty()) {
                token = token1;
            }
        }

        Map<String, Object> header = new HashMap<>();//if (UserInfoConfig.TOKEN!=null){
        if (token != null) {
            header.put("X-GEWE-TOKEN", token);
        }
        try {
            if (baseUrl == null || "".equals(baseUrl)) {
                throw new RuntimeException("baseUrl 未配置");
            }
            String res = json(baseUrl + route, header, param.toJSONString(), okHttpClient());
            System.out.println(res);
            JSONObject jsonObject = JSONObject.parse(res);
            if (jsonObject.getInteger("ret") == 200) {
                return jsonObject;
            } else {
                throw new RuntimeException(res);
            }
        } catch (Exception e) {
            System.out.println("url=" + baseUrl + route);
            throw new RuntimeException(e);
        }
    }

    private static String json(String url, Map<String, Object> header, String json, OkHttpClient client) throws IOException {
        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        Iterator<Map.Entry<String, Object>> headerIterator = header.entrySet().iterator();
        headerIterator.forEachRemaining(e -> {
            headerBuilder.add(e.getKey(), (String) e.getValue());
        });
        headerBuilder.add("Content-Type", "application/json");

        // application/octet-stream
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), json);

        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(requestBody);

        try (Response execute = client.newCall(builder.build()).execute()) {
            return execute.body().string();
        }
    }

}
