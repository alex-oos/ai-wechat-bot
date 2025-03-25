package com.wechat.bot.service.impl;

import com.wechat.ai.entity.SystemPromptEntity;
import com.wechat.ai.session.Session;
import com.wechat.ai.session.SessionManager;
import com.wechat.bot.service.AiSystemRoleService;
import com.wechat.bot.service.SessionService;
import com.wechat.util.FileUtil;
import com.wechat.util.WordParticipleMatch;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Alex
 * @since 2025/3/25 23:34
 * <p></p>
 */
@Service
public class AiSystemRoleServiceImpl implements AiSystemRoleService {

    @Resource
    private SessionService sessionService;

    public static List<SystemPromptEntity> systemPromptList = null;

    private SessionManager persionSessionManager = null;

    SessionManager groupSessionManager = null;

    static {
        systemPromptList = FileUtil.readSystemPrompt();
    }

    @Override
    public void updatePersonSystemRole(String userId, String userName) {

        persionSessionManager = sessionService.getPersionSessionManager();
        Session session = persionSessionManager.getSession(userId);
        String systemPrompt = systemPromptList.stream().filter(systemPromptEntity -> systemPromptEntity.getUserName().equals(userName)).map(SystemPromptEntity::getSystemPrompt).findFirst().orElseThrow(() -> new RuntimeException("系统角色不存在"));
        if (session == null) {
            persionSessionManager.createSession(userId, systemPrompt);
            return;
        }
        session.setSystemPrompt(systemPrompt);

    }

    @Override
    public void updateGroupSystemRole(String userId, String userName) {

        groupSessionManager = sessionService.getGroupSessionManager();
        Session session = groupSessionManager.getSession(userId);
        String systemPrompt = systemPromptList.stream().filter(systemPromptEntity -> systemPromptEntity.getUserName().equals(userName)).map(SystemPromptEntity::getSystemPrompt).findFirst().orElseThrow(() -> new RuntimeException("系统角色不存在"));
        if (session == null) {
            groupSessionManager.createSession(userId, systemPrompt);
            return;
        }
        session.setSystemPrompt(systemPrompt);
    }

    @Override
    public Boolean isUpdateAiSystemRole(String content) {

        boolean isVideoType = WordParticipleMatch.containsPartKeywords(content, List.of("角色", "切换"), 2);
        //updatePersonSystemRole();
        return null;
    }

}
