package com.wechat.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.admin.config.AdminAuthProperties;
import com.wechat.bot.entity.dto.AdminUserDTO;
import com.wechat.bot.mapper.AdminUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final AdminAuthProperties authProperties;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminUserService(AdminUserMapper adminUserMapper, AdminAuthProperties authProperties) {
        this.adminUserMapper = adminUserMapper;
        this.authProperties = authProperties;
    }

    public void ensureDefaultUser() {
        String username = authProperties.getUsername();
        String password = authProperties.getPassword();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            log.warn("Admin default user not created: missing username/password in admin.auth.");
            return;
        }
        AdminUserDTO existing = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUserDTO>()
                .eq(AdminUserDTO::getUsername, username)
                .last("LIMIT 1"));
        if (existing == null) {
            AdminUserDTO user = AdminUserDTO.builder()
                    .username(username)
                    .passwordHash(passwordEncoder.encode(password))
                    .displayName("Admin")
                    .status("ACTIVE")
                    .deleted(0)
                    .build();
            adminUserMapper.insert(user);
            log.info("Created default admin user: {}", username);
            return;
        }
        existing.setPasswordHash(passwordEncoder.encode(password));
        existing.setDisplayName(existing.getDisplayName() == null ? "Admin" : existing.getDisplayName());
        existing.setStatus(existing.getStatus() == null ? "ACTIVE" : existing.getStatus());
        existing.setDeleted(existing.getDeleted() == null ? 0 : existing.getDeleted());
        adminUserMapper.updateById(existing);
        log.info("Updated default admin user: {}", username);
    }

    public Optional<AdminUserDTO> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        AdminUserDTO user = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUserDTO>()
                .eq(AdminUserDTO::getUsername, username)
                .eq(AdminUserDTO::getDeleted, 0)
                .last("LIMIT 1"));
        return Optional.ofNullable(user);
    }

    public boolean validateLogin(String username, String password) {
        Optional<AdminUserDTO> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        AdminUserDTO user = userOpt.get();
        if (user.getStatus() != null && !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return false;
        }
        if (password == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
}
