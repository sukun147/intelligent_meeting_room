package com.meeting.intelligent.vo;

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
    private String title;
    /**
     * 会议开始时间
     */
    private Date startTime;
    /**
     * 预计会议结束时间
     */
    private Date endTime;
    /**
     * 最晚会议结束时间
     */
    private Date latestEndTime;
    /**
     * 参会人员及其签到情况
     */
    private List<Participants> participants;
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
    private Long roomId;
    /**
     * 会议室地址
     */
    private String position;
    /**
     * 会议描述
     */
    private String meetingDescription;
}
