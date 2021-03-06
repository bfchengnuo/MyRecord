# Linux下使用ip替代ifconfig

2009年 Debian 开发者邮件列表宣布放弃使用缺乏维护的 net-tools 工具包，到今天 net-tools 仍然被部分人们所使用。

**由于 net-tools 不再维护，强烈建议以 ip 命令代替 ifconfig。**

最起码，你得知道这个新的 ip 命令！新版的 CentOS Mini 已经不再预装 net-tools，Docker 版的 ubuntu 也不再支持，所以很有必要学习下新的 ip 命令。

不管是 ip 还是 ifconfig 我们也就是干下面的几件事：

- 列出系统上配置了哪些网络接口
- 查看网络接口的状态
- 配置网络接口（包括本地环路和以太网）
- 启用或禁用网络接口
- 管理默认静态路由
- IP 隧道配置
- 配置 ARP 或 NDISC 缓存条目

## 收集信息

用的最频繁的就是查看分配的 ip 等信息了吧，以前只需要输入 ifconfig 即可，现在也有同样简单的命令：

`ip a`

如果只想看 ipv4 的信息只需要：

`ip -4 a`

查看特定网络接口的信息(比如无线)：

`ip a show wlan0`

列出正在运行的网络接口：

`ip link ls up`

## 修改配置

这是常用的第二大功能，下面就比较来说：

修改 ip 为指定的地址，ifconfig 是这样的：

`ifconfig eth0 192.168.1.101`

那么用 ip 命令却是这样的。

`ip a add 192.168.1.101/255.255.255.0 dev eth0`

简短一点可以这样：

`ip a add 192.168.1.101/24 dev eth0`

显然这样的话，你需要**知道你要安排的地址的子网掩码**。

> 还记得么？ipv4 是由 32 位构成，分为四段，每段 8 位，`1111 1111` 就是最大值换成十进制是 255
>
> x.x.x.x/24 的意思就是前 24 位是网络地址，也就是最后一段才是主机地址。

同样的方式，你可以这样删除一个网卡的地址:

`ip a del 192.168.1.101/24 dev eth0`

如果你想简单的清除**所有接口**上的**所有地址**，只需要这样即可。

`ip -s -s a f to 192.168.1.0/24`

ip 命令另一方面还能激活/禁用网络接口。

- 禁用 eth0

  `ip link set dev eth0 down`

- 激活 eth0

  `ip link set dev eth0 up`

使用 ip 命令，我们还可以添加/删除默认的网关，就像这样:

`ip route add default via 192.168.1.254`

如果你想获得网络接口的更多细节，你可以编辑传输队列，给速度慢的接口设置一个低值，给速度快的设置一个较高值。那么你需要像这样做 :

`ip link set txqueuelen 10000 dev eth0`

该命令设置了一个很长的传输队列。你应该设置一个最适合你硬件的值。

还可以用 ip 命令为网络接口设置最大传输单元。

`ip link set mtu 9000 dev eth0`

一旦你做了改变，便可以使用 `ip a list eth0` 来检验是否生效。

## 管理路由

其实还可以使用 ip 命令来管理系统路由表。这是 ip 命令非常有用的一个功能。并且你应该小心使用。

查看所有路由表：

`ip r`

现在你想要路由的所有流量从 eth0 网卡的 192.168.1.254 网关通过，那么请这样做：

`ip route add 192.168.1.0/24 dev eth0`

删除这个路由：

`ip route del 192.168.1.0/24 dev eth0`



来自：https://linuxstory.org/replacing-ifconfig-with-ip/