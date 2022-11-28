package com.meeting.intelligent.vo;

import lombok.Data;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
public class UserVo {

    /**
     * 员工id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 电话号码
     */
    private Integer phone;
    /**
     * 权限等级（1为普通员工）
     */
    private Integer permissionLevel;
    /**
     * 启用状态（0为禁用，1为启用）
     */
    private Integer status;

}
