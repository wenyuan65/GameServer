drop table if exists `version`;
create table `version` (
	`version` varchar(20) not null default '' comment '版本号',
);
insert into `version`(`version`) value('0.0.0.0');

drop table if exists `user`;
create table `user` (
	`id` bigint(20) unsigned not null comment '玩家id',
	`user_name` varchar(20) not null default '' comment '名称',
	`password` varchar(20) not null default '' comment '密码',
	`yx_user_id` varchar(100) not null default '' comment '玩家联运标识',
	`yx` varchar(20) not null default '' comment '联运',
	`channel_id` varchar(50) not null default '' comment '渠道',
	`create_time` datetime not null default '2000-01-01 00:00:00' comment '创建日期',
	primary key(`id`),
	index `idx_userId_yx` (`yx_user_id`, `yx`) using btree,
	index `idx_user_name` (`user_name`) using btree
);

-- 更新版本号
update `version` set `version` = '0.0.0.1';