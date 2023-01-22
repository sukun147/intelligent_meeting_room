package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@TableName("user")
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 员工id
     */
    @TableId(type = IdType.AUTO)
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
    @Size(min=6, max=20, message = "密码长度必须在6-20个字符之间")
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
     * 权限等级（1为普通员工）
     */
    @NotNull(message = "权限等级不能为空")
    private Integer permissionLevel;
    /**
     * 启用状态（0为禁用，1为启用）
     */
    private Integer status;
    /**
     * 人脸照片（base64）
     */
    @TableField(exist = false)
    private String facePhoto;

}
