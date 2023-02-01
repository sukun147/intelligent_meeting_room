DROP
    DATABASE IF EXISTS `imrms`;

CREATE
    DATABASE `imrms`;

USE
    `imrms`;

CREATE TABLE `admin`
(
    `admin_id`     bigint       NOT NULL COMMENT '管理员id',
    `username`     varchar(255) NOT NULL COMMENT '用户名',
    `password`     varchar(255) NOT NULL COMMENT '加密后密码',
    `phone_number` char(11)     NOT NULL COMMENT '手机号',
    `email`        varchar(255) NOT NULL COMMENT '邮箱',
    PRIMARY KEY (`admin_id`),
    INDEX `admin` (`username`, `password`) USING BTREE INVISIBLE
);

INSERT INTO `admin`
values (0, 'admin', '$2a$10$kS.kKUlgqWi56/Luazz0t.GEEGzPDWAFnu4jE2XLC6KJymkxSawoi', '19135055247', '838a@2udj.net');
-- 此处手机号和邮箱均为随机生成

CREATE TABLE `meeting`
(
    `meeting_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '会议id',
    `title`               varchar(255) NOT NULL COMMENT '会议标题',
    `start_time`          datetime     NOT NULL COMMENT '会议开始时间',
    `end_time`            datetime     NOT NULL COMMENT '会议结束时间',
    `create_user_id`      bigint       NOT NULL COMMENT '会议发起人',
    `participants`        json         NOT NULL COMMENT '参会人员及其签到情况', -- 格式：[{"userId":1,"signStatus":0}]
    `report_address`      varchar(255) COMMENT '会议报告地址',
    `scheduled_period`    varchar(255) COMMENT '预定周期，cron格式（用于按日、周、月预定）',
    `meeting_status`      tinyint      NOT NULL default 1 COMMENT '启用状态（0为禁用，1为启用）',
    `room_id`             bigint       NOT NULL COMMENT '会议室id',
    `meeting_description` longtext COMMENT '会议描述',
    PRIMARY KEY (`meeting_id`),
    INDEX `room_id` (`room_id`) USING BTREE INVISIBLE,
    INDEX `create_user_id` (`create_user_id`) USING BTREE INVISIBLE,
    INDEX `meeting_title` (`title`) USING BTREE INVISIBLE
);

CREATE TABLE `meeting_room`
(
    `room_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `position`         varchar(255) NOT NULL COMMENT '地址',
    `permission_level` integer      NOT NULL COMMENT '允许权限等级',
    `idle_start_time`  time         NOT NULL COMMENT '空闲开始时间',
    `idle_end_time`    time         NOT NULL COMMENT '空闲结束时间',
    `type_id`          integer      NOT NULL COMMENT '会议室类型',
    `room_status`      tinyint      NOT NULL default 1 COMMENT '启用状态（0为禁用，1为启用）',
    `room_sort`        integer      NOT NULL COMMENT '会议室排序',
    PRIMARY KEY (`room_id`),
    INDEX `type_id` (`type_id`) USING BTREE INVISIBLE,
    INDEX `room_sort` (`room_sort`) USING BTREE INVISIBLE
);

CREATE TABLE `meeting_room_type`
(
    `type_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '类型id',
    `type_name`        varchar(255) NOT NULL COMMENT '会议室名称',
    `type_description` longtext     NOT NULL COMMENT '会议室描述',
    `equipment`        json         NOT NULL COMMENT '会议室设备',
    `type_status`      tinyint      NOT NULL default 1 COMMENT '启用状态（0为禁用，1为启用）',
    `type_sort`        integer      NOT NULL COMMENT '会议室类型排序',
    PRIMARY KEY (`type_id`),
    INDEX `type_sort` (`type_sort`) USING BTREE INVISIBLE
);

CREATE TABLE `user`
(
    `user_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '员工id',
    `username`         varchar(255) NOT NULL COMMENT '用户名',
    `password`         varchar(255) NOT NULL COMMENT '加密后密码',
    `real_name`        varchar(255) NOT NULL COMMENT '真实姓名',
    `phone`            char(11)     NOT NULL COMMENT '电话号码',
    `permission_level` integer      NOT NULL COMMENT '权限等级（1为普通员工）',
    `status`           tinyint      NOT NULL default 1 COMMENT '启用状态（0为禁用，1为启用）',
    PRIMARY KEY (`user_id`),
    INDEX `user` (`username`, `password`) USING BTREE INVISIBLE
);