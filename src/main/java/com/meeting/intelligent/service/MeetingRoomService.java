package com.meeting.intelligent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.entity.MeetingRoomEntity;

import java.util.Map;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
public interface MeetingRoomService extends IService<MeetingRoomEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

