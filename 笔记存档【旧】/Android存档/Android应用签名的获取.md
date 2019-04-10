---
title: Android应用签名的获取
date: 2016-05-23 09:54:42
tags: Android
categories: Android
---
开发的时候如果需要一些分享、第三方登陆等功能的时候，这就需要使用第三方的SDK工具，在使用这些SDK的时候一般会让我们进行注册申请，提交自己应用的一些信息才会分配给一个AppID，这时候就需要我们获取应用的签名了。
<!-- more -->

## 利用keytool命令获取签名
一般开发中我们自然不太喜欢每次调试运行都给应用进行签名，我们习惯直接使用debug模式进行调试运行，这时候我个人比较倾向于使用debug时候的签名开发，等到完成后再修改成正式的签名。

Android默认是不允许没有签名的APP安装的，那么我们调试的时候也没签名啊，可以直接安装在手机上啊，这是因为在后台会有一个debug的默认签名，签名文件默认位置在：
你的用户目录下 `.Android\debug.keystore`

然后就可以使用命令行来查看签名，CD进签名的目录，执行

> keytool -list -v -keystore debug.keystore

输入密码(默认密码为空或者android)就可以看到：
![](http://o6lgtfj7v.bkt.clouddn.com/Android%E7%AD%BE%E5%90%8D.png)
这里的MD5就是签名了

## 利用第三方的APP查询签名

微信SDK提供了一个根据包名查询签名的APP工具，安装即可使用
![](http://o6lgtfj7v.bkt.clouddn.com/AndroidAPP%E7%AD%BE%E5%90%8D.png)

## 制作自己的签名文件

在AS中，选择`Build > Generate Signed APK…`按照步骤即可
![](http://o6lgtfj7v.bkt.clouddn.com/AndroidAS%E7%AD%BE%E5%90%8D.jpg)

## 补充

还有一种方法可以利用gradle依赖让调试的时候自动根据我们指定的签名文件进行签名
不过这种方式可能会暴露一些签名的敏感信息，需要的话再找谷狗吧。

## 参考

[打包、生成jks密钥、签名Apk、多渠道打包](http://blog.csdn.net/yy1300326388/article/details/48344411)