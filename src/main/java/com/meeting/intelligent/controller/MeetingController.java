package com.meeting.intelligent.controller;

import java.util.Arrays;
import java.util.Map;

import com.meeting.intelligent.utils.Result;
import com.meeting.intelligent.vo.MeetingVo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.meeting.intelligent.entity.MeetingEntity;
import com.meeting.intelligent.service.MeetingService;
import com.meeting.intelligent.utils.PageUtils;


/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@RestController
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private MeetingService meetingService;

    /**
     * 列表
     */
    @GetMapping
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = meetingService.queryPage(params);
        return Result.success().setData(page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long meetingId) {
        MeetingEntity meeting = meetingService.getById(meetingId);
        return Result.success().setData(meeting);
    }

    /**
     * 保存
     */
    @PostMapping
    public Result save(@RequestBody @Valid MeetingVo meetingVo) {
        meetingService.bookMeeting(meetingVo);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result update(@RequestBody @Valid MeetingEntity meeting) {
        meetingService.updateById(meeting);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result delete(@RequestBody Long[] meetingIds) {
        meetingService.removeByIds(Arrays.asList(meetingIds));
        return Result.success();
    }

}
