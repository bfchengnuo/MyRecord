# MySQL 服务无法启动-3534解决及安装
<br>
<br>
<br>

## SQL安装

在配置文件写入下列信息
	
	[mysql]
	# 设置mysql客户端默认字符集
	default-character-set=utf8 
	[mysqld]
	#设置3306端口
	port = 3306 
	# 设置mysql的安装目录
	basedir=D:\mysql\mysql-5.6.17-winx64
	# 设置mysql数据库的数据的存放目录
	datadir=D:\mysql\mysql-5.6.17-winx64\data
	# 允许最大连接数
	max_connections=200
	# 服务端使用的字符集默认为8比特编码的latin1字符集
	character-set-server=utf8
	# 创建新表时将使用的默认存储引擎
	default-storage-engine=INNODB 

CMD管理员运行进入bin目录执行**mysqld install**即可

<br>
<br>

##服务无法启动

成功安装以后，启动MySQL，输入：

>net start mysql

提示：


>”MySQL 服务无法启动，服务没有报告任何错误，请键入 NET HELPMSG 3534 以获得更多的帮助。”

查了下，在当前目录下输入：


~~`mysqld  --initialize`~~

`mysqld --initialize-insecure`


还是出错，又查了下，原因是：

	mysqld --initialize-insecure自动生成无密码的root用户，mysqld --initialize自动生成带随机密码的root用户。data文件夹不为空是不能执行这个命令的。

解决办法：

先删除data目录下的所有文件或者移走。

<br>
<br>

## 修改密码

MySQL 的“root”用户默认状态是没有密码的，所以在 PHP 中您可以使用mysql_connect("localhost","root","") 来连接 MySQL 服务器；

如果您想为 MySQL 中的“root”用户设置密码（例如：本机MySQL密码为 123456），请在控制台中使用“mysqladmin”命令。例如：

`d:\PHP\xampp\mysql\bin\mysqladmin.exe  -u  root  password  123456`

另外，如果是先前有秘密，则修改命令为：

`d:\PHP\xampp\mysql\bin\mysqladmin.exe -u root -p password 123456`

回车后提示你输入密码，输入“旧密码”即可。