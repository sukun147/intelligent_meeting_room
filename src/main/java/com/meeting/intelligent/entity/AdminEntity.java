package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
@TableName("admin")
public class AdminEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 管理员id
     */
    @TableId
    private Long adminId;
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
    @Size(min=6, max=20, message = "密码长度必须在6-20个字符之间")
    private String password;
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "手机号格式不正确")
    private String phoneNumber;
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

}
