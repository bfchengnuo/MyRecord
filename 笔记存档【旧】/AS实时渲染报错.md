# AS实时渲染报错

<br >
<br >
![](https://github.com/bfchengnuo/MyRecord/blob/master/笔记/img/AS渲染错误.png)
<br >
<br >

应该是更新完SDK后出现的问题，原因至今不明

AS版本1.5
<br >

**解决：**

修改Preview (API?)的版本23为其他版本，修改为21后渲染正常


<br >

临时解决方案，完美方法待发现...



## CSDN网友解答


目测原因可能有两个：

1. Android Studio 更新SDK后由于被墙的关系可能有些兼容包没有下载完全 

2. 你引用的图片格式为*.jpg或者资源的名字有非法字符，比如有大写字符，特俗符号等。

3. 引用资源Id错误,请检查下当前所在布局中是否有这个资源Id.


解决方法：

1. 打开你的SDK 进行再次更新，更新完全后再Clean 下工程， Rebuild 你的Android 项目就好了。

2. 根据错误提示，检查你所引用的资源文件名是否引用了非法字符，比如大写，比如引用了android studio 不支持的jpg文件图片格式，比如文件名中有“—”等非法字符。


3. 英文错误提示中有提到，说引用资源Id找不到异常，所以请检查下你的图片等其他资源和引用路径是否合法。

多插一句：

如果还不行，请修改Preview 的版本23为其他版本，比如21 等，前提是这个预览版本你的SDK platform 中要有这个版本。不然肯定报错

原文：[http://bbs.csdn.net/topics/391910932](http://bbs.csdn.net/topics/391910932)