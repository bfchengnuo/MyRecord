## 问题描述：

使用 Git 客户端向 Github push 代码提示连接被拒绝，443 错误

使用 Vue 的客户端初始化项目（需要从 Github 拉取模板）提示连接拒绝，并伴有 127.0.0.1:443 提示等

使用浏览器 + ss 无压力访问，git、npm 设置代理依然无法访问。

## 解决过程

尝试各种 Google 的办法，大部分是说代理有问题，但是不管是否使用代理都无法连接

## 原因

经过一番研究，大概得出结论：

猜测因为最近“沈阳”事件，Github 被全面封锁，应该是 DNS 污染，范围很大，后来查询中国移动、联通、电信解析 github 都指向了错误的地址。

## 解决

使用远程 DNS 解析，或者配置 Hosts 文件本地进行解析。

使用在线 DNS 查询网站获取 github 的**非中国**地区的DNS解析服务器解析出的最佳 ip 地址，配置到本地 host

```
52.74.223.119 github.com
185.199.108.153 documentcloud.github.com
185.199.108.153 gist.github.com
13.229.189.0 nodeload.github.com
13.250.162.133 raw.github.com
192.30.255.120 training.github.com
13.250.162.133 codeload.github.com
192.30.255.117 api.github.com
```
codeload.github.com 解析正确后 Vue 的初始化功能即可恢复正常！

## 拓展-解决sublime授权被移除问题

无奈，穷人使用的是网上共享的授权，新版的 sublime 当时验证通过，后来会通过授权校验服务器移除相关恶意授权

解决方案就是：屏蔽 Sublime3 认证服务器，采用 Hosts 文件方式

```
127.0.0.1 license.sublimehq.com
127.0.0.1 45.55.255.55
127.0.0.1 45.55.41.223
```

刷新 DNS 缓存即可，win：`ipconfig/dnsflush`
