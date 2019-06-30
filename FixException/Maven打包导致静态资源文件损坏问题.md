项目中存放了一些 Excel 的导入模板，在打包后这些文件被损坏了，下载下来无法使用。

一般情况下，常用的有三种处理方法。

需要在 pom 文件中使用 maven-resources-plugin 插件进行处理

### 使用resources插件
``` xml
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>

    <!--解决模板文件编译后损坏的问题-->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-resources-plugin</artifactId>
      <configuration>
        <nonFilteredFileExtensions>
          <nonFilteredFileExtension>xlsx</nonFilteredFileExtension>
          <nonFilteredFileExtension>xls</nonFilteredFileExtension>
        </nonFilteredFileExtensions>
      </configuration>
    </plugin>
  </plugins>
</build>
```

还有另外几种方案就是排除这些静态文件，个人比较倾向于使用插件

### 过滤器：
``` xml
<build>
  <resources>
    <resource>
      <directory>src/main/resources</directory>
      <filtering>true</filtering>
    </resource>
  </resources>
</build>
```

### resources 标签：

``` xml
<build>  
  <resources>  
    <resource>  
      <directory>src/main/java</directory>  
      <includes>  
        <include>**/*.xml</include>  
      </includes>

      <excludes>
        <exclude>**/*.xls</exclude>
      </excludes>
    </resource>  
  </resources>  
</build>  
```

另外，IDE 一般也是有相应的操作支持排除。


## 其他问题

还发现在使用 SpringBoot 的方式，打包 jar 进行运行的时候，项目中的静态文件存在 IO 读取不到的问题。
暂时未解决

之前的一个工程是可以的，参考代码：
``` java
public ResponseEntity<byte[]> download() throws Exception {
    //下载文件路径
    String path = ResourceUtils.getFile("classpath:static/template/ex_import.xlsx").getAbsolutePath();
    File file = new File(path);
    HttpHeaders headers = new HttpHeaders();
    //通知浏览器以attachment（下载方式）打开
    headers.setContentDispositionFormData("attachment", new String("导入模板.xlsx".getBytes("UTF-8"),"iso-8859-1"));
    //application/octet-stream ： 二进制流数据（最常见的文件下载）。
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    return new ResponseEntity<>(FileUtils.readFileToByteArray(file),
            headers, HttpStatus.CREATED);
}
```

再补充一个：
``` java
public ResponseEntity<byte[]> download(String templateName) throws IOException {
    ClassPathResource classPathResource = new ClassPathResource(templateName);
    String filename = classPathResource.getFilename();
    @Cleanup InputStream inputStream = classPathResource.getInputStream();
    byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
    // 为了解决中文名称乱码问题
    String fileName = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", fileName);
    return new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);
}
```
