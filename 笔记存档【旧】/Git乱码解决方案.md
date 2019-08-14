---
title: git for windows 乱码问题
date: 2016-3-22
tags: [Git]
categories: 我是修电脑的
---

**假设git安装在`C:\git`**


## 1. 使用 git add 命令添加文件名含中文字符的文件时


乱码类似：

	\316\304\261\276\316\304\265\265.txt

解决方案：
<!-- more -->
~~**1.编辑 C:\Git\etc\inputrc 文件中对应的行，查找以下2行，并修改其值**~~

原先：

    set output-meta off
    set convert-meta on
改为：


    set output-meta on
    set convert-meta off

**2.在bash提示符下输入：**

`git config --global core.quotepath false`

core.quotepath设为false的话，就不会对0x80以上的字符进行quote。中文显示正常。

## 2. 使用git log查看含有中文的log信息时

乱码类似：

    <E4><BF><AE><E6><94><B9><E6><96><87><E6><9C><AC><E6><96><87><E6><A1><A3>

**解决方案：**

在Bash提示符下输入：


    git config --global i18n.commitencoding utf-8

    git config --global i18n.logoutputencoding gbk

注：设置 commit 提交时使用 utf-8 编码，可避免 Linux 服务器上乱码；同时设置在执行 git log 时将 utf-8 编码转换成 gbk 编码，以解决乱码问题。

编辑 `C:\Git\etc\profile` 文件，添加如下一行：

    export LESSCHARSET=utf-8

注：以使git log可以正常显示中文（需要配合：i18n.logoutputencoding gbk）

## 3.使用ls命令查看含有中文的文件名乱码时
乱码类似：

    ????.txt
    ???????.md

**解决方案：**

使用 ls --show-control-chars 命令来强制使用控制台字符编码显示文件名，即可查看中文文件名。

为了方便使用，可以编辑 `C:\Git\etc\git-completion.bash` 文件，添加如下一行：

    alias ls="ls --show-control-chars"
## 4.在Git Gui中查看UTF-8编码的文本文件时
乱码类似：

    锘夸腑鏂囨枃妗￡
**解决方案：**

在Bash提示符下输入：

    git config --global gui.encoding utf-8
注：通过上述设置，UTF-8 编码的文本文件可以正常查看，但是 GBK 编码的文件将会乱码，所以还是没有从根本上解决问题。

可行的方法之一为：将所有文本文件的编码统一为 UTF-8 或 GBK，然后设置相应的gui.encoding 参数为 utf-8 或 gbk。


**原文/参考资料：**[https://segmentfault.com/a/1190000000578037](https://segmentfault.com/a/1190000000578037)