package com.wechat.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author Alex
 * @since 2025/3/18 10:20
 * <p></p>
 */
@Slf4j
public class OkHttpUtil {

    private static volatile OkHttpClient okHttpClient = null;

    private static volatile Semaphore semaphore = null;

    private Map<String, String> headerMap;

    private Map<String, String> paramMap;

    private String url;

    private Request.Builder request;

    /**
     * 初始化okHttpClient，并且允许https访问
     */
    private OkHttpUtil() {

        if (okHttpClient == null) {
            synchronized (OkHttpUtil.class) {
                if (okHttpClient == null) {
                    TrustManager[] trustManagers = buildTrustManagers();
                    okHttpClient = new OkHttpClient.Builder()
                            // 设置连接超时时间
                            .connectTimeout(60 * 5, TimeUnit.SECONDS)
                            // 写入超时时间
                            .writeTimeout(60 * 5, TimeUnit.SECONDS)
                            // 从连接成功到响应的总时间
                            .readTimeout(60 * 5, TimeUnit.SECONDS)
                            // 跳过ssl认证(https)
                            .sslSocketFactory(createSSLSocketFactory(trustManagers), (X509TrustManager) trustManagers[0])
                            .hostnameVerifier((hostName, session) -> true)
                            .retryOnConnectionFailure(true)
                            // 设置连接池最大连接数量, 持续存活的连接
                            .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
                            .build();
                    addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
                }
            }
        }
    }

    /**
     * 用于异步请求时，控制访问线程数，返回结果
     */
    private static Semaphore getSemaphoreInstance() {
        // 只能1个线程同时访问
        synchronized (OkHttpUtil.class) {
            if (semaphore == null) {
                semaphore = new Semaphore(0);
            }
        }
        return semaphore;
    }

    /**
     * 创建OkHttpUtil
     */
    public static OkHttpUtil builder() {

        return new OkHttpUtil();
    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     */
    private static SSLSocketFactory createSSLSocketFactory(TrustManager[] trustAllCerts) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            log.error("SSLContext initialization failed", e);
        }
        return ssfFactory;
    }

    private static TrustManager[] buildTrustManagers() {

        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {

                        return new X509Certificate[]{};
                    }
                }
        };
    }

    /**
     * 添加url
     */
    public OkHttpUtil url(String url) {

        this.url = url;
        return this;
    }

    /**
     * 添加参数
     */
    public OkHttpUtil addParam(String key, String value) {

        if (paramMap == null) {
            paramMap = new LinkedHashMap<>(16);
        }
        paramMap.put(key, value);
        return this;
    }

    /**
     * 添加请求头
     */
    public OkHttpUtil addHeader(String key, String value) {

        if (headerMap == null) {
            headerMap = new LinkedHashMap<>(16);
        }
        headerMap.put(key, value);
        return this;
    }

    /**
     * 初始化get方法
     */
    public OkHttpUtil get() {
        request = new Request.Builder().get();
        StringBuilder urlBuilder = new StringBuilder(url);
        if (paramMap != null) {
            urlBuilder.append("?");
            try {
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                            .append("&");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1);
            } catch (Exception e) {
                log.error("Error encoding URL parameters for URL: {}", url, e);
                throw new RuntimeException(e);
            }
        }
        request.url(urlBuilder.toString());
        return this;
    }

    /**
     * 初始化post方法
     *
     * @param isJsonPost true等于json的方式提交数据，类似postman里post方法的raw
     *                   false等于普通的表单提交
     */
    public OkHttpUtil post(boolean isJsonPost) {

        RequestBody requestBody;
        if (isJsonPost) {
            String json = "";
            if (paramMap != null) {
                json = JSONObject.toJSONString(paramMap);
            }
            requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        } else {
            FormBody.Builder formBody = new FormBody.Builder();
            if (paramMap != null) {
                paramMap.forEach(formBody::add);
            }
            requestBody = formBody.build();
        }
        request = new Request.Builder().post(requestBody).url(url);
        return this;
    }

    public OkHttpUtil post(JSONArray param) {

        RequestBody requestBody;
        String json = JSONObject.toJSONString(param);
        requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        request = new Request.Builder().post(requestBody).url(url);
        return this;
    }

    public OkHttpUtil post(JSONObject param) {

        RequestBody requestBody;
        String json = JSONObject.toJSONString(param);
        requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        request = new Request.Builder().post(requestBody).url(url);
        return this;
    }

    public OkHttpUtil put() {

        String json = "";
        if (paramMap != null) {
            json = JSONObject.toJSONString(paramMap);
        }
        request = new Request.Builder().put(
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        ).url(url);
        return this;
    }

    public OkHttpUtil del() {

        String json = "";
        if (paramMap != null) {
            json = JSONObject.toJSONString(paramMap);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        request = new Request.Builder().delete(requestBody).url(url);
        return this;
    }

    /**
     * 同步请求
     */
    public String sync() {
        setHeader(request);
        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            if (response.body() != null) {
                return response.body().string();
            } else {
                log.error("Response body is null for URL: {}", url);
                return "请求失败: Response body is null";
            }
        } catch (IOException e) {
            log.error("请求发生异常", e);
            return "请求失败: " + e.getMessage();
        }
    }

    /**
     * 异步请求，有返回值
     */
    public String async() {
        StringBuilder responseResult = new StringBuilder();
        setHeader(request);
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseResult.append("请求出错：").append(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.body() != null) {
                    responseResult.append(response.body().string());
                } else {
                    log.error("Response body is null for URL: {}", url);
                    responseResult.append("请求失败: Response body is null");
                }
                getSemaphoreInstance().release();
            }
        });
        try {
            getSemaphoreInstance().acquire();
        } catch (InterruptedException e) {
            log.error("acquire异常", e);
        }
        return responseResult.toString();
    }

    /**
     * 异步请求，带有接口回调
     */
    public void async(ICallBack callBack) {

        setHeader(request);
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                callBack.onFailure(call, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                assert response.body() != null;
                callBack.onSuccessful(call, response.body().string());
            }
        });
    }

    /**
     * 为request添加请求头
     */
    private void setHeader(Request.Builder request) {

        if (headerMap != null) {
            try {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 自定义一个接口回调
     */
    public interface ICallBack {

        void onSuccessful(Call call, String data);

        void onFailure(Call call, String errorMsg);

    }

}
