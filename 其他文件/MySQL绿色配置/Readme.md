5.7.7 之后默认不再有 data 目录

> 新版的 mysql 默认没有 data 文件夹，初始化时会生成 root 密码。
>
> root 密码初始化在 data 文件夹  *.err 文件中

- Win 下已测试
- Linux 未测试

## 安装

解压后配置 my.ini ：

``` ini
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
```

将默认的 ini 文件改名 my.ini ，不改也可以，这样可以备份一下，避免玩坏了

## 初始化

> 先要保证设置的 Data 文件夹是空的

执行：

`mysqld.exe --initialize-insecure --user=mysql`

windows 下可省略 --user 选项；-insecure 表示不会创建 root 密码

---

当然也可以直接执行 `mysqld.exe --initialize` 生成的密码去设置的 data 目录找。

## 安装服务

`mysqld --install` 或者 `mysqld install`

启动服务：

`net start mysql`

## 附加

如果您想为 MySQL 中的“root”用户设置密码，请在控制台中使用“mysqladmin”命令。例如：

`mysqladmin.exe -u root password 123456`

另外，如果是先前有密码，则修改命令为：

`mysqladmin.exe -u root -p password 123456`

回车后提示你输入当前密码，确认后会被修改为新密码

## Linux

主要命令：

``` shell
groups mysql

groupadd mysql
useradd -r -g mysql mysql

cd mysql/
chown -R mysql:mysql ./

./mysqld --user=mysql --basedir=/home/mysql --datadir=/home/mysql/data --initialize

./support-files/mysql.server start
```

https://blog.csdn.net/cryhelyxx/article/details/49757217

配置相关：

``` shell
vim /etc/my.cnf

#复制以下内容

[client]
port = 3306
socket = /tmp/mysql.sock

[mysqld]
character_set_server=utf8
init_connect='SET NAMES utf8'
basedir=/usr/local/mysql
datadir=/usr/local/mysql/data
socket=/tmp/mysql.sock
log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid
#不区分大小写
lower_case_table_names = 1

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION

max_connections=5000

default-time_zone = '+8:00'
```

初始化：

``` shell
#先安装一下这个东东，要不然初始化有可能会报错
yum install libaio
#手动编辑一下日志文件，什么也不用写，直接保存退出
cd /var/log/

vim mysqld.log

chmod 777 mysqld.log
chown mysql:mysql mysqld.log

/usr/local/mysql/bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql --datadir=/usr/local/mysql/data --lc_messages_dir=/usr/local/mysql/share --lc_messages=en_US
```

http://sfau.lt/b51sVx

## 更多

[去博客](https://bfchengnuo.com/2016/03/22/MySQL%E6%9C%8D%E5%8A%A1%E6%97%A0%E6%B3%95%E5%90%AF%E5%8A%A8%E8%A7%A3%E5%86%B3%E4%BB%A5%E5%8F%8A%E5%AE%89%E8%A3%85/)