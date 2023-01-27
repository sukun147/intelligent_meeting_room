package com.meeting.intelligent.Exception;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *  11: 会议室
 *  15: 数据
 */
public enum ExceptionCodeEnum {

    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数校验未通过"),
    TO_MANY_REQUEST(10002, "请求流量过大，请稍后再试"),
    CAPTCHA_CODE_BUSY_EXCEPTION(10003, "验证码获取频率太高，请稍后再试"),
    PERMISSION_EXCEPTION(10004, "没有权限，请登录"),
    SECONDARY_CERTIFICATION_EXCEPTION(10005, "没有权限，请进行二级认证"),
    MEETING_ROOM_OCCUPY_EXCEPTION(11001, "目标会议室该时间段已被占用，请重新选择"),
    ILLEGAL_TIMES_EXCEPTION(11002, "目标会议室该时间段不是允许时间，请重新选择"),
    USER_EXIST_EXCEPTION(15001, "存在相同的用户名"),
    PHONE_EXIST_EXCEPTION(15002, "存在相同的手机号"),
    EMAIL_EXIST_EXCEPTION(15003, "存在相同的邮箱"),
    ACCOUNT_PASSWORD_WRONG_EXCEPTION(15004, "用户名密码错误"),
    PHOTO_EXCEPTION(15005, "照片质量不佳，请更换照片"),
    ILLEGAL_CHARACTERS_EXCEPTION(15006, "存在非法字符"),
    CAPTCHA_CODE_WRONG_EXCEPTION(15007, "验证码错误");

    private int code;
    private String msg;

    ExceptionCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
