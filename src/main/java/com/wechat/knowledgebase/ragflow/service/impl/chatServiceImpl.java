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
        String responseStr = OkHttpUtil.builder().url(raptFlowConfig.getHost() + RagFlowURI.CHAT_ASSISTANT_URI)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + raptFlowConfig.getApiKey())
                .post(body).async();
        JSONObject response = JSONObject.parseObject(responseStr);
        JSONObject data = response.getJSONObject("data");
        if (data.getInteger("code") != 0) {
            throw new RuntimeException("获取答案失败" + response.toJSONString());
        }
        return data.getString("answer");
    }

}
