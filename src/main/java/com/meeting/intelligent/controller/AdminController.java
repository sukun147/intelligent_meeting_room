package com.meeting.intelligent.controller;

import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.stp.StpUtil;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;
import com.meeting.intelligent.utils.Result;
import com.meeting.intelligent.vo.AdminVo;
import com.meeting.intelligent.vo.LoginVo;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.SECONDARY_CERTIFICATION_EXCEPTION;


@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestBody @Valid LoginVo loginVo) {
        adminService.login(loginVo);
        StpUtil.login(0);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

    @GetMapping("/info")
    public Result info() {
        AdminEntity adminEntity = adminService.getById(1L);
        AdminVo adminVo = new AdminVo();
        BeanUtils.copyProperties(adminEntity, adminVo);
        return Result.success().setData(adminVo);
    }

    @PutMapping
    @SaCheckSafe
    public Result update(@RequestBody @Valid AdminEntity admin) {
        if (!StpUtil.isSafe()) {
            return Result.error(SECONDARY_CERTIFICATION_EXCEPTION.getCode(), SECONDARY_CERTIFICATION_EXCEPTION.getMsg());
        }
        adminService.updateById(admin);
        return Result.success();
    }

    @PostMapping("/openSafe")
    public Result openSafe(@RequestBody String password) {
        adminService.checkPassword(password);
        StpUtil.openSafe(120);
        return Result.success();
    }

}
