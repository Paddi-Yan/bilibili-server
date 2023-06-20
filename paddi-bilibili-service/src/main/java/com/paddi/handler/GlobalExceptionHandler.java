package com.paddi.handler;

import com.paddi.core.ret.Result;
import com.paddi.exception.BadRequestException;
import com.paddi.exception.ConditionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 15:16:49
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<String> exceptionHandler(Exception e) {
        log.error(e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(value = ConditionException.class)
    @ResponseBody
    public Result<String> conditionExceptionHandler(ConditionException e) {
        log.error(e.getMessage());
        return Result.error(e.getMessage(), e.getCode());
    }

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseBody
    public Result<String> badRequestExceptionHandler(BadRequestException e) {
        log.error(e.getMessage());
        return Result.error(e.getMessage(), e.getCode());
    }
}
