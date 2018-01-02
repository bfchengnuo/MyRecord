因为 RabbitMQ 是基于 erlang 开放的，所以需要安装 erlang 环境

## 安装erlang

我使用 yum 命令可以安装：

`yum install erlang`

如果没有这个源，可以尝试进行添加或者使用 rpm 包来安装

安装完成后可以使用 `erl -version` 命令来测试是否成功

## 安装RabbitMQ

首先从官网下载 rpm 包：

`wget https://dl.bintray.com/rabbitmq/rabbitmq-server-rpm/rabbitmq-server-3.6.12-1.el7.noarch.rpm`

安装RabbitMQ Server:

``` shell
rpm --import http://www.rabbitmq.com/rabbitmq-signing-key-public.asc 
yum install rabbitmq-server-3.6.12-1.el7.noarch.rpm
```

## 启动RabbitMQ

顺便可以设置下自启动：

``` shell
chkconfig rabbitmq-server on
# cent7+后使用
# systemctl enable rabbitmq-server.service

service rabbitmq-server start
# cent7+后使用：
# systemctl start rabbitmq-server
```

如果出现下面的异常：

> Starting rabbitmq-server (via systemctl):  Job for rabbitmq-server.service failed. See 'systemctl status rabbitmq-server.service' and 'journalctl -xn' for details. [FAILED]

尝试禁用 SELinux ，修改 /etc/selinux/config ：
SELINUX=disabled ；
修改后重启系统

## 安装Web管理界面插件

终端输入： `rabbitmq-plugins enable rabbitmq_management`

如果出现下面的提示表示成功：

> The following plugins have been enabled:
>
>  mochiweb
>
>  webmachine
>
>  rabbitmq_web_dispatch
>
>  amqp_client
>
>  rabbitmq_management_agent
>
>  rabbitmq_management
>
> Plugin configuration has changed. Restart RabbitMQ for changes to take effect.

这样就可以使用 127.0.0.1:15672 进行访问了，最高权限的账号是 guest ，密码同

## 设置RabbitMQ远程ip登录

因为安全的原因，Guest 账户只有使用 localhost 访问才可以登录，所以当使用远程登录时需要新建一个账户；

下面来新建一个管理员账户

``` shell
# 创建一个用户
rabbitmqctl add_user test 123456
# 设置用户角色
rabbitmqctl  set_user_tags  test  administrator
# 设置用户权限
rabbitmqctl set_permissions -p "/" test ".*" ".*" ".*"
# 设置完成后可以查看当前用户和角色(需要开启服务)
rabbitmqctl list_users
```

用户角色有五类，一个用户可以设置多个角色：

1. 超级管理员(administrator)

   可登陆管理控制台(启用management plugin的情况下)，可查看所有的信息，并且可以对用户，策略(policy)进行操作。
   
2. 监控者(monitoring)

   可登陆管理控制台(启用management plugin的情况下)，同时可以查看rabbitmq节点的相关信息(进程数，内存使用情况，磁盘使用情况等)
   
3. 策略制定者(policymaker)

   可登陆管理控制台(启用management plugin的情况下), 同时可以对policy进行管理。但无法查看节点的相关信息(上图红框标识的部分)。与administrator的对比，administrator能看到这些内容。

4. 普通管理者(management)

   仅可登陆管理控制台(启用management plugin的情况下)，无法看到节点信息，也无法对策略进行管理。
  
5. 其他

   无法登陆管理控制台，通常就是普通的生产者和消费者。
  
  ## 参考
  
  http://www.jianshu.com/p/e3af4cf97820
