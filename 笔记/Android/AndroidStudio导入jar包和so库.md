---
title: AndroidStudio导入jar包和so库
date: 2016-06-10 15:42:29
tags: [Android,AndroidStudio]
categories: Android
---
AS采用的是gradle进行编译，目前比较流行进行远程依赖了，一行代码就可以搞定，这比导包要方便了很多，但是目前仍有很多是需要自己导入jar包以及so库，特别是so库，真是不好弄，目前还没完全理解/解决，先挖个坑。
<!-- more -->
## 导入jar包

在Project下面，有一个libs文件夹，将Jar包复制到这里。
然后点击鼠标右键有一个 add 的选项，添加即可，或者直接在build.gradle文件中加入下面的代码，加入了所有的jar包。
``` html
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
}
```
## 导入so库
导入方法网上有很多，不清楚哪一种最有效果，我个人测试时候，有时候某一种方法可以，换一个项目就挂掉了，暂时都整理下吧，，
### 第一种
- 还是把文件复制到工程的libs目录下
- 在app的buid.gradle文件中添加SO库目录配置：
``` xml
 android {
     sourceSets {
         main.jniLibs.srcDirs = ['libs']
    }
 }
```
- 点击Sync，同步配置。

### 第二种
要在工程的src/main下面新建一个jniLibs文件夹，然后将所用到的第三方so库复制进来(有说到此就可以导入成功了，如果不行继续向下看)
然后找到Project下的build.gradle文件，在其中添加以下几行代码：
``` xml
buildTypes {
	release {
	minifyEnabled false
	proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
	}
}

task nativeLibsToJar(type: Zip, description: "create a jar archive of the native libs") {
	destinationDir file("$projectDir/libs")
	baseName "Native_Libs2"
	extension "jar"
	from fileTree(dir: "libs", include: "**/*.so")
	into "lib"
}
```
然后更新下配置就好了。
### 第三种
要在工程的src/main下面新建一个jniLibs文件夹，然后将所用到的第三方so库复制进来。
代码调用：
``` java
String libName = "helloNDK"; // the module name of the library, without .so
System.loadLibrary( libName );
```
### 第四种

感觉和第一种与第三种没什么差别，目前对于so库调用这块还非常不熟悉，不懂原理，先这样贴出来吧

> 比如你的so是**libname.so**,如果是**armeabi**的编译器，那就把so放在工程目录下`libs->armeabi`文件里
> 程序中使用：
> 就直接`System.loadLibrary("name");`//注意这里不能有lib的前缀,但是so文件的名字里要加lib
>
> 如果是mips的编译器，就放在`libs->mipos`文件中，使用也是loadLibrary
>
> 这样安装完apk，so会自动放在手机的`data/data/packagename/libs`目录下

## 远程依赖

直接在APP的build.gradle文件中加入：
``` html
dependencies {
    compile '依赖包'
}
```
更新下配置即可。
## 参考
[System.load 和 System.loadLibrary详解](http://blog.csdn.net/ring0hx/article/details/3242245)

[http://dwtedx.com/blog_448.html](http://dwtedx.com/blog_448.html)
