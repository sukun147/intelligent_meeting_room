package com.meeting.intelligent.controller;

import java.util.Arrays;
import java.util.Map;

import cn.dev33.satoken.stp.StpUtil;
import com.meeting.intelligent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.service.UserService;
import com.meeting.intelligent.utils.PageUtils;


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
    public Result register(@RequestBody UserEntity userEntity) {
        if (userService.register(userEntity)) {
            return Result.ok();
        }
        return Result.error("请更换人脸照片");
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserEntity userEntity) {
        Long userId = userService.login(userEntity);
        if (userId != null) {
            StpUtil.login(userId);
            return Result.ok();
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 列表
     */
    @GetMapping
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = userService.queryPage(params);

        return Result.ok().put("data", page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long userId) {
        UserEntity user = userService.getById(userId);

        return Result.ok().put("data", user);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result update(@RequestBody UserEntity user) {
        userService.updateById(user);

        return Result.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result delete(@RequestBody Long[] userIds) {
        userService.removeByIds(Arrays.asList(userIds));

        return Result.ok();
    }

}
