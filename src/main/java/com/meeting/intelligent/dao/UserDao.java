package com.meeting.intelligent.dao;

import com.meeting.intelligent.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
}
