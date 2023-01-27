package com.meeting.intelligent.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baidu.aip.face.AipFace;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.thirdParty.FaceClient;
import com.meeting.intelligent.vo.RegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.UserDao;
import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.service.UserService;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.*;
import static com.meeting.intelligent.utils.Constant.CAPTCHA_CODE_CACHE_PREFIX;

@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
            new Query<UserEntity>().getPage(params),
            new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean register(RegisterVo registerVo) {
        checkUsernameUnique(registerVo.getUsername());
        checkPhoneUnique(registerVo.getPhone());
        String code = redisTemplate.opsForValue().get(CAPTCHA_CODE_CACHE_PREFIX + registerVo.getPhone());
        if (StringUtils.isBlank(code) || !registerVo.getCode().equals(code)) {
            throw new GlobalException(CAPTCHA_CODE_WRONG_EXCEPTION.getMsg(), CAPTCHA_CODE_WRONG_EXCEPTION.getCode());
        }
        redisTemplate.delete(CAPTCHA_CODE_WRONG_EXCEPTION + registerVo.getPhone());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(encoder.encode(registerVo.getPassword()));
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
    public Long login(UserEntity userEntity) throws GlobalException {
        String username = userEntity.getUsername();
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<UserEntity>().eq("username", username);
        UserEntity user = this.getOne(queryWrapper);
        if (user == null) {
            return null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(userEntity.getPassword(), user.getPassword()) ? user.getUserId() : null;
    }

    @Override
    public void checkPhoneUnique(String phone) {
        long count = this.count(new QueryWrapper<UserEntity>().eq("phone", phone));
        if (count != 0) {
            throw new GlobalException(PHONE_EXIST_EXCEPTION.getMsg(), PHONE_EXIST_EXCEPTION.getCode());
        }
    }

    @Override
    public void checkUsernameUnique(String name) {
        long count = this.count(new QueryWrapper<UserEntity>().eq("username", name));
        if (count != 0) {
            throw new GlobalException(USER_EXIST_EXCEPTION.getMsg(), USER_EXIST_EXCEPTION.getCode());
        }
    }

    @Override
    public void checkPassword(String password) {
        long id = StpUtil.getLoginIdAsLong();
        UserEntity userEntity = this.getById(id);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (userEntity == null || !encoder.matches(password, userEntity.getPassword())) {
            throw new GlobalException(ACCOUNT_PASSWORD_WRONG_EXCEPTION.getMsg(), ACCOUNT_PASSWORD_WRONG_EXCEPTION.getCode());
        }
    }

    @Override
    public List<String> getPhones(List<Long> userIds) {
        List<String> result = new ArrayList<>();
        userIds.forEach(userId -> {
            if (userId == null) {
                throw new GlobalException("用户id不正确", VALID_EXCEPTION.getCode());
            }
            UserEntity user = this.getById(userId);
            if (user != null) {
                result.add(user.getPhone());
            }
        });
        return result;
    }
}