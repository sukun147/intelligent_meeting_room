package com.meeting.intelligent.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
public class AdminRespVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 邮箱
     */
    private String email;

}
