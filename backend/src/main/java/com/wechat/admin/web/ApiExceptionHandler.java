package com.wechat.admin.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException e) {
        // 避免把内部堆栈直接暴露给前端
        log.error("API error", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("success", false, "message", e.getMessage()));
    }
}

