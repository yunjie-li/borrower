
DROP DATABASE IF EXISTS borrower;
DROP DATABASE IF EXISTS borrower1;
DROP DATABASE IF EXISTS borrower2;
DROP DATABASE IF EXISTS borrower3;

CREATE DATABASE borrower;
CREATE DATABASE borrower1;
CREATE DATABASE borrower2;
CREATE DATABASE borrower3;

DROP TABLE IF EXISTS borrower.user;
CREATE TABLE borrower.user
(
	id bigint auto_increment comment '主键id'
		primary key,
	account varchar(96) not null comment '用户名',
	name varchar(96) not null comment '姓名',
	cellphone varchar(96) not null comment '手机号',
	email varchar(128) null comment '邮箱',
	create_date datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	update_date datetime default CURRENT_TIMESTAMP not null comment '更新时间'
)
;

DROP TABLE IF EXISTS borrower1.user;
CREATE TABLE borrower1.user
(
	id bigint auto_increment comment '主键id'
		primary key,
	account varchar(96) not null comment '用户名',
	name varchar(96) not null comment '姓名',
	cellphone varchar(96) not null comment '手机号',
	email varchar(128) null comment '邮箱',
	create_date datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	update_date datetime default CURRENT_TIMESTAMP not null comment '更新时间'
)
;

DROP TABLE IF EXISTS borrower2.user;
CREATE TABLE borrower2.user
(
	id bigint auto_increment comment '主键id'
		primary key,
	account varchar(96) not null comment '用户名',
	name varchar(96) not null comment '姓名',
	cellphone varchar(96) not null comment '手机号',
	email varchar(128) null comment '邮箱',
	create_date datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	update_date datetime default CURRENT_TIMESTAMP not null comment '更新时间'
)
;

DROP TABLE IF EXISTS borrower3.user;
CREATE TABLE borrower3.user
(
	id bigint auto_increment comment '主键id'
		primary key,
	account varchar(96) not null comment '用户名',
	name varchar(96) not null comment '姓名',
	cellphone varchar(96) not null comment '手机号',
	email varchar(128) null comment '邮箱',
	create_date datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	update_date datetime default CURRENT_TIMESTAMP not null comment '更新时间'
)
;

INSERT INTO borrower.user (account, name, cellphone, email, create_date, update_date) VALUES ('yunjie1', '哈哈', '15434245753', '29445346@qq.com', '2017-09-30 18:23:01', '2017-10-13 20:25:57');
INSERT INTO borrower.user (account, name, cellphone, email, create_date, update_date) VALUES ('yunjie', '哈哈', '15434245753', '29445346@qq.com', '2017-09-30 18:23:01', '2017-09-30 18:23:01');
