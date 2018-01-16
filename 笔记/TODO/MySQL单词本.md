having 所有；持有

limit 限制

columns 列

group 群；组

order by 按顺序（ASC|DESC）

ascending 上升的

dump 倾倒 （还原）

distinct 清楚的，不同的；独特的

truncate 截短，被删节的

form 表格

from 从...来

modify 改装；修改

alert 警报

alter 改变

select 选择（查询）

insert 插入

delete 删除

where 哪里

any 任何

some 一些

inner 内

join 加入 [inner join = 内连接]

primary 主

foreign 国外；对外 [foreign key = 外键]

references 引用；参考

delimiter 分隔符

begin 开始

procedure 程序 （存储过程）

unsigned 无符号

transaction 事务

rollback 回滚

commit 提交

unique 唯一

---


total 总；总计

增删改查：
``` sql
insert into tb values(v1,v2,v3);
delete from tb where name='v';
update tb set name='new' where id=1;
select * from tb;
select distinct columns from tb;
```
// group by(having)  order by
删除：
``` sql
drop table tb;
drop database db;
truncate table tb;
```
创建：
``` sql
create database db;
create table tb
(
	columns1 varchar(10),
	columns2 int not null,
	id int primary key not null unique
);
```
