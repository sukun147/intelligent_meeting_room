package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
@TableName("admin")
public class AdminEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 管理员id
     */
    @TableId
    private Long adminId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private Integer phoneNumber;
    /**
     * 邮箱
     */
    private String email;

}
