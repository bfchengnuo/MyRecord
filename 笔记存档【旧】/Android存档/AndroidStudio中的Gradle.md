---
title: AndroidStudio中的Gradle
date: 2017-03-16 09:33:54
tags: [AndroidStudio,Gradle]
categories: Android
---

Gradle是一个基于Apache Ant和Apache Maven概念的项目自动化建构工具。它使用一种基于Groovy的特定领域语言来声明项目设置，而不是传统的XML。<!-- more -->
在Gradle爆红之前，常用的构建工具是ANT，然后又进化到Maven。ANT和Maven这两个工具其实也还算方便，现在还有很多地方在使用。但是二者都有一些缺点，所以让更懒得人觉得不是那么方便。比如，Maven编译规则是用XML来编写的。XML虽然通俗易懂，但是很难在xml中描述`if{某条件成立，编译某文件}/else{编译其他文件}`这样有不同条件的任务。

## Groovy介绍

>   Groovy是一种动态语言。这种语言比较有特点，它和Java一样，也运行于Java虚拟机中。恩？？对头，简单粗暴点儿看，你可以认为Groovy扩展了Java语言。比如，Groovy对自己的定义就是：Groovy是在 java平台上的、 具有像Python， Ruby 和 Smalltalk 语言特性的灵活动态语言， Groovy保证了这些特性像 Java语法一样被 Java开发者使用。

**实际上，由于Groovy Code在真正执行的时候已经变成了Java字节码，所以JVM根本不知道自己运行的是Groovy代码**。
所以说，对于做Java的来说，可以无缝切换

## 回到Gradle

说多了，还是来看看Gradle怎么搞吧，当然这里的也只限于AndroidStudio中的，还是比较简单的
在Android项目中，有两个`build.gradle`文件，一个在最外层，一个在app目录下，这两个文件都非常的重要

### 外层的build.gradle

里面的内容类似：

```json
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.0'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        jcenter()
    }
}
```

上面的都是自动生成的，`buildscript{}`说白了就是设置脚本的运行环境
`repositories{}`的闭包都声明了jcenter，那么jcenter是干啥的呢，其实就是一个仓库，开发者把Android开源项目放到jcenter上，我们就可以在项目上轻松引用了，这也是很强力的地方
`dependencies{}`闭包引用了一个插件，我们知道gradle不止可以构建Java的项目，还支持很多，Google开发的这个插件就是专门用于支持构建Android项目的，后面是版本号

### app目录下的build.gradle

这个就比上面的复杂多了，可配置的东西也更多

```java
apply plugin: 'com.android.application'

android {
    // 项目的编译版本
    compileSdkVersion 24
    // 项目构建工具的版本
    buildToolsVersion "25.0.1"
    defaultConfig {
        applicationId "com.bfchengnuo.mytest"
        // 最低兼容的Android版本
        minSdkVersion 19
        // 已做过充分测试的版本
        targetSdkVersion 24
        // 版本号和版本名
        versionCode 1
        versionName "1.0"
        // 关于自动化测试框架的
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            // 是否对项目进行混淆
            minifyEnabled false
            // 混淆的规则文件
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
// 设置依赖，支持本地依赖、远程依赖、库依赖
dependencies {
    // 本地依赖，libs目录下所有的jar文件
    compile fileTree(dir: 'libs', include: ['*.jar'])
    // 远程依赖
    compile 'com.android.support:appcompat-v7:24.2.1'
    compile 'com.android.support.constraint:constraint-layout:1.0.1'
    compile 'com.android.support:support-v4:24.2.1'
    // 测试用例库
    testCompile 'junit:junit:4.12'
}
```

首先说第一行，一般有两个值：`com.android.application`和`com.android.library`，分别代表这是一个应用程序模块或库模块，应用程序也就是APP了是可以运行的，库模块是依赖于别的应用程序来运行的
接下来是一个大的**Android闭包**，什么作用上面的注释也写了一些了，我要再提一下targetSdkVersion这个，比如你写23+说明你的APP经测试在6.0以上的机子没问题，那么就可以加入新特性/功能，比如运行时权限，如果是22那么认为没在6.0+的机子上测试过，为了保证稳定性，6.0+的特性就不会被加入
**buildTypes**闭包用于指定生成安装文件的配置，通常有两个release和debug(可忽略)，看名字也能猜到是什么作用的了吧，需要注意的是，**通过AndroidStudio直接运行的是测试版的安装文件**
关于远程依赖，AS会先检测本地是否有这个库的缓存，如果没有就会联网下载

## 其他补充

除了上面的关键文件，涉及到gradle的还有一些，比如下面这三个

### settings.gradle

如果打开这个文件，一般里面只有一句代码: `include ':app'`
它用于指示 Gradle 在构建应用时应将哪些模块包括在内。对大多数项目而言，该文件很简单，就上面的一句
不过，多模块项目需要指定应包括在最终构建之中的每个模块。

### gradle.properties

这个文件的内容每个人可能不太一样，比如我的设置过代理后的样子，以及一些补充

```java
systemProp.https.proxyPort=1080
systemProp.http.proxyHost=127.0.0.1
org.gradle.jvmargs=-Xmx1536m
systemProp.https.proxyHost=127.0.0.1
systemProp.http.proxyPort=1080

# 开启JNI编译支持过时API
android.useDeprecatedNdk=true
# 开启并行编译
org.gradle.parallel=true
# 开启守护进程
org.gradle.daemon=true
# 按需编译
org.gradle.configureondemand=true
# 设置编译jvm参数
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
```

### gradle-wrapper.properties

主要是声明指向和版本

```java
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-3.3-all.zip
```

上面我们也看到了下载地址，默认会下载到`/Users/你的用户名/.gradle/wrapper/dists`目录下，让AS去下，速度应该蛮让人着急的，可以收到去下载然后mv到这个目录下就可以了

当我们导入别人的项目的时候，很多时候会构建失败，原因可能是这里的问题，还记得前面我们说过有一个插件负责构建Android工程，它的版本和这里的版本可能会有些问题，比如：

>   Gradle插件版本我设置的是1.2.3，Gradle构建工具的版本是3.3.
>   Android Plugin Version和Gradle version是有对应关系的，Plugin版本太低了，所以gradle 3.3是不支持 1.2.3版本的plugin。

### 解决Gradle版本报错

我们一般有3种方案，推荐是第二种

#### 降低Gradle版本

既然插件版本太低，那就搞个低版本的Gradle构建包，低版本对低版本正好，修改distributionUrl的版本号即可，但是最后是手动下载，AS太慢了，然后去手动指定下目录也行，进设置搜索即可
**选择 File—>invalidate and restart **，然后等一会就好了
这时候可能会弹框提醒你升级 插件版本Android Gradle Plugin，忽略就好了

#### 提高 plugin 版本

就是提高Goggle的插件版本来适应高版本的构建工具
`classpath 'com.android.tools.build:gradle:1.5.0’`，后面的版本号改高一点即可
**这时候不要点击Sync Now！！！**
**选择 File—>invalidate and restart**

#### 灵活配置

主要是使用了AS的设置中，设置**use default gradle wrapper**来管理gradle
其实也是上面两种的结合

## 参考

http://www.infoq.com/cn/articles/android-in-depth-gradle
[官方文档](https://developer.android.com/studio/build/index.html?hl=zh-cn)
[Gradle版本问题报错的处理方案](http://www.jianshu.com/p/c7983274c510)