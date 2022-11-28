package com.meeting.intelligent.controller;

import java.util.Arrays;
import java.util.Map;

import com.meeting.intelligent.utils.Result;
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

        return Result.ok().put("data", page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long meetingId) {
        MeetingEntity meeting = meetingService.getById(meetingId);

        return Result.ok().put("data", meeting);
    }

    /**
     * 保存
     */
    @PostMapping
    public Result save(@RequestBody MeetingEntity meeting) {


        meetingService.save(meeting);

        return Result.ok();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result update(@RequestBody MeetingEntity meeting) {
        meetingService.updateById(meeting);

        return Result.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result delete(@RequestBody Long[] meetingIds) {
        meetingService.removeByIds(Arrays.asList(meetingIds));

        return Result.ok();
    }

}
