package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
@TableName("user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 员工id
	 */
	@TableId
	private Long userId;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
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
	 * base64编码的图片
	 */
	private String faceInfo;
	/**
	 * 启用状态（0为禁用，1为启用）
	 */
	private Integer status;

}
