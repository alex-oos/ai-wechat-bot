package com.wechat.bot.service;

import com.wechat.ai.session.SessionManager;

/**
 * @author Alex
 * @since 2025/3/24 17:04
 * <p></p>
 */
public interface SessionService {

    SessionManager getPersionSessionManager();

    SessionManager getGroupSessionManager();

}
