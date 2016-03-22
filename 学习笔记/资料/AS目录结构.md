# Android Studio目录结构 #

![](https://github.com/bfchengnuo/MyRecord/blob/master/学习笔记/资料/img/01.png)

【= = 好复杂的样子】

OK，我们这么看，第一，把这么多文件先分成这么三块


1. 编译系统（Gradle）
2. 配置文件
3. 应用模块

Gradle是Google推荐使用的一套基于Groovy的编译系统脚本（当然，你也可以使用ant），具体的介绍和文档可以参考这个传送门：[https://developer.android.com/tools/building/plugin-for-gradle.html](https://developer.android.com/tools/building/plugin-for-gradle.html "传送")

如果你学会之后，会对Android项目的编译了如指掌（总之非常爽~），它的缺点目前是效率不高，然后因为有功夫网的存在，所以在bintray上下载依赖会比较慢。

上面那个图中出现gradle字眼的就是gradle相关的一些文件。
Android中使用Gradle Wrapper对Gradle进行了一层包装，我猜测这么做的原因是因为gradle更新速度实在太快，为了兼容性着想，才出了这么一套方案。（如果觉得这个猜想有问题请指正）
gradlew相关的文件就是和Gradle Wrapper有关。我们对除了app文件夹以外的文件列一下。

<table>
<thead><tr>
<th>文件（夹）名</th>
  <th>用途</th>
</tr></thead>
<tbody>
<tr>
<td>.gradle</td>
  <td>Gradle编译系统，版本由wrapper指定</td>
</tr>
<tr>
<td>.idea</td>
  <td>Android Studio IDE所需要的文件</td>
</tr>
<tr>
<td>build</td>
  <td>代码编译后生成的文件存放的位置</td>
</tr>
<tr>
<td>gradle</td>
  <td>wrapper的jar和配置文件所在的位置</td>
</tr>
<tr>
<td>.gitignore</td>
  <td>git使用的ignore文件</td>
</tr>
<tr>
<td>build.gradle</td>
  <td>gradle编译的相关配置文件（相当于Makefile）</td>
</tr>
<tr>
<td>gradle.properties</td>
  <td>gradle相关的全局属性设置</td>
</tr>
<tr>
<td>gradlew</td>
  <td>*nix下的<code>gradle wrapper</code>可执行文件</td>
</tr>
<tr>
<td>graldew.bat</td>
  <td>windows下的<code>gradle wrapper</code>可执行文件</td>
</tr>
<tr>
<td>local.properties</td>
  <td>本地属性设置（key设置，android sdk位置等属性），这个文件是不推荐上传到VCS中去的</td>
</tr>
<tr>
<td>settings.gradle</td>
  <td>和设置相关的gradle脚本</td>
</tr>
</tbody>
</table>

这些就是外部文件相关的一些文件的介绍。我们来看下更重要的app模块里的文件

![](https://github.com/bfchengnuo/MyRecord/blob/master/学习笔记/资料/img/02.png)

这是app模块下的文件目录结构，介绍下他们的用途

<table>
<thead><tr>
<th>文件（夹）名</th>
  <th>用途</th>
</tr></thead>
<tbody>
<tr>
<td>build</td>
  <td>编译后的文件存在的位置（包括最终生成的apk也在这里面）</td>
</tr>
<tr>
<td>libs</td>
  <td>依赖的库所在的位置（<code>jar</code>和<code>aar</code>)</td>
</tr>
<tr>
<td>src</td>
  <td>源代码所在的目录</td>
</tr>
<tr>
<td>src/main</td>
  <td>主要代码所在位置（src/androidTest)就是测试代码所在位置了</td>
</tr>
<tr>
<td>src/main/assets</td>
  <td>android中附带的一些文件</td>
</tr>
<tr>
<td>src/main/java</td>
  <td>最最重要的，我们的java代码所在的位置</td>
</tr>
<tr>
<td>src/main/jniLibs</td>
  <td>jni的一些动态库所在的默认位置(.so文件)</td>
</tr>
<tr>
<td>src/main/res</td>
  <td>android资源文件所在位置</td>
</tr>
<tr>
<td>src/main/AndroidManifest.xml</td>
  <td>AndroidManifest不用介绍了吧~</td>
</tr>
<tr>
<td>build.gradle</td>
  <td>和这个项目有关的gradle配置，相当于这个项目的Makefile，一些项目的依赖就写在这里面</td>
</tr>
<tr>
<td>proguard.pro</td>
  <td>代码混淆配置文件</td>
</tr>
</tbody>
</table>

作者：Gemini

原文地址：[https://segmentfault.com/a/1190000002963895](https://segmentfault.com/a/1190000002963895)

## 其他补充 ##
### Project 结构类型 ###
- app/build/ app模块build编译输出的目录
- app/build.gradle app模块的gradle编译文件
- app/app.iml app模块的配置文件
- app/proguard-rules.pro app模块proguard文件
- build.gradle 项目的gradle编译文件
- settings.gradle 定义项目包含哪些模块
- gradlew 编译脚本，可以在命令行执行打包
- local.properties 配置SDK/NDK
- MyApplication.iml 项目的配置文件
- External Libraries 项目依赖的Lib, 编译时自动下载的

### Android结构类型 ###

- app/manifests AndroidManifest.xml配置文件目录
- app/java 源码目录
- app/res 资源文件目录
- Gradle Scripts gradle编译相关的脚本

原文地址：[https://www.aswifter.com/2015/07/07/android-studio-project-struct/](https://www.aswifter.com/2015/07/07/android-studio-project-struct/)

### 主要与Eclipse的区别 ###
1. Android Studio的目录结构本来就代表一个workspace，一个workspace里面可以有Module，可以将一个Module理解成Eclipse中的一个Project；
2. 目录中将java代码和资源文件（图片、布局文件等）全部归结为src，在src目录下有一个main的分组，同时划分出java和res两个Group，res和Eclipse下的结构一样，java下就是源码的包和类文件；
3. 新建文件方式的不同，右键选择New或Command+N后出现新建菜单，类型有Java Class，也就是一般的java类，File就是普通文件，Package就是建包，值得注意的就是Android Component，它直接提供了Android中基本组件的创建。

*仅作为资料查阅，备份，待补充