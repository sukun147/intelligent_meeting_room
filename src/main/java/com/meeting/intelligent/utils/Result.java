package com.meeting.intelligent.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Result extends HashMap<String, Object> implements Serializable {
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR_DATA = 1;
    public static final int CODE_ERROR_SYSTEM = 2;
    private static final long serialVersionUID = 1L;

    public Result() {
        put("code", CODE_SUCCESS);
        put("msg", "");
    }

    public static Result error() {
        return error(CODE_ERROR_SYSTEM, "系统异常，请稍后再试");
    }

    public static Result error(String msg) {
        return error(CODE_ERROR_DATA, msg);
    }

    public static Result error(int code, String msg) {
        Result Result = new Result();
        Result.put("code", code);
        Result.put("msg", msg);
        return Result;
    }

    public static Result ok(String msg) {
        Result Result = new Result();
        Result.put("msg", msg);
        return Result;
    }

    public static Result ok(Map<String, Object> map) {
        Result Result = new Result();
        Result.putAll(map);
        return Result;
    }

    public static Result ok() {
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