package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meeting.intelligent.vo.Participants;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
@TableName("meeting")
public class MeetingEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会议id
     */
    @TableId(type = IdType.AUTO)
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
     * 参会人员及其签到情况
     */
    @NotEmpty(message = "参会人员不能为空")
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
    @NotNull(message = "会议室id不能为空")
    private Long roomId;
    /**
     * 会议描述
     */
    private String meetingDescription;

}
