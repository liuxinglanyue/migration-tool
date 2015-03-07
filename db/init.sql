/*============================================*/
/*table:migration_id_segment               */
/*info:迁移表，存储id段                         */
/*========================================*/
drop table if exists `migration_id_segment`;
create table `migration_id_segment` (
  `id` int(11) not null auto_increment comment '主键id',
  `tables` varchar(32) not null comment '数据库表名',
  `min` int(11) default 0 comment '开始id',
  `max` int(11) default 0 comment '结束id',
  `status` int(11) default 0 comment '状态 0:准备 1:失败 2:成功(删除)',
  `create_time` datetime default null comment '创建时间',
  primary key (`id`),
  UNIQUE KEY `UK_segment` (`tables`,`min`,`max`) 
) engine=innodb default charset=utf8 comment='迁移表，存储id段';

/*============================================*/
/*table:migration_id_current               */
/*info:迁移表，存储当前id                         */
/*========================================*/
drop table if exists `migration_id_current`;
create table `migration_id_current` (
  `id` int(11) not null auto_increment comment '主键id',
  `tables` varchar(32) not null comment '数据库表名',
  `current` int(11) default 0 comment '当前id, id=-1说明迁移完成',
  `ability` int(11) default 0 comment '能力值',
  `create_time` datetime default null comment '创建时间',
  primary key (`id`),
  UNIQUE KEY `UK_tables` (`tables`) 
) engine=innodb default charset=utf8 comment='迁移表，存储当前id';


/*============================================*/
/*table:migration_device               */
/*info:迁移组件(改为存储于 内存中)               */
/*========================================*/
/*drop table if exists `migration_device`;
create table `migration_device` (
  `id` int(11) not null auto_increment comment '主键id',
  `devicename` varchar(100) not null comment '设备名称，pid@hostname@ip',
  `threadname` varchar(100) not null comment '线程名称',
  `tables` varchar(32) not null comment '数据库表名',
  `ability` int(11) default 0 comment '能力值',
  `create_time` datetime default null comment '创建时间',
  `update_time` datetime default null comment '更新时间',
  primary key (`id`),
  UNIQUE KEY `UK_device` (`devicename`,`tables`) 
) engine=innodb default charset=utf8 comment='迁移表，存储当前id';*/
