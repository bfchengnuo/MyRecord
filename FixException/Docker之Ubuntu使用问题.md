记录在使用 Ubuntu 的基础 Docker 镜像遇到的问题。

首先 apt 不能用的话就先 update 一下。

## 中文乱码问题
我是搜到了一些方案，但是很多并没有什么卵用。

下面是一个对我来说有效的方案：

- 安装语言支持：`apt install  language-pack-zh-han*`
- 添加中文支持：`locale-gen zh_CN.UTF-8`
- 在 `/etc/bash.bashrc` 里面末尾写入 `export LC_ALL="C.UTF-8"`，然后使用 source 让其生效。

相关命令：
- locale
- locale -a

来源：https://www.jianshu.com/p/43a3468362aa

## zip解压乱码问题
主要是由于 GBK 带来的乱码问题，使用 `unzip -O cp936 xxxx.zip -d ./xx` 解决。

使用 -d 指定解压目录。

## vim打开文件乱码
编辑 `~/.vimrc` 文件（没有则手动创建） 添加如下几行

```
set fileencodings=utf-8,ucs-bom,gb18030,gbk,gb2312,cp936
set termencoding=utf-8
set encoding=utf-8
```

## Ubuntu支持GBK
未验证

参考：https://www.jianshu.com/p/22a5529eb7c8

---

编辑 `/var/lib/locales/supported.d/local` 文件，添加字符集，如下：

``` 
en_US.UTF-8 UTF-8
zh_CN.UTF-8 UTF-8
zh_CN.GBK GBK
zh_CN GB2312
```

保存后，执行命令：`sudo locale-gen`

