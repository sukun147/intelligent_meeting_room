package com.meeting.intelligent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.dao.MeetingDao;
import com.meeting.intelligent.entity.MeetingEntity;
import com.meeting.intelligent.entity.MeetingRoomEntity;
import com.meeting.intelligent.service.MeetingService;
import com.meeting.intelligent.service.UserService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;
import com.meeting.intelligent.utils.TimeUtil;
import com.meeting.intelligent.vo.MeetingVo;
import com.meeting.intelligent.vo.Participant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.*;


@Slf4j
@Service("meetingService")
public class MeetingServiceImpl extends ServiceImpl<MeetingDao, MeetingEntity> implements MeetingService {
    private static final int THIRTY_MINUTES = 30 * 60 * 1000;
    private static final int ONE_DAY = 24 * 60 * 60 * 1000;
    private static final int TWO_DAY = 48 * 60 * 60 * 1000;
    private static final int ONE_WEEK = 7 * 24 * 60 * 60 * 1000;
    @Autowired
    private MeetingRoomServiceImpl meetingRoomService;
    @Autowired
    private UserService userService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingEntity> page = this.page(
            new Query<MeetingEntity>().getPage(params),
            new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public void reserveMeeting(MeetingVo meetingVo) {
        Date startTime = meetingVo.getStartTime();
        Date endTime = meetingVo.getEndTime();
        if (startTime.after(endTime)) {
            throw new GlobalException("会议时间非正确时间段", VALID_EXCEPTION.getCode());
        }
        MeetingRoomEntity room = meetingRoomService.getById(meetingVo.getRoomId());
        if (room == null) {
            throw new GlobalException("会议室不存在", VALID_EXCEPTION.getCode());
        }
        Date idleStartTime = room.getIdleStartTime();
        Date idleEndTime = room.getIdleEndTime();
        if (TimeUtil.compareTime(idleStartTime, startTime) > 0 || TimeUtil.compareTime(idleEndTime, endTime) < 0) {
            throw new GlobalException(ILLEGAL_TIMES_EXCEPTION.getMsg(), ILLEGAL_TIMES_EXCEPTION.getCode());
        }
        long createTime = new Date().getTime();
        Timer timer = new Timer();
        ReserveTimerTask reserveTimerTask = new ReserveTimerTask(meetingVo, createTime);
        if (startTime.getTime() - createTime < TWO_DAY) {
            doReserveMeeting(meetingVo, createTime);
        } else if (startTime.getTime() - createTime < ONE_WEEK) {
            timer.schedule(reserveTimerTask, new Date(startTime.getTime() - ONE_DAY));
        } else {
            if (meetingVo.getAdjustAble()) {
                timer.schedule(reserveTimerTask, new Date(startTime.getTime() - ONE_WEEK));
            } else {
                timer.schedule(reserveTimerTask, new Date(startTime.getTime() - ONE_DAY));
            }
        }
    }

    private void doReserveMeeting(MeetingVo meetingVo, long createTime) {
        Long roomId = meetingVo.getRoomId();
        Date startTime = meetingVo.getStartTime();
        Date endTime = meetingVo.getEndTime();
        MeetingEntity meetingEntity = new MeetingEntity();
        BeanUtils.copyProperties(meetingVo, meetingEntity);
        QueryWrapper<MeetingEntity> nowRoomQuery = new QueryWrapper<MeetingEntity>().eq("room_id", roomId)
            .eq("meeting_status", 1)
            .and(wrapper -> wrapper
                .between("start_time", startTime, endTime)
                .or().between("end_time", startTime, endTime)
            );
        List<MeetingEntity> conflictMeetings = this.list(nowRoomQuery);
        if (conflictMeetings.size() > 0) {
            if (startTime.getTime() - createTime < TWO_DAY) {
                throw new GlobalException(MEETING_ROOM_OCCUPY_EXCEPTION.getMsg(), MEETING_ROOM_OCCUPY_EXCEPTION.getCode());
            } else {
                if (meetingVo.getAdjustAble()) {
                    adjustMeeting(conflictMeetings, meetingEntity);
                } else {
                    throw new GlobalException(MEETING_ROOM_OCCUPY_EXCEPTION.getMsg(), MEETING_ROOM_OCCUPY_EXCEPTION.getCode());
                }
            }
        } else {
            doSaveMeeting(meetingEntity, roomId, startTime);
        }
    }

    private void doSaveMeeting(MeetingEntity meetingEntity, Long roomId, Date startTime) {
        this.save(meetingEntity);
        List<Participant> participants = meetingEntity.getParticipants();
        List<Long> userIds = new ArrayList<>();
        for (Participant participant : participants) {
            userIds.add(participant.getUserId());
        }
        List<String> phones = userService.getPhones(userIds);
        String position = meetingRoomService.getById(roomId).getPosition();
        Timer timer = new Timer();
        RemindTimerTask remindTimerTask = new RemindTimerTask(phones, meetingEntity.getTitle(), position);
        timer.schedule(remindTimerTask, new Date(startTime.getTime() - THIRTY_MINUTES));
    }

    private void adjustMeeting(List<MeetingEntity> conflictMeetings, MeetingEntity currentMeeting) {
        // 权重=会议时长(单位小时)*会议人数
        PriorityQueue<MeetingEntity> priorityQueue = new PriorityQueue<>(
            Comparator.comparingInt(o ->
                (int) ((o.getEndTime().getTime() - o.getStartTime().getTime()) / 1000 / 60 / 60 * o.getParticipants().size())
            )
        );
        priorityQueue.addAll(conflictMeetings);
        priorityQueue.add(currentMeeting);
        MeetingEntity priorityMeeting = priorityQueue.poll();
        Long roomId = priorityMeeting.getRoomId();
        Date startTime = priorityMeeting.getStartTime();
        doSaveMeeting(priorityMeeting, roomId, startTime);
        MeetingRoomEntity room = meetingRoomService.getById(roomId);
        Date idleStartTime = room.getIdleStartTime();
        Date idleEndTime = room.getIdleEndTime();
        TimeUtil.setDateTime(idleStartTime, startTime);
        TimeUtil.setDateTime(idleEndTime, startTime);
        Times boundary = new Times(idleStartTime, idleEndTime);
        List<Times> subList = new ArrayList<>();
        QueryWrapper<MeetingEntity> targetDayMeetings = new QueryWrapper<MeetingEntity>().eq("room_id", roomId)
            .eq("meeting_status", 1)
            .between("start_time", idleStartTime, idleEndTime)
            .between("end_time", idleStartTime, idleEndTime);
        List<MeetingEntity> meetings = this.list(targetDayMeetings);
        subList.add(boundary);
        for (MeetingEntity item : meetings) {
            Date meetingStartTime = item.getStartTime();
            Date meetingEndTime = item.getEndTime();
            for (Times times : subList) {
                if (meetingStartTime.getTime() >= times.getStartTime().getTime() && meetingEndTime.getTime() <= times.getEndTime().getTime()) {
                    subList.remove(times);
                    if (meetingStartTime.getTime() > times.getStartTime().getTime()) {
                        subList.add(new Times(times.getStartTime(), meetingStartTime));
                    }
                    if (meetingEndTime.getTime() < times.getEndTime().getTime()) {
                        subList.add(new Times(meetingEndTime, times.getEndTime()));
                    }
                    break;
                }
            }
        }
        while (!priorityQueue.isEmpty()) {
            MeetingEntity meeting = priorityQueue.poll();
            // 在该会议室当天的空闲时间段中寻找合适的时间段
            for (Times times : subList) {
                if (meeting.getEndTime().getTime() - meeting.getStartTime().getTime() <= times.getEndTime().getTime() - times.getStartTime().getTime()) {
                    subList.remove(times);
                    if (meeting.getEndTime().getTime() < times.getEndTime().getTime()) {
                        subList.add(new Times(meeting.getEndTime(), times.getEndTime()));
                    }
                    doSaveMeeting(meeting, roomId, times.getStartTime());
                    break;
                }
            }
            // 该会议室当天无合适时间段，通知用户自行调整会议时间
            if (meeting.getEndTime().getTime() - meeting.getStartTime().getTime() > subList.get(subList.size() - 1).getEndTime().getTime() - subList.get(subList.size() - 1).getStartTime().getTime()) {
                this.removeById(meeting.getMeetingId());
                // 目前未备案，无SMS服务，故打印日志
                // TODO 发送短信通知用户自行调整会议时间
                log.info("因会议室智能调度，您的{}会议已取消，请自行选择其他时间", meeting.getTitle());
            }
        }
    }

    private class ReserveTimerTask extends TimerTask {
        private final MeetingVo meetingVo;
        private final long createTime;

        public ReserveTimerTask(MeetingVo meetingVo, long createTime) {
            this.meetingVo = meetingVo;
            this.createTime = createTime;
        }

        @Override
        public void run() {
            doReserveMeeting(meetingVo, createTime);
        }
    }

    private class RemindTimerTask extends TimerTask {

        private final List<String> phones;
        private final String meetingName;
        private final String roomName;

        public RemindTimerTask(List<String> phones, String meetingName, String roomName) {
            this.phones = phones;
            this.meetingName = meetingName;
            this.roomName = roomName;
        }

        @Override
        public void run() {
            phones.forEach(phone -> {
                // 发送短信通知，目前无备案，无法使用SMS服务，故打印在控制台代替
                log.info("发送短信通知给{}通知：{}将于30分钟后在{}开始", phone, meetingName, roomName);
            });
        }
    }

    @Data
    private class Times {
        private final Date startTime;
        private final Date endTime;
    }

}