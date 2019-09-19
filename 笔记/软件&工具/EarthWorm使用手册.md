# EarthWorm使用手册

EarthWorm（简称 EW）是一套轻量便携且功能强大的网络穿透工具，基于标准 C 开发，具有 socks5 代理、端口转发和端口映射三大功能。

官网网站（已被 Google 标黑）：http://rootkiter.com/EarthWorm/

> 该工具能够以“正向”、“反向”、“多级级联”等方式打通一条网络隧道，直达网络深处，用蚯蚓独有的手段突破网络限制，给防火墙松土。

如无特殊说明代理端口均为 1080，服务均为 Socks V5 代理服务.

该工具共有 6 种命令格式（ssocksd、rcsocks、rssocks、lcx_slave、lcx_listen、lcx_tran）。

前三个负责 Socks 代理（ssocksd 正向，后两个反向），lcx 模块可负责端口转发（slave 和 listen 成对使用）与端口映射。

## 正向SOCKS v5服务器

目的：就是开一台 SOCKS5 代理服务器，你本地可以用客户端去连。

``` shell
./ew -s ssocksd -l 1080
```

## 反弹SOCKS v5服务器

目标机器：

``` shell
ew -s rssocks -d 2.2.2.2 -e 888
```

其中，2.2.2.2 这个地址是你想要反弹到的服务器 ip。

---

攻击方的机器：

``` shell
ew -s rcsocks -l 1008 -e 888
```

它监听 888 端口的数据包，转发到 1008 端口

这样，你就可以在本地使用 SOCKS 代理客户端去连本地的 1008 端口使用代理了。

## 本地端口映射(转发)

将所有发往主机 1080 端口的请求转发到 10.10.12.1 的 9999 端口

``` shell
./ew -s lcx_tran -l 1080 -f 10.10.12.1 -g 9999
```

## 参考

https://y4er.com/post/port-forwarding/