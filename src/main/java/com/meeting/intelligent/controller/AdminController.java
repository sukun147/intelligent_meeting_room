package com.meeting.intelligent.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;
import com.meeting.intelligent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestBody AdminEntity adminEntity) {
        if (adminService.login(adminEntity)) {
            StpUtil.login(0);
            return Result.ok();
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return Result.ok();
    }

    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long adminId) {
        AdminEntity admin = adminService.getById(adminId);

        return Result.ok().put("data", admin);
    }

    @PutMapping
    public Result update(@RequestBody AdminEntity admin) {
        adminService.updateById(admin);

        return Result.ok();
    }
}
