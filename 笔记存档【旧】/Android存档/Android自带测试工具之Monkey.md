---
title: Android自带测试工具之Monkey
date: 2017-04-18 21:03:06
tags: [Android,测试]
categories: Android
---

## 为什么要进行压力测试

其实说到底就是为了留住用户，如果 APP 在使用期间出现一些问题，比如加载缓慢、崩掉等部分用户就会卸载，放弃使用，所以保证 APP 的稳定性是非常重要的<!-- more -->

进行压力测试要在保证首轮功能测试通过后进行，因为首先要保证程序不会出现 bug
进行时间放在晚上比较好

## Monkey介绍

>   Monkey 是 Android 中的一个命令行工具，可以运行在模拟器里或实际设备中。可以说是 Android SDK 为我们提供的一个工具
>   它向系统发送伪随机的用户事件流(如按键输入、触摸屏输入、手势输入等)，实现对正在开发的应用程序进行压力测试。
>   Monkey 测试是一种为了测试软件的稳定性、健壮性的快速有效的方法。

简单说，Monkey 就是存在 Android 系统中，能够代替我们模拟人为的一些操作
给 Monkey 发送指令需要的工具是 ADB ，这个我们应该都很熟了，在命令行操作即可

如果用命令行一条一条执行，效率比较低，我们可以写一个脚本 MonkeyScript 

### MonkeyRunner

>   monkeyrunner 工具提供了一个 API，**使用此 API 写出的程序可以在 Android 代码之外控制 Android 设备和模拟器。**
>   通过 monkeyrunner，您可以写出一个 Python 程序去安装一个 Android 应用程序或测试包，运行它，向它发送模拟击键，截取它的用户界面图片，并将截图存储于工作站上。
>   monkeyrunner 工具的主要设计目的是用于测试功能/框架水平上的应用程序和设备，或用于运行单元测试套件，但您当然也可以将其用于其它目的。
>
>   monkeyrunner 将 monkey 内一些命令进行封装，实现了一些 API 函数

-   Monkey：
    Monkey 工具直接运行在设备或模拟器的 adb  shell 中，生成用户或系统的伪随机事件流。
-   monkeyrunner：
    monkeyrunner 工具则是在工作站上通过 API 定义的特定命令和事件控制设备或模拟器。

**测试类型：**

1.  多设备控制：
    monkeyrunner API 可以跨多个设备或模拟器实施测试套件。
    您可以在同一时间接上所有的设备或一次启动全部模拟器（或统统一起），依据程序依次连接到每一个，然后运行一个或多个测试。您也可以用程序启动一个配置好的模拟器，运行一个或多个测试，然后关闭模拟器。
2.  功能测试：
     monkeyrunner 可以为一个应用自动贯彻一次功能测试。您提供按键或触摸事件的输入数值，然后观察输出结果的截屏。
3.  回归测试：
    monkeyrunner 可以运行某个应用，并将其结果截屏与既定已知正确的结果截屏相比较，以此测试应用的稳定性。
4.  可扩展的自动化：
    由于 monkeyrunner 是一个 API 工具包，您可以基于 Python 模块和程序开发一整套系统，以此来控制 Android 设备。
    除了使用 monkeyrunner API 之外，您还可以使用标准的 Python os 和 subprocess 模块来调用 Android Debug Bridge 这样的 Android 工具。

**MonkeyRunner APIS:**

-   Monkey runner:
    用来连接设备或者模拟器；
-   Monkey Device:
    提供安装，卸载应用，发送模拟事件；
-   Monkey Image：
    完成图像保存，及对比的操作。

## 结果分析

我们主要分析异常测试结果，一般异常也就分为两类

-   CRASH
    崩溃，应用程序使用过程中，非正常的退出
-   ANR (Application Not Responding)
    就是未响应，一般也是有大问题的，也会对正常的使用产生干扰

## 基本的压力测试

首先，我们可以通过命令测试是否链接成功 `adb devices`
最简单的，我模拟可以通过下面的命令让其随机触发 100 次操作
`adb shell monkey 100`

下面我们就来测试下系统自带的计算器吧，这样的话我们要知道它的包名才行，可以通过 logcat 找到

```shell
# 可以安装一个应用
$ adb install package.apk
# 监控 log 中包含 START 的内容，打开某个应用会有相应的输出
$ adb logcat | grep START
```

执行后，打开计算器就可以获取到包名了，甚至还有 Activity 的名字：
`cmp=com.android.calculator2/.Calculator`
然后就是给指定的包进行压力测试

```shell
$ adb shell monkey -p com.android.calculator2 1000
```

1000 次其实很快就完了，速度还是很快的，也可以指定每次操作间隔的时间

```shell
# 每隔一秒执行一个，一共十个
$ adb shell monkey -p com.android.calculator2 --throttle 1000 10
```

## 进阶使用

### 使用 seed

上面说过，Monkey 执行的是随机操作，当出现问题时是没法进行复现的
其实它也不是真随机，是个伪随机，只要指定的 seed 参数是相同的，执行的测试也是相同的

```shell
$ adb shell monkey -p com.android.calculator2 -s 10 1000
```

这样的话，两次执行显示的结果应该是一致的，如果使用的是模拟器，可能由于延迟的原因有些差别

### 指定操作事件

我们可以让它只进行点击测试并不进行滑动啊等的，这个就可以设置触摸事件的百分比
只做点击事件：

```shell
$ adb shell monkey -p com.android.calculator2 --pct-touch 100 1000
```

查看详情可以加个 `-v` 参数，能清楚的指定它做了什么

其他的也都类似，常用的就这几个轨迹球事件：

-   动作事件：由屏幕上某处的一个 down 事件、一系列的伪随机事件和一个 up 事件组成
    命令：`adb shell monkey --pct-motion<percent>`
-   设定轨迹球事件百分比
    命令：`adb shell monkey --pct-trackball<percent>`
-   基本导航事件：输入设备的上下左右方向键的导航
    命令：`adb shell monkey --pct-nav<percent>`
-   主要导航事件：中间键、返回键、菜单按钮键
    命令：`adb shell monkey --pct-majornav<percent>`
-   系统导航事件：HOME 键、BACK 键、拨号、音量键
    命令：`adb shell monkey --pct-syskeys<percent>`
-   启动 Activity 事件：在已有的Activity之间进行切换
    命令：`adb shell monkey --pct-appswitch<percent>`
-   不常用事件：
    命令：`adb shell monkey --pct-anyevent<percent>`

### 异常

当 monkey  遇到崩溃或者 ANR 的时候就会停下，如果是无人值守的情况下这样就不好玩了，所以一般也会设置个参数把这些异常忽略
`--ignore-crashes`
`--ignore-timeouts`
这里只是说了两种最常见的异常，还有其他很多异常，当然也可以进行忽略，更详细的可以去看：
http://blog.csdn.net/marshalchen/article/details/9119979

ANR 的日志文件一般保存在：` /data/anr/`

### 日志保存

-   保存在 PC 中：

    ```shell
    $ adb shell monkey [options] <event-count> > d:\monkeylog.txt
    ```

-   保存在手机中：

    ```shell
    $ adb shell
    # monkey [options] <event-count> /mnt/sdcard/monkeylog.txt
    ```

-   标准流与错误流分开保存：

    ```shell
    # monkey [options] <event-count> 1>/mnt/sdcard/monkeylog.txt 2>/mnt/sdcard/monkeyErrorlog.txt
    ```

## 使用MonkeyScript 

上面所说的测试基本都是随机的，当然可以编写 MonkeyScript  来指定测试内容，下面是个例子

```
# Start of Script
type= user
count= 49 
speed= 1.0
start data >>

# 开始编写，1. 启动 APP，就是打开浏览器
LaunchActivity(com.android.browser,com.android.browser.BrowserActivity)
# 等待 2s
UserWait(2000)
#2.清空网址
Tap(223,146)
ProfileWait()
DispatchPress(112)
ProfileWait()
#3.输入网址
DispatchString(www.baidu.com)
ProfileWait()
#4.确认，载入网址
DispatchPress(KEYCODE_ENTER)
ProfileWait()
#5.完成退出浏览器
DispatchPress(KEYCODE_HOME)
ProfileWait()
```

如果需要点击操作，可以通过 SDK 中的 uiautomatorviewer 工具来获取坐标之类的信息，然后使用 captureDispatchPointer 进行操作

下面就是执行了，首先，Monkey 是手机里的，脚本是电脑里的，只有复制到手机里才能运行，可以使用下面的命令进行传输

```shell
$ adb push data.script /data/local/tmp/
$ adb shell
$# cd /data/local/tmp/
$# monkey -f data.script 2
```

如果要想从命令行启动 APP 记得把对应的 Activity 在清单文件中加入 `android:exported="true"`

## 使用MonkeyRunner

这个也是 Android 自带的一个工具，通常我们使用 Python 来进行编写

AndroidStudio 中已经集成，貌似可以在 AS 中写，具体可以参考：
http://blog.csdn.net/niubitianping/article/details/52781266

我测试的时候 MonkeyRunner 没有运行成功，老是提示各种错误

-   我测试的时候，这个工具是存在于 `AndroidSDK\tools\bin`
-   遇到 `Please set ANDROID_SWT to point to...` 错误
    参考 Google 论坛的解决方案，添加 ANDROID_SWT 环境变量
    设置：`AndroidSDK\tools\lib\x86_64\swt.jar` ，于是就有了下面的错误...
-   遇到 `Error: Unable to access jarfile...` 错误
    得知 monkeyrunner-25.3.1.jar 文件位于 `AndroidSDK\tools\lib`
    经过一番折腾，未果...
    终极办法...在 bin 的上层目录新建 framework 文件夹，然后把这俩文件拷过来，然后下面还有错
-   遇到 `Error: A JNI error has occurred, please check your ...`  错误
    待解决...我实在是不想弄了

看过一眼 MonkeyRunner.bat 无奈，看不懂，无法修改

最后附上官方文档：
https://developer.android.com/studio/test/monkeyrunner/index.html

应该还有其他工具吧....