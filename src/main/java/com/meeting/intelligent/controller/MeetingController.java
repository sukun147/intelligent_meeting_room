package com.meeting.intelligent.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.meeting.intelligent.service.MeetingService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Result;
import com.meeting.intelligent.vo.MeetingRespVo;
import com.meeting.intelligent.vo.MeetingVo;
import com.meeting.intelligent.vo.SignInVo;
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
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private MeetingService meetingService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = meetingService.queryPage(params);
        return Result.success().setData(page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long meetingId) {
        MeetingRespVo meetingRespVo = meetingService.get(meetingId);
        return Result.success().setData(meetingRespVo);
    }

    /**
     * 根据会议室id查询会议信息
     */
    @GetMapping("/room_meetings")
    public Result infoByRoomId(@RequestParam("roomId") Long roomId) {
        List<MeetingRespVo> vos = meetingService.getByRoomId(roomId);
        return Result.success().setData(vos);
    }

    /**
     * 保存
     */
    @PostMapping
    public Result save(@RequestBody @Valid MeetingVo meetingVo) {
        meetingService.add(meetingVo);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result update(@RequestBody @Valid MeetingVo meetingVo) {
        meetingService.delete(List.of(meetingVo.getMeetingId()));
        meetingService.add(meetingVo);
        return Result.success();
    }

    /**
     * 强制修改
     */
    @PostMapping("/update_force")
    @SaCheckRole("admin")
    public Result updateForce(@RequestBody @Valid MeetingVo meetingVo) {
        meetingService.updateForce(meetingVo);
        return Result.success();
    }

    /**
     * 会议签到
     */
    @PostMapping("/sign_in")
    public Result signIn(@RequestBody @Valid SignInVo signInVo) {
        meetingService.signIn(signInVo);
        return Result.success();
    }

    /**
     * 会议结束
     */
    @GetMapping("/finish")
    public Result finish(@RequestParam("id") Long meetingId, @RequestParam("sign") String sign, @RequestParam("time") Long timeStamp) {
        meetingService.finish(meetingId, sign, timeStamp);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result delete(@RequestParam("id") List<Long> meetingIds) {
        meetingService.delete(meetingIds);
        return Result.success();
    }

}
