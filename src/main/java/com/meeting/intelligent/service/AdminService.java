package com.meeting.intelligent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.vo.LoginVo;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
public interface AdminService extends IService<AdminEntity> {

    void login(LoginVo loginVo);

    void checkPassword(String password);

    void updateAdmin(AdminEntity admin);
}

