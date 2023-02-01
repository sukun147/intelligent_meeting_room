package com.meeting.intelligent.controller;

import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.stp.StpUtil;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;
import com.meeting.intelligent.utils.Result;
import com.meeting.intelligent.vo.AdminRespVo;
import com.meeting.intelligent.vo.LoginVo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody @Valid LoginVo loginVo) {
        adminService.login(loginVo);
        StpUtil.login(0);
        return Result.success();
    }

    /**
     * 管理员登出
     */
    @PostMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 获取管理员信息
     */
    @GetMapping
    public Result info() {
        AdminRespVo adminRespVo = adminService.getAdmin();
        return Result.success().setData(adminRespVo);
    }

    /**
     * 修改密码，需要二次认证
     */
    @PutMapping
    @SaCheckSafe
    public Result update(@RequestBody @Valid AdminEntity admin) {
        adminService.updateAdmin(admin);
        return Result.success();
    }

    /**
     * 开启二次认证
     */
    @PostMapping("/open_safe")
    public Result openSafe(@RequestBody String password) {
        adminService.checkPassword(password);
        StpUtil.openSafe(120);
        return Result.success();
    }

}
