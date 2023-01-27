package com.meeting.intelligent.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginVo {
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
    private String password;
}
