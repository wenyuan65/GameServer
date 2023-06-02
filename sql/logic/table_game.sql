drop table if exists `global_value`;
create table `global_value` (
	`id` int(10) unsigned not null comment 'id',
	`param1` varchar(128) not null default '' comment '参数1',
	`param2` bigint(20) unsigned not null default '0' comment '参数2',
    `param3` datetime unsigned not null default '0' comment '参数3',
	`intro` varchar(50) not null default '' comment '描述',
	primary key(`id`)
);

drop table if exists `version`;
create table `version` (
	`version` varchar(20) not null default '' comment '版本号',
);
insert into `version`(`version`) value('0.0.0.0');

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

update `version` set `version` = '0.0.0.1';