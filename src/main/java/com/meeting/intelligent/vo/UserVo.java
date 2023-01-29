package com.meeting.intelligent.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserVo {

    private Long userId;
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min=4, max=20, message = "用户名长度必须在4-20个字符之间")
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min=32, max=32, message = "密码未加密")
    private String password;
    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(min=2, max=36, message = "真实姓名长度必须在2-36个字符之间")
    private String realName;
    /**
     * 电话号码
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "手机号格式不正确")
    private String phone;
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
    /**
     * 人脸照片（base64）
     */
    @NotBlank(message = "人脸照片不能为空")
    private String facePhoto;
}
