
drop table if exists `global_value`;
create table `global_value` (
	`id` int(10) unsigned not null comment 'id',
	`value` varchar(128) not null default '' comment '值',
	`param1` bigint(20) not null default '0' comment '参数1',
	`param2` bigint(20) not null default '0' comment '参数2',
	`param3` bigint(20) not null default '0' comment '参数3',
    `time` datetime not null default '2020-01-01 00:00:00' comment '时间参数',
	`intro` varchar(50) not null default '' comment '描述',
	primary key(`id`)
);

insert into `global_value`(`id`, `value`, `intro`) value(1, '0.0.0.0', '数据库版本号');

drop table if exists `user`;
create table `user` (
	`id` bigint(20) unsigned not null comment '玩家id',
	`user_name` varchar(20) not null default '' comment '名称',
	`password` varchar(20) not null default '' comment '密码',
	`yx_user_id` varchar(100) not null default '' comment '玩家联运标识',
	`yx` varchar(20) not null default '' comment '联运',
	`channel_id` varchar(50) not null default '' comment '渠道',
	`logic_node_id` int(10) unsigned not null default '0' comment '逻辑服的id',
	`create_time` datetime not null default '2000-01-01 00:00:00' comment '创建日期',
	primary key(`id`),
	index `idx_userId_yx` (`yx_user_id`, `yx`) using btree,
	index `idx_user_name` (`user_name`) using btree
);

update `global_value` set `value` = '0.0.1.0' where `id` = 1;