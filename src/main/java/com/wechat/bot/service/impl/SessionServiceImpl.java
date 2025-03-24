package com.wechat.bot.service.impl;

import com.wechat.ai.session.SessionManager;
import com.wechat.bot.service.SessionService;
import org.springframework.stereotype.Service;

/**
 * @author Alex
 * @since 2025/3/24 17:05
 * <p></p>
 */
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionManager persionSessionManager = new SessionManager();

    private final SessionManager groupSessionManager = new SessionManager();

    @Override
    public SessionManager getPersionSessionManager() {

        return persionSessionManager;
    }

    @Override
    public SessionManager getGroupSessionManager() {

        return groupSessionManager;
    }

}
