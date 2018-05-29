java读取文件的两种方法：java.io 和 java.lang.ClassLoader （我就知道这两种.....）

``` java
// java.io:  
File  file  =  new  File("...");  
FileInputStream  fis  =  new  FileInputStream("...");  
FileReader  fr  =  new  FileReader("...");  

//ClassLoader:  
ClassLoader  loader  =  XXXClass.class.getClassLoader();   
ClassLoader  loader2  =  Thread.currentThread().getContextClassLoader();  

URL  url  =  loader.getResource("...");  
File  file  =  new  File(url.getFile());  
InputStream  input  =  loader.getResourceAsStream("...");  
```

上面这些就是最简单直接的两种用法了。

## 使用 IO

java.io 包中的类总是根据**当前用户目录**来分析相对路径名，也就是说相对路径是否好使，取决于 user.dir 的值。系统属性  user.dir 是 JVM 启动的时候设置的，**通常是 Java 虚拟机的调用目录，即执行 java 命令所在的目录**。

对于 tomcat/jboss 容器，user.dir 是 `%home/bin%/` 目录，因为这个目录就是我们启动 web 容器的地方

> 在 eclipse 中运行程序的时候，eclipse 会将 user.dir 的值设置为工程的根目录

用户目录可以使用 `System.getProperty("user.dir")` 来查看

**所以说，使用 java.io 读取文件，无论是相对路径，还是绝对路径都不是好的做法，能不使用就不要使用（在 JavaEE 中）。**

## 使用ClassLoader

Class.getResource() 有 2 种方式，绝对路径和相对路径。绝对路径以 `/` 开头，从 classpath 或 jar 包根目录下开始搜索；

相对路径是相对当前 class 所在的目录，允许使用 `..` 或 `.` 来定位文件。

---

ClassLoader.getResource() **只能使用绝对路径，而且不用以 `/` 开头**。

这两种方式读取资源文件，不会依赖于 user.dir，也不会依赖于具体部署的环境，是推荐的做法（JavaEE）

使用`this.getClass().getResource()`获得的是代码所在类编译成class文件之后输出文件所在目录位置，而`this.getClass().getClassLoader().getResource()`获得的是class loader所在路径(`.../classes/`)

## 如何选取

- java.io：

  相对于当前用户目录的相对路径读取；注重**与磁盘文件打交道**或者纯 java project 中使用。 

  虽然 ClassLoader 方式更通用，但是如果不是 javaEE 环境，要定位到 classpath 路径下去读文件是不合理的。 

- java.lang.ClassLoader：

  相对于 classpath 的相对路径读取；建议在 javaEE 环境中都使用这种方式。 

通常，ClassLoader  不能读取太大的文件，它适合读取 web 项目的那些配置文件，如果需要读取大文件，还是要用 IO 包下的，可以先通过 ClassLoader 获取到文件的绝对路径，然后传给 File 或者其他对象，用 io 包里的对象去读取会更好些

## 关于文件夹与classpath

这个话题就牵扯到了 package、folder 和 source folder 的区别了。

**folder：**就是普通文件夹，IDE 不会对其进行任何的检查。

**package**：就是 Java 开发过程中的包，其路径就是每一个类的包路径，**其必须存放在一个 source folder 下。**

**source folder** ：是用来存放 Java 源代码的，其下的所有 Java 源文件都会被时时编译成 class 文件。

以 Eclipse 来说，对于 JavaSE 的项目会被编译到项目目录下的 bin 目录下，对于 JavaEE 项目会被编译到相应的 `/WEB-INF/classes` 文件夹中，无论是哪种项目 bin 文件和 classes 文件夹都是不会再 IDE 中显示的，并且上面说到的都是默认的编译路径。

利用这个特性可以建一个 config 的 source folder 文件夹来存放配置文件，使用 classpath 的方式来读取。

---

再说下 Classpath，简单说就是 CLASSPATH 环境变量的作用是指定 Java 类所在的目录。

最典型的，当尝试使用 Java 命令在 cmd 里运行一个 class 文件时，很有可能会提示：`错误: 找不到或无法加载主类 HelloWorld` 这就是 classpath 配置不正确带来的问题。

如果刚装完 JDK，没有配置环境变量，那么缺省的 `%CLASSPATH%` 环境变量的值是`.`，也就是当前目录。

和 path 类似，当配置多个时是从左向右进行搜索，所以一般都把 `.` 配置在最左边。

Java 中通常将环境变量 CLASSPATH 配置为`.;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\dt.jar`。

> dt.jar：
>
> 运行环境类库，主要是 Swing 包( GUI 相关)，这一点通过用压缩软件打开 dt.jar 也可以看到。如果在开发时候没有用到 Swing 包，那么可以不用将 dt.jar 添加到 CLASSPATH 变量中。
>
> tools.jar：
>
> 工具类库，它跟我们程序中用到的基础类库没有关系。我们注意到在 Path 中变量值 bin 目录下的各个 exe 工具的大小都很小，一般都在 27 KB左右，这是因为它们实际上仅仅相当于是一层代码的包装，这些工具的实现所要用到的类库都在 tools.jar 中，用压缩软件打开 tools.jar ，你会发现有很多文件是和 bin 目录下的 exe 工具相对的。

## 关于路径

看到一个问题：

那为什么 Java （IO）中写正斜杠“/”、“//”、“///”，甚至再多都不会有问题呢？

那是因为 Java 中处理流，都会使用到 File 这个类，在 Windows 环境中，File 会使用 WinNTFileSystem 这个工具类处理那些问题，再 WinNTFileSystem 类中，会把所有的正斜杠“/”都处理成反斜杠，再把多余的反斜杠“\”给去掉，最终会表示成转义后的一个反斜杠。

## JavaEE

在 JavaEE 中获取路径还有一直方式，那就是 ServletContext 的 getRealPath 方法，它可以获得物理路径。

参数中 `'/'` 就表示当前的工程所在的目录，可以理解为 WebRoot 所在的那个目录。

对于 Web 上运行这些的结果：

``` java
this.getClass().getClassLoader().getResource("/").getPath();
this.getClass().getClassLoader().getResource("").getPath(); 
```

得到的是 ClassPath的绝对URI路径。 如：`/D:/.../WEB-INF/classes/ `

``` java
this.getClass().getResource("/").getPath();
this.getClass().getResource("").getPath();
```

得到的是当前类文件的 URI 目录。不包括自己！ 如：`/D:/.../WEB-INF/classes/com/jebel/helper/ `

``` java
Thread.currentThread().getContextClassLoader().getResource("/").getPath()
Thread.currentThread().getContextClassLoader().getResource("").getPath()
```

得到的是 ClassPath 的绝对URI路径。
如：`/D:/.../WEB-INF/classes/`

使用 class 的 getResource 的时候可以使用字符串分割来获得路径

```java
//得到d:/tomcat/webapps/工程名WEB-INF/classes/路径 
String path=this.getClass().getResource("/").getPath();
//从路径字符串中取出工程路径
path=path.substring(1, path.indexOf("WEB-INF/classes"));
```

## 附：Spring中ClassPathResource实现

Spring 可以说是 JavaWeb 开发不可或缺的框架，它有提供 ClassPathResource 来读取文件：

``` java
/** 
 * This implementation opens an InputStream for the given class path resource. 
 * @see java.lang.ClassLoader#getResourceAsStream(String) 
 * @see java.lang.Class#getResourceAsStream(String) 
 */  
public InputStream getInputStream() throws IOException {  
  InputStream is;  
  if (this.clazz != null) {  
    is = this.clazz.getResourceAsStream(this.path);  
  }  
  else {  
    is = this.classLoader.getResourceAsStream(this.path);  
  }  
  if (is == null) {  
    throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");  
  }  
  return is;  
}  
```

可以看出 spring 提供的 ClassPathResource，底层使用的就是 Class.getResource 或 ClassLoader.getResource()

## 附2：对于properties的读取

读取 properties 有很多种方法，下面补充个我不太熟悉的：

读取 properties 文件可以尝试 **ResourceBundle**，支持国际化。

路径方面没问题：

在某个包下：`ResourceBundle.getBundle("com/mmq/test");`

在 src 下：`ResourceBundle.getBundle("test");`

// TODO 待补全

## 参考

http://blog.csdn.net/aitangyong/article/details/36471881