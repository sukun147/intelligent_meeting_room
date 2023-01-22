package com.meeting.intelligent.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;
import com.meeting.intelligent.utils.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION;


@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestBody AdminEntity adminEntity) {
        if (adminService.login(adminEntity)) {
            StpUtil.login(0);
            return Result.success();
        }
        return Result.error(LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg());
    }

    @PostMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long adminId) {
        AdminEntity admin = adminService.getById(adminId);
        return Result.success().setData(admin);
    }

    @PutMapping
    public Result update(@RequestBody @Valid AdminEntity admin) {
        adminService.updateById(admin);
        return Result.success();
    }
}
