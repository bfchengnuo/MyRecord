# MySQL中的函数

今天遇到的一个需求，之前因为要做权限控制，并且它这个设计好像是有问题的，通过简单的 sql 连接无法完全过滤，最开始的方案，我配合 J8 点 stream 流进行了 3 次过滤才完成，我就称它为二次过滤吧。

但是呢，后来发现一个问题，这样办的话，分页就难弄了，仔细想了想也没想到有什么好的解决方案，基本是个死局。

想要解决，也就只能从 SQL 上下手，于是我想着把 Java 代码转换为 Mysql 的函数，额效率啥的先不考虑了。。。。

下面是一个模版：

``` sql
CREATE DEFINER=`skip-grants user`@`skip-grants host` FUNCTION `isMatchRange`(S_IDS VARCHAR(3000), pid bigint) RETURNS int(1)
BEGIN
	-- 定义变量
  DECLARE s int DEFAULT 0;
	DECLARE PID_C_ID BIGINT;
	DECLARE flag INT DEFAULT 0;
	
	-- 定义游标，并将sql结果集赋值到游标中
	DECLARE report CURSOR FOR SELECT ROW_ID FROM sm_group where GROUP_PID = pid and DELETED_FLAG='0';
	-- 声明当游标遍历完后将标志变量置成某个值
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET s=1;

	-- 打开游标
	open report;
		-- 将游标中的值赋值给变量，注意：变量名不要和返回的列名同名，变量顺序要和sql结果列的顺序一致
		fetch report into PID_C_ID;
		-- 当s不等于1，也就是未遍历完时，会一直循环
		while s<>1 do
			-- 执行业务逻辑-使用 find_in_set 函数判断 PID_C_ID 是否存在于 S_IDS 中
			IF !find_in_set(PID_C_ID, S_IDS) THEN
				SET flag = 1;
			END IF;
			
			-- 将游标中的值再赋值给变量，供下次循环使用
			fetch report into PID_C_ID;
		end while;
	-- 关闭游标
	close report;

	RETURN flag;
END
```

我感觉基本上很全了，判断循环变量赋值都有了，差点都忘记游标这回事了。