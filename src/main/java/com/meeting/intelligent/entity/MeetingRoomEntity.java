package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Time;

import lombok.Data;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
@TableName("meeting_room")
public class MeetingRoomEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId
    private Long roomId;
    /**
     * 地址
     */
    private String position;
    /**
     * 允许权限等级
     */
    private Integer permissionLevel;
    /**
     * 空闲开始时间
     */
    private Time idleStartTime;
    /**
     * 空闲结束时间
     */
    private Time idleEndTime;
    /**
     * 会议室类型
     */
    private Integer typeId;
    /**
     * 启用状态（0为禁用，1为启用）
     */
    private Integer roomStatus;
    /**
     * 会议室排序
     */
    private Integer roomSort;

}
