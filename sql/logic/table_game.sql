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

drop table if exists `player`;
create table `player` (
	`player_id` bigint(20) unsigned not null comment '玩家id',
	`player_name` varchar(20) not null default '' comment '名称',
	`lv` int(10) unsigned not null default '0' comment '等级',
	`male` tinyint(4) unsigned not null default '1' comment '性别，1男2女',
	`pic` varchar(20) not null default '' comment '角色图片',
	`user_id` bigint(20) unsigned not null comment 'user表id',
	`yx_user_id` varchar(100) not null default '' comment '玩家联运标识',
	`yx` varchar(20) not null default '' comment '联运',
	`channel_id` varchar(50) not null default '' comment '渠道',
	`create_time` datetime not null default '2000-01-01 00:00:00' comment '创建日期',
	primary key(`player_id`),
	index `idx_userId` (`user_id`) using btree
);

update `global_value` set `value` = '0.0.1.0' where `id` = 1;
