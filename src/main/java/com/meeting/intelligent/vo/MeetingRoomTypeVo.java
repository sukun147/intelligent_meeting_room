package com.meeting.intelligent.vo;

import lombok.Data;

import java.util.List;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@Data
public class MeetingRoomTypeVo {

    /**
     * 类型id
     */
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
    private List<Equipment> equipment;
    /**
     * 启用状态（0为禁用，1为启用）
     */
    private Integer typeStatus;
    /**
     * 会议室类型排序
     */
    private Integer typeSort;

}
