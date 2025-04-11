package com.wechat.search.serivce;

import com.alibaba.dashscope.common.Message;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.search.config.AliSearchConfig;
import com.wechat.util.OkHttpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Alex
 * @since 2025/4/11 10:05
 * <p>
 * 接口文档：https://opensearch.console.aliyun.com/cn-shanghai/rag/experience-center?serverType=document-analyze
 * 阿里云AI搜索开放平台
 * </p>
 */
@Service
public class AliAiSearchService {

    @Resource
    private AliSearchConfig aliSearchConfig;

    //public static void main(String[] args) {
    //
    //    AliAiSearchService aliAiSearchService = new AliAiSearchService();
    //    ArrayList<Message> messages = new ArrayList<>();
    //    messages.add(Message.builder().role("user").content("你好").build());
    //    aliAiSearchService.searchAndAI(messages);
    //}

    /**
     * 内容生成服务
     * https://help.aliyun.com/zh/open-search/search-platform/developer-reference/text-generation-api-details?spm=a2c4g.11186623.help-menu-29102.d_3_1_7.3550e507RHtX9E
     *
     * @param messages
     */
    public String searchAndAI(List<Message> messages) {

        String url = aliSearchConfig.getHost().concat("/v3/openapi/workspaces/default/text-generation/deepseek-v3");
        JSONObject body = new JSONObject();
        body.put("messages", messages);
        JSONObject parameters = new JSONObject();
        parameters.put("search_return_result", true);
        parameters.put("search_way", "fast");
        parameters.put("search_top_k", 1);
        body.put("parameters", parameters);
        body.put("enable_search", true);
        body.put("Stream", false);
        String responseStr = OkHttpUtil.builder().addHeader("Authorization", "Bearer " + aliSearchConfig.getApiKey()).url(url).post(body).async();
        JSONObject response = JSONObject.parseObject(responseStr);
        if (response.getDouble("latency") == 0) {
            throw new RuntimeException("搜索失败" + response.toJSONString());
        }
        String repalyContent = response.getJSONObject("result").getString("text");
        messages.add(Message.builder().role("assistant").content(repalyContent).build());
        return repalyContent;
    }

    /**
     * 联网搜索
     * https://help.aliyun.com/zh/open-search/search-platform/developer-reference/web-search?spm=a2c4g.11186623.help-menu-29102.d_3_1_10.3ecb5553ZqLXUK&scm=20140722.H_2873858._.OR_help-T_cn~zh-V_1
     *
     * @param query
     * @param messages
     * @return
     */
    public JSONObject search(String query, List<Message> messages) {

        String url = aliSearchConfig.getHost().concat("/v3/openapi/workspaces/default/web-search/ops-web-search-001");
        JSONObject body = new JSONObject();
        body.put("query", query);
        body.put("way", "normal");
        body.put("top_k", 1);
        body.put("history", messages);
        String responseStr = OkHttpUtil.builder().addHeader("Authorization", "Bearer " + aliSearchConfig.getApiKey()).url(url).post(body).async();
        JSONObject response = JSONObject.parseObject(responseStr);
        if (response.getInteger("code") != 200) {
            throw new RuntimeException("搜索失败" + response.toJSONString());
        }
        return response;
    }

}
