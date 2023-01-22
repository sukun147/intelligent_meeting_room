package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
@TableName("meeting_room")
public class MeetingRoomEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long roomId;
    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空")
    @Size(max = 50, message = "地址长度不能超过50")
    private String position;
    /**
     * 允许权限等级
     */
    @NotNull(message = "权限等级不能为空")
    private Integer permissionLevel;
    /**
     * 空闲开始时间
     */
    @NotNull(message = "空闲开始时间不能为空")
    private Time idleStartTime;
    /**
     * 空闲结束时间
     */
    @NotNull(message = "空闲结束时间不能为空")
    private Time idleEndTime;
    /**
     * 会议室类型
     */
    @NotNull(message = "会议室类型不能为空")
    private Integer typeId;
    /**
     * 启用状态（0为禁用，1为启用）
     */
    private Integer roomStatus;
    /**
     * 会议室排序
     */
    @OrderBy
    @NotNull(message = "会议室排序不能为空")
    private Integer roomSort;

}
