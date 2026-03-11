package com.wechat.admin.controller;

import com.wechat.admin.config.AdminAuthProperties;
import com.wechat.admin.service.AdminTokenService;
import com.wechat.admin.service.AdminUserService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AdminAuthController {

    private final AdminAuthProperties props;
    private final AdminTokenService tokenService;
    private final AdminUserService adminUserService;

    public AdminAuthController(AdminAuthProperties props, AdminTokenService tokenService, AdminUserService adminUserService) {
        this.props = props;
        this.tokenService = tokenService;
        this.adminUserService = adminUserService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        boolean ok = req != null && adminUserService.validateLogin(req.getUsername(), req.getPassword());

        Map<String, Object> res = new HashMap<>();
        if (!ok) {
            res.put("success", false);
            res.put("message", "Invalid username or password");
            return res;
        }

        String token = tokenService.issueToken(props.getTokenTtlSeconds());
        res.put("success", true);
        res.put("token", token);
        res.put("expiresInSeconds", props.getTokenTtlSeconds());
        return res;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
