package com.meeting.intelligent.controller;

import java.util.Arrays;
import java.util.Map;

import com.meeting.intelligent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.meeting.intelligent.entity.MeetingRoomEntity;
import com.meeting.intelligent.service.MeetingRoomService;
import com.meeting.intelligent.utils.PageUtils;



/**
 * 
 *
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@RestController
@RequestMapping("/meeting_room")
public class MeetingRoomController {
    @Autowired
    private MeetingRoomService meetingRoomService;

    /**
     * 列表
     */
    @GetMapping
    public Result list(@RequestParam Map<String, Object> params){
        PageUtils page = meetingRoomService.queryPage(params);

        return Result.ok().put("data", page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long roomId){
		MeetingRoomEntity meetingRoom = meetingRoomService.getById(roomId);

        return Result.ok().put("data", meetingRoom);
    }

    /**
     * 保存
     */
    @PostMapping
    public Result save(@RequestBody MeetingRoomEntity meetingRoom){
		meetingRoomService.save(meetingRoom);

        return Result.ok();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result update(@RequestBody MeetingRoomEntity meetingRoom){
		meetingRoomService.updateById(meetingRoom);

        return Result.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result delete(@RequestBody Long[] roomIds){
		meetingRoomService.removeByIds(Arrays.asList(roomIds));

        return Result.ok();
    }

}
