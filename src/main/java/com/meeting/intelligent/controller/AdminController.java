package com.meeting.intelligent.controller;

import java.util.Arrays;
import java.util.Map;

import com.meeting.intelligent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;
import com.meeting.intelligent.utils.PageUtils;



/**
 *
 *
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long adminId){
		AdminEntity admin = adminService.getById(adminId);

        return Result.ok().put("data", admin);
    }

    /**
     * 保存
     */
    @PostMapping
    public Result save(@RequestBody AdminEntity admin){
		adminService.save(admin);

        return Result.ok();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result update(@RequestBody AdminEntity admin){
		adminService.updateById(admin);

        return Result.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result delete(@RequestBody Long[] adminIds){
		adminService.removeByIds(Arrays.asList(adminIds));

        return Result.ok();
    }

}
