package com.meeting.intelligent.service.impl;

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

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.UserDao;
import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.service.UserService;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.CAPTCHA_CODE_ERROR_EXCEPTION;
import static com.meeting.intelligent.Exception.ExceptionCodeEnum.PHONE_EXIST_EXCEPTION;
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
            throw new GlobalException(CAPTCHA_CODE_ERROR_EXCEPTION.getMsg(), CAPTCHA_CODE_ERROR_EXCEPTION.getCode());
        }
        redisTemplate.delete(CAPTCHA_CODE_ERROR_EXCEPTION + registerVo.getPhone());
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
        UserEntity user = baseMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(userEntity.getPassword(), user.getPassword()) ? user.getUserId() : null;
    }

    @Override
    public void checkPhoneUnique(String phone) {
        Long count = baseMapper.selectCount(new QueryWrapper<UserEntity>().eq("phone", phone));
        if (count == 0) {
            throw new GlobalException(PHONE_EXIST_EXCEPTION.getMsg(), PHONE_EXIST_EXCEPTION.getCode());
        }
    }

    @Override
    public void checkUsernameUnique(String name) {
        Long count = baseMapper.selectCount(new QueryWrapper<UserEntity>().eq("username", name));
        if (count == 0) {
            throw new GlobalException(PHONE_EXIST_EXCEPTION.getMsg(), PHONE_EXIST_EXCEPTION.getCode());
        }
    }
}