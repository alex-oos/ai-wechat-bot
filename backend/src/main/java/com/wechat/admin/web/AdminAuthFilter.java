package com.wechat.admin.web;

import com.wechat.admin.service.AdminTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class AdminAuthFilter extends OncePerRequestFilter {

    private final AdminTokenService tokenService;

    public AdminAuthFilter(AdminTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 静态资源/页面
        if (path.startsWith("/admin/") || path.equals("/") || path.equals("/index.html")) {
            return true;
        }
        if (path.startsWith("/static/") || path.startsWith("/webjars/") || path.startsWith("/favicon")) {
            return true;
        }

        // 回调接口必须放行
        if (path.startsWith("/v2/api/callback/")) {
            return true;
        }

        // 登录/健康检查
        if (path.equals("/api/auth/login") || path.equals("/api/health")) {
            return true;
        }

        // 只保护 /api/**
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            applyCorsHeaders(request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring("Bearer ".length()).trim();
        }

        if (!tokenService.isValid(token)) {
            applyCorsHeaders(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void applyCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if (origin == null || origin.isBlank()) {
            origin = "*";
        }
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        String reqHeaders = Optional.ofNullable(request.getHeader("Access-Control-Request-Headers")).orElse("*");
        response.setHeader("Access-Control-Allow-Headers", reqHeaders);
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
