drop table if exists `test`;

create table `test` (
       `id` bigint not null comment  'id',
       `name` varchar(50) comment '名称',
       `password` varchar(50) comment '密码',
       primary key (`id`)
)engine=innodb default  charset=utf8mb4 comment='测试';#记得用innodb引擎,Innodb是mysql的引擎之一

insert into `test` (id,name,password) values(1,'测试','123')
