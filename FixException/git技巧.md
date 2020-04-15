---
title: Git技巧
date: 2016-3-22
tags: [Git]
categories: 技能Get
---

## **查看历史提交日志**

默认的日志格式真是无法入目啊，如何才能让其显示出好看的提交日志呢，有大神已经写好了，就是下面的命令：

`git log --graph --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit --`
<!-- more -->
这样是可以查看毕竟漂亮的日志，但是太长了。。我们可以写进全局配置里去：

`git config --global alias.lg "log --color --graph --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit --"`

然后，我们就可以使用这样的短命令了：`git lg`

## Git Push 避免用户名和密码方法

添加 Git Config 内容

进入 git bash 终端， 输入如下命令：`git config --global credential.helper store`

执行完后查看 %HOME% 目录下的 `.gitconfig` 文件，会多了一项：
``` 
[credential]
    helper = store
```
重新开启` git bash`会发现 `git push` 时不用再输入用户名和密码

## 解决换行符问题

因为不同系统之间的换行符的差别，造成的不必要的差异是不能忍受的，在 Git 官方也有相关的描述：

> 假如你正在 Windows 上写程序，又或者你正在和其他人合作，他们在 Windows 上编程，而你却在其他系统上，在这些情况下，你可能会遇到行尾结束符问题。这是因为 Windows 使用回车和换行两个字符来结束一行，而 Mac 和 Linux 只使用换行一个字符。虽然这是小问题，但它会极大地扰乱跨平台协作。

然后他们也给出了解决方案，我选用的就说第一种：

~~Git 可以在你提交时自动地把行结束符 CRLF 转换成 LF，而在签出代码时把 LF 转换成 CRLF。~~
~~用 `core.autocrlf` 来打开此项功能，如果是在 Windows 系统上，把它设置成 `true` ，这样当签出代码时，LF 会被转换成 CRLF：`git config --global core.autocrlf true`~~

引用自：https://git-scm.com/book/zh/v1/%E8%87%AA%E5%AE%9A%E4%B9%89-Git-%E9%85%8D%E7%BD%AE-Git#%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%B8%8E%E7%A9%BA%E7%99%BD

请参考：https://github.com/bfchengnuo/MyRecord/issues/58

当然，我顺便会再配置点其他的东西，配置文件：

``` 
[core]
	autocrlf = true
	# 避免中文乱码，另一篇中有介绍
	quotepath = false
	# 开启大小写敏感
	ignorecase = false
```

注意 ignorecase 设置为 false 是开启大小写敏感，默认 Git 对大小写不敏感，这就导致一个文件你改了名字，当然只是大小写的改变，Git 并不会感知到，这就很蛋疼了，所以还是开了。

## 解决乱码问题

### 使用 add 命令添加中文文件名时

乱码类似：

```
\316\304\261\276\316\304\265\265.txt
```

解决方案有两种

#### 修改inputrc文件(不推荐)

**编辑 `C:\Git\etc\inputrc` 文件中对应的行，查找以下 2 行，并修改其值**

原先：

```
set output-meta off
set convert-meta on
```

改为：

```
set output-meta on
set convert-meta off
```

#### 配置quotepath属性

**在bash提示符下输入：**

`git config --global core.quotepath false`

core.quotepath 设为 false 的话，就不会对 0x80 以上的字符进行 quote。中文显示正常。

## 使用log查看含有中文的log信息时

乱码类似：

```
<E4><BF><AE><E6><94><B9><E6><96><87><E6><9C><AC><E6><96><87><E6><A1><A3>
```

**解决方案：**

在 Bash 提示符下输入：

```
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding gbk

# 顺便把 GUI 的也配了
git config --global gui.encoding utf-8
```

注：设置 commit 提交时使用 utf-8 编码，可避免 Linux 服务器上乱码；同时设置在执行 `git log` 时将 utf-8 编码转换成 gbk 编码，以解决乱码问题。

编辑 `C:\Git\etc\profile` 文件，添加如下一行：

```
export LESSCHARSET=utf-8
```

注：以使 `git log` 可以正常显示中文（需要配合：i18n.logoutputencoding gbk）

参考：https://segmentfault.com/a/1190000000578037

## 设置代理服务器

配置文件中加入
```
[https]
	postBuffer = 524288000
	proxy = socks5://127.0.0.1:1080
[http]
	postBuffer = 524288000
	proxy = socks5://127.0.0.1:1080
	sslVerify = false
```

[飞机](http://blog.useasp.net/archive/2015/08/26/config-git-proxy-settings-on-windows.aspx)