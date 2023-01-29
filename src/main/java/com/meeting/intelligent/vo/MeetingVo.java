package com.meeting.intelligent.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
public class MeetingVo {

    /**
     * 会议id
     */
    private Long meetingId;
    /**
     * 会议标题
     */
    @NotBlank(message = "会议标题不能为空")
    @Size(max = 30, message = "会议标题不能超过30个字符")
    private String title;
    /**
     * 会议开始时间
     */
    @NotNull(message = "会议开始时间不能为空")
    @Future(message = "会议开始时间必须大于当前时间")
    private Date startTime;
    /**
     * 会议结束时间
     */
    @Future(message = "会议结束时间必须大于当前时间")
    @NotNull(message = "会议结束时间不能为空")
    private Date endTime;
    /**
     * 会议发起人
     */
    private Long createUserId;
    /**
     * 参会人员及其签到情况
     */
    @NotEmpty(message = "参会人员不能为空")
    @Valid
    private List<Participant> participants;
    /**
     * 会议报告地址
     */
    private String reportAddress;
    /**
     * 预定周期，cron格式（用于按日、周、月预定）
     */
    private String scheduledPeriod;
    /**
     * 启用状态（0为禁用，1为启用）
     */
    private Integer meetingStatus;
    /**
     * 会议室id
     */
    @NotNull(message = "会议室id不能为空")
    private Long roomId;
    /**
     * 会议室地址
     */
    private String position;
    /**
     * 会议描述
     */
    private String meetingDescription;
    /**
     * 是否接收调剂
     */
    @NotNull(message = "是否接收调剂不能为空")
    private Boolean adjustAble;
}
