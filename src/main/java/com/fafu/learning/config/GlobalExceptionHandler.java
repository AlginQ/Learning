package com.fafu.learning.config;

import com.fafu.learning.common.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(Exception.class)
    public ApiResult<?> handleException(Exception e) {
        logger.error("服务器内部错误", e);
        return ApiResult.fail(500, e.getMessage());
    }
}