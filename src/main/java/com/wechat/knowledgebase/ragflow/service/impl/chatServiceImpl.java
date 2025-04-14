package com.wechat.knowledgebase.ragflow.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.knowledgebase.ragflow.config.RagFlowConfig;
import com.wechat.knowledgebase.ragflow.contant.RagFlowURI;
import com.wechat.knowledgebase.ragflow.service.ChatService;
import com.wechat.util.OkHttpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/4/14 18:03
 * <p></p>
 */
@Service
public class chatServiceImpl implements ChatService {

    @Resource
    RagFlowConfig raptFlowConfig;

    @Override
    public String chatWithAssistant(String content, String sessionId) {

        JSONObject body = new JSONObject();
        body.put("question", content);
        body.put("stream", false);
        body.put("session_id", sessionId);
        body.put("user_id", "1");
        JSONObject response = sendRequest(RagFlowURI.CHAT_ASSISTANT_URI, body);
        JSONObject data = response.getJSONObject("data");
        return data.getString("answer");
    }

    @Override
    public String createSession(String url) {

        return "";
    }

    public JSONObject sendRequest(String uri, JSONObject body) {

        String responseStr = OkHttpUtil.builder().url(raptFlowConfig.getHost() + uri)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + raptFlowConfig.getApiKey())
                .post(body).async();
        JSONObject response = JSONObject.parseObject(responseStr);
        if (response.getInteger("code") != 0) {
            throw new RuntimeException("请求ragflow 失败，请检查服务可用性！" + response.toJSONString());
        }
        return response;
    }

}
