package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.meeting.intelligent.vo.Equipment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
@TableName(value = "meeting_room_type", autoResultMap = true)
public class MeetingRoomTypeEntity implements Serializable {
    @Serial
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
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Equipment> equipment;
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
