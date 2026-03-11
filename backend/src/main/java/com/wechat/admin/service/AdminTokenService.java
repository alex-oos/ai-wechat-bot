package com.wechat.admin.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<String, Long> tokenToExpireAtEpochSeconds = new ConcurrentHashMap<>();

    public String issueToken(long ttlSeconds) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        long expireAt = Instant.now().getEpochSecond() + Math.max(60, ttlSeconds);
        tokenToExpireAtEpochSeconds.put(token, expireAt);
        return token;
    }

    public boolean isValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Long expireAt = tokenToExpireAtEpochSeconds.get(token);
        if (expireAt == null) {
            return false;
        }
        if (Instant.now().getEpochSecond() >= expireAt) {
            tokenToExpireAtEpochSeconds.remove(token);
            return false;
        }
        return true;
    }

    public Optional<Long> getExpireAt(String token) {
        return Optional.ofNullable(tokenToExpireAtEpochSeconds.get(token));
    }
}

