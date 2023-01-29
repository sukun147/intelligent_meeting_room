package com.meeting.intelligent.controller;

import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.stp.StpUtil;
import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.service.UserService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Result;
import com.meeting.intelligent.vo.LoginVo;
import com.meeting.intelligent.vo.UserVo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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
    public Result register(@RequestBody @Valid UserVo userVo) {
        userService.register(userVo);
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        userService.login(loginVo);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 列表
     */
    @GetMapping("/list")
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
    @SaCheckSafe
    public Result update(@RequestBody @Valid UserVo userVo) {
        userService.updateUser(userVo);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @SaCheckSafe
    public Result delete(@RequestParam("id") List<Long> userIds) {
        userService.deleteUsers(userIds);
        return Result.success();
    }

    @PostMapping("/open_safe")
    public Result openSafe(@RequestBody String password) {
        userService.checkPassword(password);
        StpUtil.openSafe(120);
        return Result.success();
    }

}
