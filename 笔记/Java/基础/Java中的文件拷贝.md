# Java中的文件拷贝

这个情景一般是用在文件的上传下载，虽然现在主要是云存储或者 FTP 来做，但是文件拷贝确实是个很有意思的点。

常用方式：

- 传统 IO：例如使用 FileInputStream、FileOutputStream 来做。

- 利用 `java.nio` 类库提供的 transferTo 或 transferFrom 方法。

  在 Linux 和 Unix 上，则会使用到零拷贝技术，数据传输并不需要用户态参与，省去了上下文切换的开销和不必要的内存拷贝，进而可能提高应用拷贝性能

- 使用 `java.nio.file.Files.copy()` 工具类。

  此方法有多个不同的重载，但应该都是对第一种的封装。

以上的方式递进也是随着 Java I/O 的不断升级，例如最具特点的 NIO 的多路复用，NIO 2 的异步 IO，以及 J8 之后所谓的零拷贝概念。

---

NIO 参考：

``` java
public static void copyFileByChannel(File source, File target) throws IOException {
  try (FileChannel sc = new FileInputStream(source).getChannel();
       FileChannel tc = new FileOutputStream(target).getChannel();) {
    long count = sc.size();
    while (count > 0) {
      long transferred = sc.transferTo(sc.position(), count, tc);
      count -= transferred;
    }
  }
}
```

即：总体上来说，NIO transferTo/transferFrom 的方式**可能更快**，因为它更能利用现代操作系统底层机制，避免不必要拷贝和上下文切换。

## SpringBoot配置

在上传下载的场景中，大概率会外部化配置上传的路径，这个配置简单的可以直接用 @Value 读取，也可以使用 SB 的专用的方式，例如：

假设配置文件中有：`file.upload-dir=./uploads`

对应的属性 Bean 为：

``` java
@ConfigurationProperties(prefix = "file")
public class FileProperties {
  private String uploadDir;

  public String getUploadDir() {
    return uploadDir;
  }
  public void setUploadDir(String uploadDir) {
    this.uploadDir = uploadDir;
  }
}
```

在启动类中开启相关的配置解析：

``` java
@SpringBootApplication
@EnableConfigurationProperties({
  FileProperties.class
    })
public class SpringBootFileApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringBootFileApplication.class, args);
  }
}
```

这样就可以在 Spring 中直接注入使用了。

## 参考

[Java的文件拷贝方式](https://www.heshengbang.tech/2018/06/%E7%AC%AC12%E8%AE%B2-Java%E7%9A%84%E6%96%87%E4%BB%B6%E6%8B%B7%E8%B4%9D%E6%96%B9%E5%BC%8F/)

[SpringMVC中的文件上传下载](https://github.com/bfchengnuo/JavaReplay/blob/master/UseLibraries/src/main/java/com/bfchengnuo/uselibraries/spring/web/FileController.java)

---

其他参考：https://juejin.im/post/6844903809852899336