package com.meeting.intelligent.Exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.meeting.intelligent.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.VALID_EXCEPTION;

@Slf4j
@RestControllerAdvice(basePackages = "com.meeting.intelligent.controller")
public class GlobalExceptionAdvice {
    @ExceptionHandler(value = {Exception.class})
    public Result exceptionHandler(Exception exception) {
        if (exception instanceof NotLoginException) {
            log.info("用户未登录");
            return Result.error(ExceptionCodeEnum.PERMISSION_EXCEPTION.getCode(), ExceptionCodeEnum.PERMISSION_EXCEPTION.getMsg());
        } else if (exception instanceof MethodArgumentNotValidException) {
            Map<String, String> map = new HashMap<>();
            BindingResult result = ((MethodArgumentNotValidException) exception).getBindingResult();
            result.getFieldErrors().forEach((item) -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            log.info("数据校验未通过:{}", exception.getMessage());
            return Result.error(VALID_EXCEPTION.getCode(), VALID_EXCEPTION.getMsg()).setData(map);
        } else if (exception instanceof GlobalException) {
            log.info("自定义异常信息 ex={}", exception.getMessage());
            return Result.error(((GlobalException) exception).getCode(), ((GlobalException) exception).getMsg());
        } else {
            log.warn("未知异常 ex={}", exception.getMessage());
            exception.printStackTrace();
            return Result.error();
        }
    }
}
