package com.meeting.intelligent.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.service.UserService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Result;
import com.meeting.intelligent.vo.RegisterVo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION;
import static com.meeting.intelligent.Exception.ExceptionCodeEnum.PHOTO_EXCEPTION;


/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody @Valid RegisterVo registerVo) {
        if (userService.register(registerVo)) {
            return Result.success();
        }
        return Result.error(PHOTO_EXCEPTION.getCode(), PHOTO_EXCEPTION.getMsg());
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserEntity userEntity) {
        Long userId = userService.login(userEntity);
        if (userId != null) {
            StpUtil.login(userId);
            return Result.success();
        }
        return Result.error(LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg());
    }

    @GetMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 列表
     */
    @GetMapping
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = userService.queryPage(params);
        return Result.success().put("data", page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long userId) {
        UserEntity user = userService.getById(userId);
        return Result.success().put("data", user);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result update(@RequestBody @Valid UserEntity user) {
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result delete(@RequestBody @Valid Long[] userIds) {
        userService.removeByIds(Arrays.asList(userIds));
        return Result.success();
    }

}
