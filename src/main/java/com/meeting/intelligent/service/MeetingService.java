package com.meeting.intelligent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.entity.MeetingEntity;
import com.meeting.intelligent.vo.MeetingRespVo;
import com.meeting.intelligent.vo.MeetingVo;
import com.meeting.intelligent.vo.SignInVo;

import java.util.List;
import java.util.Map;

/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
public interface MeetingService extends IService<MeetingEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void add(MeetingVo MeetingVo);

    MeetingRespVo get(Long meetingId);

    void updateForce(MeetingVo meetingVo);

    void delete(List<Long> meetingIds);

    List<MeetingRespVo> getByRoomId(Long roomId);

    void signIn(SignInVo signInVo);

    void finish(Long meetingId, String sign, Long timeStamp);
}

