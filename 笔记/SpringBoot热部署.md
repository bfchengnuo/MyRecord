# SpringBoot热部署

## 基础知识

首先来看看 JAVA 热部署与热加载的联系：

1. 都可以不重启服务器的情况下进行编译/部署项目；
2. 基于 Java 的类加载器实现

热部署与热加载的区别：

- 热部署在服务器**运行时**重新部署项目
- 热加载在运行时重新加载 class （字节码文件）
  只加载重新修改后的类（class 文件）
- 热部署会重新加载整个应用
- 热加载在运行时重新加载 class
  可以理解为 JVM 启动后会启动一个后台线程，定时来监控文件的时间戳，如果变化就将类重新载入
- 热部署更多在生产环境下使用，热加载多在开发环境下使用（热加载无法记录“热加载执行的日志”）


下面再来说一下 JVM 加载类的相关知识点，字节码文件肯定是通过类加载器进行加载的，类加载一般可分为五个阶段：

1. 加载
   找到类的静态存储结构，加载到虚拟机里然后转化成方法区运行时的数据结构，生成 class 对象；
   允许用户自定义类加载器参与进来
2. 验证
   确保字节码是安全的，不会对虚拟机造成危害，可以通过启动参数来禁用一些验证（不推荐）
3. 准备
   确定内存布局，初始化类变量（给变量赋初始值不会执行程序自定义的赋值操作）
4. 解析
   将符号引用转换为直接引用
5. 初始化
   这里才是调用程序自定义的初始化代码

关于初始化阶段，Java 规定在遇到五个时机时立即进行初始化（当然前面的步骤已经执行完了的情况下），需要注意的点有：

- 遇到了 new、get、static 这几个字节码指令时如果类没有初始化，则需要触发初始化。
- final 修饰的类会在编译时把结果放到常量池中，即使调用也不会触发初始化。毕竟 final 关键字它修饰的是常量。
- 使用反射对类进行反射调用，如果类没有进行初始化，就需要先初始化。
- 当初始化一个类的时候，如果发现其父类还没有进行过初始化，需要先触发父类的初始化。
  也就是先初始化父类，再初始化子类。
- 虚拟机启动的时候用户需要制定一个要执行的主类，虚拟机会先初始化这个主类。
- 使用 jdk1.7 动态机制相关的句柄会进行初始化。

Java 类加载器的特点：

- 由 APPClass Loader （系统类加载器）开始加载指定的类。
- 类加载器将加载任务交给其父，如果其父找不到，再由自己去加载。
- BootStrap Loader （启动类加载器）是最顶级的类加载器，也就是说他的父加载器为空。

Java 类的热部署可分为 类的热加载 和 配置 Tomcat 的方式，配置 Tomcat 应该很熟悉了，关于类的热加载相关代码参考：

``` java
// 自定义的类加载器
public class MyClassLoader extends ClassLoader{
  private String path;
  
  public MyClassLoader(String path){
    super(ClassLoader.getSystemClassLoader());
    this.path = path;
  }
  
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException{
    System.out.println("加载类.....");
    byte[] data = loadClassData(name);
    return this.defineClass(name,data,0,data.length);
  }
  
  // 加载 Class 文件中的内容
  private byte[] loadClassData(String name){
    try{
      name = name.replace(".", "//");
      FileInputStream is = new FileInputStream(new File(path + name + ".class"));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int b = 0;
      while((b = is.read()) != -1){
        baos.write(b);
      }
      is.close();
      return baos.toByteArray();
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
}
```

对于Tomcat 直接把项目放到 webapps 目录里就会自动加载；server.xml 里的 host 添加 context 

## SpringBoot

使用 SpringBoot 进行热部署总体来说有两种方式，

一种是使用 springloaded（依赖配置在 build 中的 **spring-boot-maven-plugin** 插件中），必须要使用 `mvn spring-boot:run` 来允许才有效果，或者下载这个 jar 在 JVM 的启动参数里配置。

第二种就比较简单了，直接和平常一样加入一个 devtools 依赖就可以了：

``` xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-devtools</artifactId>
  <optional>true</optional>
</dependency>
```

是的，就是这么简单，推荐使用第二种

###  发布

SpringBoot 项目可以使用 jar 包来直接运行，也可以发布为 war 丢到 tomcat 里去允许，

第一种就不用多说了，运行 maven 的 **install** 后，直接命令行启动就行：`java -jar xxx.jar`

第二种，首先打包方式改为 war 包，然后增加一个，然后在 application 入口类继承 SpringBootServletInitializer，复写 configure 方法：

``` java
// @SpringBootApplication：Spring Boot项目的核心注解，主要目的是开启自动配置
@SpringBootApplication
public class FirstSpringBootApplication extends SpringBootServletInitializer {
  
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
    return builder.sources(FirstSpringBootApplication.class);
  }

  public static void main(String[] args) {
    // 启动 SpringBoot 所必须的入口
    SpringApplication.run(FirstSpringBootApplication.class, args);
  }
}
```

需要加入的依赖，SpringBoot 中加依赖不需要指定版本，在父工程已经设置好了，并且名称都是 **spring-boot-*** ：

``` xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-tomcat</artifactId>
</dependency>
```

然后执行 `maven install` 得到 war 包就可以了

参考自慕课网课程：

https://www.imooc.com/learn/915