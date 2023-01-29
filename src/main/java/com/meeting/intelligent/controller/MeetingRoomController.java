package com.meeting.intelligent.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.meeting.intelligent.entity.MeetingRoomEntity;
import com.meeting.intelligent.service.MeetingRoomService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Result;
import com.meeting.intelligent.vo.MeetingRoomRespVo;
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
@RequestMapping("/meeting_room")
public class MeetingRoomController {
    @Autowired
    private MeetingRoomService meetingRoomService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = meetingRoomService.queryPage(params);
        return Result.success().setData(page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long roomId) {
        MeetingRoomRespVo meetingRoomRespVo = meetingRoomService.info(roomId);
        return Result.success().setData(meetingRoomRespVo);
    }

    /**
     * 保存
     */
    @PostMapping
    @SaCheckRole("admin")
    public Result save(@RequestBody @Valid MeetingRoomEntity meetingRoom) {
        meetingRoomService.saveRoom(meetingRoom);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @SaCheckRole("admin")
    public Result update(@RequestBody @Valid MeetingRoomEntity meetingRoom) {
        meetingRoomService.updateRoom(meetingRoom);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @SaCheckRole("admin")
    public Result delete(@RequestParam("id") List<Long> roomIds) {
        meetingRoomService.deleteRoom(roomIds);
        return Result.success();
    }

}
