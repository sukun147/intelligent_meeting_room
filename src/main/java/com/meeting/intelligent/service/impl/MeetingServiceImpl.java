package com.meeting.intelligent.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.baidu.aip.face.AipFace;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.CronFieldName;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.dao.MeetingDao;
import com.meeting.intelligent.entity.MeetingEntity;
import com.meeting.intelligent.entity.MeetingRoomEntity;
import com.meeting.intelligent.entity.UserEntity;
import com.meeting.intelligent.service.MeetingRoomService;
import com.meeting.intelligent.service.MeetingService;
import com.meeting.intelligent.service.UserService;
import com.meeting.intelligent.thirdParty.FaceClient;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;
import com.meeting.intelligent.utils.TimeUtil;
import com.meeting.intelligent.vo.MeetingRespVo;
import com.meeting.intelligent.vo.MeetingVo;
import com.meeting.intelligent.vo.Participant;
import com.meeting.intelligent.vo.SignInVo;
import jakarta.annotation.PostConstruct;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.*;


@Slf4j
@Service("meetingService")
public class MeetingServiceImpl extends ServiceImpl<MeetingDao, MeetingEntity> implements MeetingService {
    private static final int THIRTY_MINUTES = 30 * 60 * 1000;
    private static final int ONE_DAY = 24 * 60 * 60 * 1000;
    private static final int TWO_DAY = 48 * 60 * 60 * 1000;
    private static final int ONE_WEEK = 7 * 24 * 60 * 60 * 1000;
    @Autowired
    private MeetingRoomService meetingRoomService;
    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;
    private Cache<String, MeetingRespVo> meetingCache;

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("meeting_cache_").expire(Duration.ofHours(1))
            .cacheType(CacheType.REMOTE).build();
        meetingCache = cacheManager.getOrCreateCache(qc);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingRespVo> page = this.page(new Query<MeetingEntity>().getPage(params), new QueryWrapper<>()).convert(
            meetingEntity -> {
            MeetingRespVo meetingRespVo = new MeetingRespVo();
            BeanUtils.copyProperties(meetingEntity, meetingRespVo);
            return meetingRespVo;
        });
        List<MeetingRespVo> records = page.getRecords();
        Set<Long> roomIds = new HashSet<>();
        records.forEach(record -> roomIds.add(record.getRoomId()));
        Map<Long, String> roomMap = meetingRoomService.listByIds(roomIds).stream().collect(
            HashMap::new, (m, v) -> m.put(v.getRoomId(), v.getPosition()), HashMap::putAll
        );
        records.forEach(record -> record.setRoomPosition(roomMap.get(record.getRoomId())));
        page.setRecords(records);
        return new PageUtils(page);
    }

    /**
     * 预定会议入口：校验参数，根据预定发起时间调度预定
     */
    @Override
    public void add(MeetingVo meetingVo) {
        meetingVo.setMeetingId(null);
        Date startTime = meetingVo.getStartTime();
        Date endTime = meetingVo.getEndTime();
        if (startTime.after(endTime)) {
            throw new GlobalException("非法会议时间", PARAMETER_CHECK_EXCEPTION.getCode());
        }
        String period = meetingVo.getScheduledPeriod();
        if (StringUtils.isNotBlank(period) && !CronExpression.isValidExpression(period)) {
            throw new GlobalException("非法的周期规则", PARAMETER_CHECK_EXCEPTION.getCode());
        }
        MeetingRoomEntity room = meetingRoomService.getById(meetingVo.getRoomId());
        if (room == null) {
            throw new GlobalException(MEETING_ROOM_NOT_EXIST_EXCEPTION);
        }
        Date idleStartTime = room.getIdleStartTime();
        Date idleEndTime = room.getIdleEndTime();
        if (TimeUtil.compareTime(idleStartTime, startTime) > 0 || TimeUtil.compareTime(idleEndTime, endTime) < 0) {
            throw new GlobalException(ILLEGAL_TIMES_EXCEPTION);
        }
        userService.checkIds(meetingVo.getParticipants().stream().map(Participant::getUserId).toList());
        meetingVo.setCreateUserId(StpUtil.getLoginIdAsLong());
        long createTime = new Date().getTime();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            QuartzJobBean reserveMeetingJob = new QuartzJobBean() {
                @Override
                protected void executeInternal(JobExecutionContext jobExecutionContext) {
                    reserveMeeting(meetingVo, createTime);
                }
            };
            String identity = "reserveMeetingJob" + meetingVo.getRoomId() + meetingVo.getStartTime().getTime();
            JobDetail jobDetail = JobBuilder.newJob(reserveMeetingJob.getClass())
                .withIdentity(identity, "reserveMeetingJobGroup").build();
            if (startTime.getTime() - createTime < TWO_DAY) {
                reserveMeeting(meetingVo, createTime);
            } else if (startTime.getTime() - createTime < ONE_WEEK) {
                Trigger trigger = TriggerBuilder.newTrigger().startAt(
                    new Date(startTime.getTime() - ONE_DAY)
                ).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                Trigger trigger;
                if (meetingVo.getAdjustAble()) {
                    trigger = TriggerBuilder.newTrigger().startAt(
                        new Date(startTime.getTime() - ONE_WEEK)
                    ).build();
                } else {
                    trigger = TriggerBuilder.newTrigger().startAt(
                        new Date(startTime.getTime() - TWO_DAY)
                    ).build();
                }
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            throw new GlobalException(SCHEDULER_EXCEPTION);
        }
    }

    @Override
    public MeetingRespVo get(Long meetingId) {
        MeetingRespVo cache = meetingCache.get(meetingId.toString());
        if (cache != null) {
            return cache;
        }
        MeetingEntity meeting = getMeetingAndCheckExist(meetingId);
        MeetingRespVo meetingRespVo = new MeetingRespVo();
        BeanUtils.copyProperties(meeting, meetingRespVo);
        meetingRespVo.setRoomPosition(meetingRoomService.getById(meeting.getRoomId()).getPosition());
        meetingCache.put(meetingId.toString(), meetingRespVo,
            55 + new Random().nextInt(10), TimeUnit.MINUTES);
        return meetingRespVo;
    }

    @Override
    public void delete(List<Long> meetingIds) {
        long loginId = StpUtil.getLoginIdAsLong();
        if (loginId != 0L) {
            this.listByIds(meetingIds).forEach(meeting -> {
                if (!meeting.getCreateUserId().equals(loginId)) {
                    throw new GlobalException(ILLEGAL_ACCESS_EXCEPTION);
                }
            });
        }
        this.removeBatchByIds(meetingIds);
        meetingCache.removeAll(meetingIds.stream().map(String::valueOf).collect(Collectors.toSet()));
    }

    @Override
    public List<MeetingRespVo> getByRoomId(Long roomId) {
        QueryWrapper<MeetingEntity> wrapper = new QueryWrapper<MeetingEntity>().eq("room_id", roomId)
            .eq("meeting_status", 1);
        List<MeetingEntity> meetingEntities = this.list(wrapper);
        Set<Long> roomIds = new HashSet<>();
        meetingEntities.forEach(meetingEntity -> roomIds.add(meetingEntity.getRoomId()));
        Map<Long, String> roomMap = meetingRoomService.listByIds(roomIds).stream().collect(
            HashMap::new, (m, v) -> m.put(v.getRoomId(), v.getPosition()), HashMap::putAll
        );
        return meetingEntities.stream().map(meetingEntity -> {
            MeetingRespVo meetingRespVo = new MeetingRespVo();
            BeanUtils.copyProperties(meetingEntity, meetingRespVo);
            meetingRespVo.setRoomPosition(roomMap.get(meetingEntity.getRoomId()));
            return meetingRespVo;
        }).toList();
    }

    @Override
    public void signIn(SignInVo signInVo) {
        Long meetingId = signInVo.getMeetingId();
        MeetingEntity meetingEntity = getMeetingAndCheckExist(meetingId);
        if (meetingEntity.getMeetingStatus() == 0) {
            throw new GlobalException(MEETING_END_EXCEPTION);
        }
        AipFace client = FaceClient.getInstance();
        JSONObject jsonObject = client.search(signInVo.getFacePhoto(), "BASE64", "1", null);
        int errorCode = jsonObject.getInt("error_code");
        if (errorCode != 0) {
            log.warn("人脸搜索失败，错误码：{}，错误信息：{}", errorCode, jsonObject.getString("error_msg"));
            throw new GlobalException(FACE_SEARCH_EXCEPTION);
        }
        String userId = jsonObject.getJSONObject("result").getJSONArray("user_list")
            .getJSONObject(0).getString("user_id");
        meetingEntity.getParticipants().forEach(participant -> {
            if (participant.getUserId().equals(Long.valueOf(userId))) {
                participant.setSignStatus(1);
            }
        });
        this.updateById(meetingEntity);
    }

    @Override
    public void finish(Long meetingId, String sign, Long timeStamp) {
        if (new Date().getTime() - timeStamp > 60 * 1000) {
            throw new GlobalException("签名已过期", PARAMETER_CHECK_EXCEPTION.getCode());
        }
        MeetingEntity meetingEntity = getMeetingAndCheckExist(meetingId);
        if (meetingEntity.getMeetingStatus() == 0) {
            throw new GlobalException(MEETING_END_EXCEPTION);
        }
        String validSign = DigestUtils.md5DigestAsHex((
            meetingId + meetingEntity.getTitle() +
                (meetingEntity.getEndTime().getTime() - meetingEntity.getStartTime().getTime()) +
                meetingEntity.getCreateUserId() + timeStamp
        ).getBytes());
        if (validSign.equals(sign)) {
            throw new GlobalException("签名错误", PARAMETER_CHECK_EXCEPTION.getCode());
        }
        meetingEntity.setMeetingStatus(0);
        this.updateById(meetingEntity);
        meetingCache.remove(meetingId.toString());
    }

    @Override
    public void updateForce(MeetingVo meetingVo) {
        MeetingEntity meetingEntity = getMeetingAndCheckExist(meetingVo.getMeetingId());
        BeanUtils.copyProperties(meetingVo, meetingEntity);
        this.updateById(meetingEntity);
        remind(meetingEntity, meetingRoomService.getById(meetingEntity.getRoomId()).getPosition());
        meetingCache.remove(meetingVo.getMeetingId().toString());
    }

    private MeetingEntity getMeetingAndCheckExist(Long meetingId) {
        MeetingEntity meeting = this.getById(meetingId);
        if (meeting == null) {
            throw new GlobalException(MEETING_NOT_EXIST_EXCEPTION);
        }
        return meeting;
    }

    /**
     * 检查会议冲突然后保存会议，根据具体情况抛出异常
     */
    private void reserveMeeting(MeetingVo meetingVo, long createTime) {
        Long roomId = meetingVo.getRoomId();
        Date startTime = meetingVo.getStartTime();
        Date endTime = meetingVo.getEndTime();
        MeetingEntity meetingEntity = new MeetingEntity();
        BeanUtils.copyProperties(meetingVo, meetingEntity);
        QueryWrapper<MeetingEntity> nowRoomQuery = new QueryWrapper<MeetingEntity>().eq("room_id", roomId)
            .eq("meeting_status", 1).and(wrapper -> wrapper
                .between("start_time", startTime, endTime)
                .or().between("end_time", startTime, endTime)
            );
        List<MeetingEntity> conflictMeetings = this.list(nowRoomQuery);
        if (conflictMeetings.size() > 0) {
            if (conflictMeetings.stream().anyMatch(meeting -> meeting.getScheduledPeriod() != null)) {
                throw new GlobalException(CONFLICT_WITH_PERIODIC_MEETING_EXCEPTION);
            }
            if (startTime.getTime() - createTime < TWO_DAY) {
                throw new GlobalException(MEETING_ROOM_OCCUPY_EXCEPTION);
            } else {
                if (meetingVo.getAdjustAble()) {
                    adjustMeeting(conflictMeetings, meetingEntity);
                } else {
                    throw new GlobalException(MEETING_ROOM_OCCUPY_EXCEPTION);
                }
            }
        } else {
            saveMeeting(meetingEntity, roomId);
        }
    }

    private void saveMeeting(MeetingEntity meetingEntity, Long roomId) {
        this.save(meetingEntity);
        String period = meetingEntity.getScheduledPeriod();
        if (StringUtils.isNotBlank(period)) {
            CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
            CronParser parser = new CronParser(cronDefinition);
            Cron cron = parser.parse(period);
            ExecutionTime executionTime = ExecutionTime.forCron(cron);
            Optional<ZonedDateTime> nextExecutionTime = executionTime.nextExecution(ZonedDateTime.now());
            nextExecutionTime.ifPresent(zonedDateTime -> {
                MeetingEntity nextMeeting = new MeetingEntity();
                BeanUtils.copyProperties(meetingEntity, nextMeeting);
                nextMeeting.setStartTime(Date.from(zonedDateTime.toInstant()));
                Date nextMeetingStartTime = nextMeeting.getStartTime();
                long duration = nextMeeting.getEndTime().getTime() - nextMeetingStartTime.getTime();
                nextMeeting.setEndTime(new Date(nextMeetingStartTime.getTime() + duration));
                nextMeeting.setMeetingId(null);
                QuartzJobBean periodicReserveJob = new QuartzJobBean() {
                    @Override
                    protected void executeInternal(JobExecutionContext jobExecutionContext) {
                        saveMeeting(nextMeeting, roomId);
                    }
                };
                try {
                    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                    Long nextMeetingMeetingId = nextMeeting.getMeetingId();
                    JobDetail jobDetail = JobBuilder.newJob(periodicReserveJob.getClass()).withIdentity(
                        "periodicReserveJob" + nextMeetingMeetingId, "periodicReserveJobGroup"
                    ).build();
                    String hourString = cron.retrieve(CronFieldName.HOUR).getExpression().asString();
                    String execPeriod = period.replace(hourString, String.valueOf(4));
                    Optional<ZonedDateTime> firstExecZonedDateTime = ExecutionTime.forCron(parser.parse(execPeriod))
                        .nextExecution(ZonedDateTime.now());
                    if (firstExecZonedDateTime.isPresent()) {
                        // 启动时间为第一次执行时间减去一小时
                        long schedulerStartTime = Date.from(firstExecZonedDateTime.get().toInstant()).getTime()
                            - 1000 * 60 * 60;
                        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(
                            "periodicReserveTrigger" + nextMeetingMeetingId, "periodicReserveTriggerGroup"
                        ).withSchedule(CronScheduleBuilder.cronSchedule(execPeriod)).startAt(
                            new Date(schedulerStartTime)
                        ).build();
                        scheduler.scheduleJob(jobDetail, trigger);
                    }
                } catch (SchedulerException e) {
                    throw new GlobalException(SCHEDULER_EXCEPTION);
                }
            });
        }
        remind(meetingEntity, meetingRoomService.getById(roomId).getPosition());
    }

    private void remind(MeetingEntity meetingEntity, String position) {
        List<Participant> participants = meetingEntity.getParticipants();
        List<Long> userIds = participants.stream().map(Participant::getUserId).toList();
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<UserEntity>().in("user_id", userIds);
        List<String> phones = userService.list(queryWrapper).stream().map(UserEntity::getPhone).toList();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            QuartzJobBean remindJob = new QuartzJobBean() {
                @Override
                protected void executeInternal(JobExecutionContext jobExecutionContext) {
                    phones.forEach(phone -> {
                        // TODO 发送短信通知
                        // 目前无备案，无法使用SMS服务，故打印在控制台代替
                        log.info("发送短信通知给{}通知：{}将于30分钟后在{}开始", phone, meetingEntity.getTitle(), position);
                    });
                }
            };
            JobDetail jobDetail = JobBuilder.newJob(remindJob.getClass()).build();
            Trigger trigger = TriggerBuilder.newTrigger().startAt(
                new Date(meetingEntity.getStartTime().getTime() - THIRTY_MINUTES)
            ).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void adjustMeeting(List<MeetingEntity> conflictMeetings, MeetingEntity currentMeeting) {
        // 权重=会议时长(单位小时)*会议人数+是否为周期会议*100
        PriorityQueue<MeetingEntity> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(o -> (int)
            ((o.getEndTime().getTime() - o.getStartTime().getTime()) / 1000 / 60 / 60 * o.getParticipants().size()
                + (o.getScheduledPeriod() != null ? 100 : 0))
        ));
        priorityQueue.addAll(conflictMeetings);
        priorityQueue.add(currentMeeting);
        MeetingEntity priorityMeeting = priorityQueue.poll();
        Long roomId = priorityMeeting.getRoomId();
        Date startTime = priorityMeeting.getStartTime();
        saveMeeting(priorityMeeting, roomId);
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
                if (meetingStartTime.after(times.getStartTime()) && meetingEndTime.before(times.getEndTime())) {
                    subList.remove(times);
                    subList.add(new Times(times.getStartTime(), meetingStartTime));
                    subList.add(new Times(meetingEndTime, times.getEndTime()));
                    break;
                } else if (meetingStartTime.after(times.getStartTime()) && meetingEndTime.after(times.getEndTime())) {
                    subList.remove(times);
                    subList.add(new Times(times.getStartTime(), meetingStartTime));
                    break;
                } else if (meetingStartTime.before(times.getStartTime()) && meetingEndTime.before(times.getEndTime())) {
                    subList.remove(times);
                    subList.add(new Times(meetingEndTime, times.getEndTime()));
                    break;
                }
            }
        }
        while (!priorityQueue.isEmpty()) {
            MeetingEntity meeting = priorityQueue.poll();
            // 在该会议室当天的空闲时间段中寻找合适的时间段
            for (Times times : subList) {
                if (meeting.getEndTime().getTime() - meeting.getStartTime().getTime()
                    <= times.getEndTime().getTime() - times.getStartTime().getTime()) {
                    subList.remove(times);
                    subList.add(new Times(times.getStartTime(), meeting.getEndTime()));
                    subList.add(new Times(meeting.getEndTime(), times.getEndTime()));
                    saveMeeting(meeting, roomId);
                    break;
                }
            }
            // 该会议室当天无合适时间段，通知用户自行调整会议时间
            Times lastTimesInSubList = subList.get(subList.size() - 1);
            if (meeting.getEndTime().getTime() - meeting.getStartTime().getTime()
                > lastTimesInSubList.getEndTime().getTime() - lastTimesInSubList.getStartTime().getTime()) {
                this.removeById(meeting.getMeetingId());
                // 目前未备案，无SMS服务，故打印日志
                // TODO 发送短信通知用户自行调整会议时间
                log.info("因会议室智能调度，您的{}会议已取消，请自行选择其他时间", meeting.getTitle());
            }
        }
    }

    @Value
    private static class Times {
        Date startTime;
        Date endTime;
    }

}