package com.meeting.intelligent.vo;

import lombok.Data;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
public class AdminVo {

    /**
     * 管理员id
     */
    private Long adminId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private Integer phoneNumber;
    /**
     * 邮箱
     */
    private String email;

}
