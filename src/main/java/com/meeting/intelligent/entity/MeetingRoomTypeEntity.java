package com.meeting.intelligent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("meeting_room_type")
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
    @NotBlank(message = "类型名称不能为空")
    @Size(max = 10, message = "类型名称不能超过10个字符")
    private String typeName;
    /**
     * 会议室描述
     */
    @NotBlank(message = "会议室描述不能为空")
    @Size(max = 50, message = "会议室描述不能超过50个字符")
    private String typeDescription;
    /**
     * 会议室设备
     */
    @NotEmpty(message = "会议室设备不能为空")
    private List<Equipment> equipment;
    /**
     * 启用状态（0为禁用，1为启用）
     */
    private Integer typeStatus;
    /**
     * 会议室类型排序
     */
    @OrderBy
    @NotNull(message = "会议室类型排序不能为空")
    private Integer typeSort;

}
