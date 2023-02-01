package com.meeting.intelligent.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.baidu.aip.face.AipFace;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.dao.UserDao;
import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.service.UserService;
import com.meeting.intelligent.thirdParty.FaceClient;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;
import com.meeting.intelligent.vo.LoginVo;
import com.meeting.intelligent.vo.UserRespVo;
import com.meeting.intelligent.vo.UserVo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.*;
import static com.meeting.intelligent.utils.Constant.CAPTCHA_CODE_CACHE_PREFIX;
import static com.meeting.intelligent.utils.Constant.LOGIN_LIMIT_USERNAME_CACHE_PREFIX;

@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    private Cache<String, UserRespVo> userCache;

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("user_cache_").expire(Duration.ofHours(1)).cacheType(CacheType.REMOTE).build();
        userCache = cacheManager.getOrCreateCache(qc);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserRespVo> page = this.page(
            new Query<UserEntity>().getPage(params),
            new QueryWrapper<>()
        ).convert(userEntity -> {
            UserRespVo userRespVo = new UserRespVo();
            BeanUtils.copyProperties(userEntity, userRespVo);
            return userRespVo;
        });
        return new PageUtils(page);
    }

    @Override
    public void register(UserVo userVo) {
        userVo.setUserId(null);
        checkUsernameUnique(userVo.getUsername());
        checkPhoneUnique(userVo.getPhone());
        String code = redisTemplate.opsForValue().get(CAPTCHA_CODE_CACHE_PREFIX + userVo.getPhone());
        if (StringUtils.isBlank(code) || !userVo.getCode().equals(code.split("_")[0])) {
            throw new GlobalException(CAPTCHA_CODE_WRONG_EXCEPTION.getMsg(), CAPTCHA_CODE_WRONG_EXCEPTION.getCode());
        }
        redisTemplate.delete(CAPTCHA_CODE_WRONG_EXCEPTION + userVo.getPhone());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userVo.setPassword(encoder.encode(userVo.getPassword()));
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userVo, userEntity);
        userEntity.setPermissionLevel(1);
        this.save(userEntity);
        AipFace client = FaceClient.getInstance();
        JSONObject jsonObject = client.addUser(
            userVo.getFacePhoto(),
            "BASE64",
            "1",
            String.valueOf(userEntity.getUserId()),
            null
        );
        int errorCode = jsonObject.getInt("error_code");
        if (0 != errorCode) {
            log.warn("人脸注册失败，错误码：{}，错误信息：{}", errorCode, jsonObject.getString("error_msg"));
            this.removeById(userEntity.getUserId());
            throw new GlobalException(FACE_REGISTER_EXCEPTION.getMsg(), FACE_REGISTER_EXCEPTION.getCode());
        }
    }

    @Override
    public void login(LoginVo loginVo) throws GlobalException {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String username = loginVo.getUsername();
        String limit = LOGIN_LIMIT_USERNAME_CACHE_PREFIX + username;
        String count = ops.get(limit);
        if (StringUtils.isNotBlank(count) && Integer.parseInt(count) > 5) {
            redisTemplate.expire(limit, 1, TimeUnit.DAYS);
            log.info("用户{}1小时内登录失败次数超过5次，禁用一天", username);
            throw new GlobalException(USERNAME_DISABLE_EXCEPTION.getMsg(), USERNAME_DISABLE_EXCEPTION.getCode());
        }
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<UserEntity>().eq("username", username);
        UserEntity user = this.getOne(queryWrapper);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user == null || !encoder.matches(loginVo.getPassword(), user.getPassword())) {
            ops.increment(limit, 1);
            redisTemplate.expire(limit, 1, TimeUnit.HOURS);
            throw new GlobalException(ACCOUNT_PASSWORD_WRONG_EXCEPTION.getMsg(), ACCOUNT_PASSWORD_WRONG_EXCEPTION.getCode());
        }
        StpUtil.login(user.getUserId());
    }

    @Override
    public UserRespVo get(Long userId) {
        long loginId = StpUtil.getLoginIdAsLong();
        if (loginId != 0L && loginId != userId) {
            throw new GlobalException(ILLEGAL_ACCESS_EXCEPTION.getMsg(), ILLEGAL_ACCESS_EXCEPTION.getCode());
        }
        UserRespVo cache = userCache.get(userId.toString());
        if (cache != null) {
            return cache;
        }
        UserEntity userEntity = this.getById(userId);
        if (userEntity==null){
            throw new GlobalException(USER_NOT_EXIST_EXCEPTION.getMsg(), USER_NOT_EXIST_EXCEPTION.getCode());
        }
        UserRespVo userRespVo = new UserRespVo();
        BeanUtils.copyProperties(userEntity, userRespVo);
        userCache.put(userId.toString(), userRespVo, 55 + new Random().nextInt(10), TimeUnit.MINUTES);
        return userRespVo;
    }

    @Override
    public void deleteUsers(List<Long> userIds) {
        long loginId = StpUtil.getLoginIdAsLong();
        if (loginId != 0L) {
            this.listByIds(userIds).forEach(user -> {
                if (!user.getUserId().equals(loginId)) {
                    throw new GlobalException(ILLEGAL_ACCESS_EXCEPTION.getMsg(), ILLEGAL_ACCESS_EXCEPTION.getCode());
                }
            });
        }
        AipFace client = FaceClient.getInstance();
        for (Long userId : userIds) {
            JSONObject jsonObject = client.deleteUser("1", String.valueOf(userId), null);
            int errorCode = jsonObject.getInt("error_code");
            if (0 != errorCode) {
                log.warn("人脸删除失败，错误码：{}，错误信息：{}", errorCode, jsonObject.getString("error_msg"));
                throw new GlobalException(FACE_DELETE_EXCEPTION.getMsg(), FACE_DELETE_EXCEPTION.getCode());
            }
        }
        this.removeBatchByIds(userIds);
    }

    @Override
    public void updateUser(UserVo userVo) {
        long loginId = StpUtil.getLoginIdAsLong();
        if (loginId != 0L && !userVo.getUserId().equals(loginId)) {
            throw new GlobalException(ILLEGAL_ACCESS_EXCEPTION.getMsg(), ILLEGAL_ACCESS_EXCEPTION.getCode());
        }
        AipFace client = FaceClient.getInstance();
        JSONObject jsonObject = client.updateUser(
            userVo.getFacePhoto(),
            "BASE64",
            "1",
            String.valueOf(userVo.getUserId()),
            null
        );
        int errorCode = jsonObject.getInt("error_code");
        if (0 != errorCode) {
            log.warn("人脸更新失败，错误码：{}，错误信息：{}", errorCode, jsonObject.getString("error_msg"));
            throw new GlobalException(FACE_UPDATE_EXCEPTION.getMsg(), FACE_UPDATE_EXCEPTION.getCode());
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userVo.setPassword(encoder.encode(userVo.getPassword()));
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userVo, userEntity);
        this.updateById(userEntity);
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
    public void checkIds(List<Long> userIds) {
        long count = this.count(new QueryWrapper<UserEntity>().in("user_id", userIds));
        if (count != userIds.size()) {
            throw new GlobalException("非法用户id", PARAMETER_CHECK_EXCEPTION.getCode());
        }
    }
}