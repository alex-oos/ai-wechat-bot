package com.wechat.knowledgebase.ragflow.service.impl;

import com.alibaba.dashscope.common.Message;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.session.Session;
import com.wechat.knowledgebase.ragflow.config.RagFlowConfig;
import com.wechat.knowledgebase.ragflow.contant.RagFlowURI;
import com.wechat.knowledgebase.ragflow.entity.ReplyEntity;
import com.wechat.knowledgebase.ragflow.service.RagFlowService;
import com.wechat.util.OkHttpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/4/14 18:03
 * <p></p>
 */
@Service
public class RagFlowServiceImpl implements RagFlowService {

    @Resource
    RagFlowConfig raptFlowConfig;

    @Override
    public ReplyEntity chatWithAssistant(String content, String sessionId) {

        JSONObject body = new JSONObject();
        body.put("question", content);
        body.put("stream", false);
        body.put("session_id", sessionId);
        String uri = RagFlowURI.CHAT_ASSISTANT_URI.replaceAll("chat_id", raptFlowConfig.getRebootId());
        JSONObject response = sendRequest(uri, body);
        try {
            // 1. 解析外层JSON

            // 2. 获取并处理data字符串
            String innerJson = response.getString("data").replaceFirst("^data:", "")  // 移除前缀
                    .trim();                     // 清理空白

            // 3. 解析内层JSON
            JSONObject inner = JSON.parseObject(innerJson);

            // 4. 获取目标数据对象
            JSONObject targetData = inner.getJSONObject("data");

            // 5. 直接提取字段
            String answer = targetData.getString("answer");
            if (sessionId == null) {
                sessionId = targetData.getString("session_id");
            }

            return ReplyEntity.builder().answer(answer).sessionId(sessionId).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String createSession(String url) {

        return "";
    }

    @Override
    public String createRagflowChat(Session session) {

        String sessionId = session.getUserId();
        Message message = session.getTextMessages().get(session.getTextMessages().size() - 1);
        String content = message.getContent();
        ReplyEntity replyEntity = null;
        // 小于32 代表不是真正的seesionId
        if (sessionId.length() < 32) {
            replyEntity = this.chatWithAssistant(content, null);
            session.setUserId(replyEntity.getSessionId());
        } else {
            replyEntity = this.chatWithAssistant(content, sessionId);

        }

        return replyEntity.getAnswer();
    }

    public JSONObject sendRequest(String uri, JSONObject body) {

        String responseStr = OkHttpUtil.builder().url(raptFlowConfig.getHost() + uri).addHeader("Content-Type", "application/json").addHeader("Authorization", "Bearer " + raptFlowConfig.getApiKey()).post(body).async();
        JSONObject response = JSONObject.parseObject(responseStr);
        if (response.getInteger("code") != 0) {
            throw new RuntimeException("请求ragflow 失败，请检查服务可用性！" + response.toJSONString());
        }
        return response;
    }

}
