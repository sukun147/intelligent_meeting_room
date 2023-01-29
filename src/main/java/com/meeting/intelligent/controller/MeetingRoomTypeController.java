package com.meeting.intelligent.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.meeting.intelligent.entity.MeetingRoomTypeEntity;
import com.meeting.intelligent.service.MeetingRoomTypeService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Result;
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
@RequestMapping("/meeting_room_type")
public class MeetingRoomTypeController {
    @Autowired
    private MeetingRoomTypeService meetingRoomTypeService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = meetingRoomTypeService.queryPage(params);
        return Result.success().setData(page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long typeId) {
        MeetingRoomTypeEntity meetingRoomType = meetingRoomTypeService.getById(typeId);
        return Result.success().setData(meetingRoomType);
    }

    /**
     * 保存
     */
    @PostMapping
    @SaCheckRole("admin")
    public Result save(@RequestBody @Valid MeetingRoomTypeEntity meetingRoomType) {
        meetingRoomTypeService.save(meetingRoomType);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @SaCheckRole("admin")
    public Result update(@RequestBody @Valid MeetingRoomTypeEntity meetingRoomType) {
        meetingRoomTypeService.updateById(meetingRoomType);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @SaCheckRole("admin")
    public Result delete(@RequestParam("id") List<Long> typeIds) {
        meetingRoomTypeService.deleteTypes(typeIds);
        return Result.success();
    }

}
