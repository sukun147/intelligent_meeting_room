package com.meeting.intelligent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.vo.RegisterVo;

import java.util.List;
import java.util.Map;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean register(RegisterVo registerVo);

    Long login(UserEntity userEntity);

    void checkPhoneUnique(String phone);

    void checkUsernameUnique(String name);

    void checkPassword(String password);

    List<String> getPhones(List<Long> userIds);
}

