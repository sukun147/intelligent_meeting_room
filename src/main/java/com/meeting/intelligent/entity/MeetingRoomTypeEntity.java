package com.meeting.intelligent.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
@TableName("meeting_room_type")
public class MeetingRoomTypeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类型id
     */
    @TableId(type = IdType.AUTO)
    private Long typeId;
    /**
     * 会议室名称
     */
    private String typeName;
    /**
     * 会议室描述
     */
    private String typeDescription;
    /**
     * 会议室设备
     */
    private JSON equipment;
    /**
     * 启用状态（0为禁用，1为启用）
     */
    private Integer typeStatus;
    /**
     * 会议室类型排序
     */
    @OrderBy
    private Integer typeSort;

}
