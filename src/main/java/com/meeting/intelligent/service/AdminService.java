package com.meeting.intelligent.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.vo.AdminRespVo;
import com.meeting.intelligent.vo.LoginVo;

import java.util.concurrent.TimeUnit;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
public interface AdminService extends IService<AdminEntity> {

    void login(LoginVo loginVo);

    void checkPassword(String password);

    @Cached(name = "admin_cache_", expire = 1, timeUnit = TimeUnit.DAYS)
    AdminRespVo getAdmin();

    @CacheInvalidate(name = "admin_cache_")
    void updateAdmin(AdminEntity admin);
}

