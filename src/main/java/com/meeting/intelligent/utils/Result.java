package com.meeting.intelligent.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.util.HashMap;
import java.util.Map;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.UNKNOWN_EXCEPTION;

public class Result extends HashMap<String, Object> {

    private Result() {
        put("code", 0);
        put("msg", "");
    }

    public static Result error() {
        return error(UNKNOWN_EXCEPTION.getCode(), UNKNOWN_EXCEPTION.getMsg());
    }

    public static Result error(String msg) {
        return error(UNKNOWN_EXCEPTION.getCode(), msg);
    }

    public static Result error(int code, String msg) {
        Result Result = new Result();
        Result.put("code", code);
        Result.put("msg", msg);
        return Result;
    }

    public static Result success(String msg) {
        Result Result = new Result();
        Result.put("msg", msg);
        return Result;
    }

    public static Result success(Map<String, Object> map) {
        Result Result = new Result();
        Result.putAll(map);
        return Result;
    }

    public static Result success() {
        return new Result();
    }

    public Result setData(Object data) {
        put("data", data);
        return this;
    }

    public <T> T getData(TypeReference<T> typeReference) {
        Object data = get("data");
        return JSON.parseObject(JSON.toJSONString(data), typeReference);
    }

    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public Integer getCode() {
        return (Integer) this.get("code");
    }
}