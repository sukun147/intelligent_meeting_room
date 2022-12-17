package com.meeting.intelligent.service.impl;

import com.baidu.aip.face.AipFace;
import com.meeting.intelligent.thirdParty.FaceClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.UserDao;
import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.service.UserService;
import org.springframework.util.DigestUtils;

@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
            new Query<UserEntity>().getPage(params),
            new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean register(UserEntity userEntity) {
        String salt = UUID.randomUUID().toString();
        userEntity.setSalt(salt);
        userEntity.setPassword(DigestUtils.md5DigestAsHex((userEntity.getPassword() + salt).getBytes()));
        this.save(userEntity);
        AipFace client = FaceClient.getInstance();
        JSONObject jsonObject = client.addUser(
            userEntity.getFacePhoto(),
            "BASE64",
            "1",
            String.valueOf(userEntity.getUserId()),
            null
        );
        boolean result = 0 == jsonObject.getInt("error_code");
        if (!result) {
            this.removeById(userEntity.getUserId());
        }
        return result;
    }

    @Override
    public Long login(UserEntity userEntity) {
        String username = userEntity.getUsername();
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<UserEntity>().eq("username", username);
        UserEntity user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        String cryptPwd = DigestUtils.md5DigestAsHex((userEntity.getPassword() + userEntity.getSalt()).getBytes());
        String realPwd = user.getPassword();
        return cryptPwd.equals(realPwd) ? user.getUserId() : null;
    }

}