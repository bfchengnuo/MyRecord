问题 RT：

Windows不会将命令行参数传递给从shell执行的Python程序

## 问题原因：

大概是因为执行的时候会调用cmd来执行设置的默认打开程序，把参数传递到了cmd，但是没能把参数继续往下传给 py

## 解决方案：

首先检查默认打开程序是不是设置对了

然后检查注册表是否配置正确，注册表定位：`计算机\HKEY_CLASSES_ROOT\py_auto_file\shell\open\command`

查看值是不是有 `%*` 正确的值应该是：

`"D:\Development\Python\python.exe" "%1" %*`

## 其他曲线救国方案：

使用 `python xxx.py  para` 的形式运行（不推荐）

使用 bat 命令做中转（不推荐）

``` bat
@echo off
E:
cd E:\Python\qiniu
call python Uploader.py %*
```
