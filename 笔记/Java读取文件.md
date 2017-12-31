java读取文件的两种方法：java.io和java.lang.ClassLoader （我就知道这两种.....）

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

java.io 包中的类总是根据**当前用户目录**来分析相对路径名，也就是说相对路径是否好使，取决于 user.dir 的值。系统属性  user.dir 是 JVM 启动的时候设置的，**通常是 Java 虚拟机的调用目录，即执行 java 命令所在的目录**。

对于 tomcat/jboss 容器，user.dir 是 `%home/bin%/` 目录，因为这个目录就是我们启动 web 容器的地方

> 在 eclipse 中运行程序的时候，eclipse 会将 user.dir 的值设置为工程的根目录

用户目录可以使用 `System.getProperty("user.dir")` 来查看

**所以说，使用 java.io 读取文件，无论是相对路径，还是绝对路径都不是好的做法，能不使用就不要使用（在 JavaEE 中）。**

## 使用ClassLoader

Class.getResource() 有 2 种方式，绝对路径和相对路径。绝对路径以 `/` 开头，从 classpath 或 jar 包根目录下开始搜索；

相对路径是相对当前 class 所在的目录，允许使用 `..` 或 `.` 来定位文件。

ClassLoader.getResource() **只能使用绝对路径，而且不用以 `/` 开头**。

这两种方式读取资源文件，不会依赖于 user.dir，也不会依赖于具体部署的环境，是推荐的做法（JavaEE）

## 如何选取

- java.io：

  相对于当前用户目录的相对路径读取；注重**与磁盘文件打交道**或者纯 java project 中使用。 

  虽然 ClassLoader 方式更通用，但是如果不是 javaEE 环境，要定位到 classpath 路径下去读文件是不合理的。 

- java.lang.ClassLoader：

  相对于 classpath 的相对路径读取；建议在 javaEE 环境中都使用这种方式。 

通常，ClassLoader  不能读取太大的文件，它适合读取 web 项目的那些配置文件，如果需要读取大文件，还是要用 IO 包下的，可以先通过 ClassLoader 获取到文件的绝对路径，然后传给 File 或者其他对象，用 io 包里的对象去读取会更好些

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

## 参考

http://blog.csdn.net/aitangyong/article/details/36471881