package com.meeting.intelligent.Exception;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *  11: 会议室
 *  12: 会议
 *  13: 会议室类型
 *  14: 用户
 *  15: 数据
 */
public enum ExceptionCodeEnum {
    // 通用
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    PARAMETER_CHECK_EXCEPTION(10001, "参数校验未通过"),
    TO_MANY_REQUEST(10002, "请求流量过大，请稍后再试"),
    CAPTCHA_CODE_BUSY_EXCEPTION(10003, "验证码获取频率太高，请稍后再试"),
    PERMISSION_EXCEPTION(10004, "没有权限，请登录"),
    SECONDARY_CERTIFICATION_EXCEPTION(10005, "没有权限，请进行二级认证"),
    ILLEGAL_ACCESS_EXCEPTION(10006, "非法访问他人资源"),
    USERNAME_DISABLE_EXCEPTION(10007, "短时间内多次登录失败，该用户名已禁用一天"),
    SCHEDULER_EXCEPTION(10008, "定时任务异常"),
    // 会议室
    MEETING_ROOM_OCCUPY_EXCEPTION(11001, "目标会议室该时间段已被占用，请重新选择"),
    ILLEGAL_TIMES_EXCEPTION(11002, "目标会议室该时间段不是允许时间，请重新选择"),
    MEETING_ROOM_NOT_EXIST_EXCEPTION(11003, "目标会议室不存在"),
    MEETING_ROOM_IN_USE_EXCEPTION(11004, "目标会议室正在使用中，不允许删除"),
    CONFLICT_WITH_PERIODIC_MEETING_EXCEPTION(11005, "目标会议室该时间段与周期会议冲突，请重新选择"),
    // 会议
    MEETING_NOT_EXIST_EXCEPTION(12001, "目标会议不存在"),
    MEETING_END_EXCEPTION(12002, "会议已结束"),
    // 会议室类型
    ROOM_TYPE_NOT_EXIST_EXCEPTION(13001, "目标会议室类型不存在"),
    ROOM_TYPE_IN_USE_EXCEPTION(13002, "目标会议室类型正在使用中，不允许删除"),
    // 人脸
    FACE_REGISTER_EXCEPTION(14001, "人脸注册失败，请更换人脸照片重试或联系管理员"),
    FACE_DELETE_EXCEPTION(14002, "人脸删除失败，请联系管理员"),
    FACE_UPDATE_EXCEPTION(14003, "人脸更新失败，请更换人脸照片重试或联系管理员"),
    FACE_GET_EXCEPTION(14004, "人脸获取失败，请联系管理员"),
    FACE_SEARCH_EXCEPTION(14005, "人脸搜索失败，请联系管理员"),
    USER_NOT_EXIST_EXCEPTION(14006, "用户不存在"),
    // 数据
    USER_EXIST_EXCEPTION(15001, "存在相同的用户名"),
    PHONE_EXIST_EXCEPTION(15002, "存在相同的手机号"),
    EMAIL_EXIST_EXCEPTION(15003, "存在相同的邮箱"),
    ACCOUNT_PASSWORD_WRONG_EXCEPTION(15004, "用户名密码错误"),
    ILLEGAL_CHARACTERS_EXCEPTION(15005, "存在非法字符"),
    CAPTCHA_CODE_WRONG_EXCEPTION(15006, "验证码错误");

    private final int code;
    private final String msg;

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
