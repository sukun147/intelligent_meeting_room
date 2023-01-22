package com.meeting.intelligent.controller;

import java.util.Arrays;
import java.util.Map;

import com.meeting.intelligent.utils.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.meeting.intelligent.entity.MeetingRoomTypeEntity;
import com.meeting.intelligent.service.MeetingRoomTypeService;
import com.meeting.intelligent.utils.PageUtils;


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
    @GetMapping
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
    public Result save(@RequestBody @Valid MeetingRoomTypeEntity meetingRoomType) {
        meetingRoomTypeService.save(meetingRoomType);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result update(@RequestBody @Valid MeetingRoomTypeEntity meetingRoomType) {
        meetingRoomTypeService.updateById(meetingRoomType);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result delete(@RequestBody Long[] typeIds) {
        meetingRoomTypeService.removeByIds(Arrays.asList(typeIds));
        return Result.success();
    }

}
