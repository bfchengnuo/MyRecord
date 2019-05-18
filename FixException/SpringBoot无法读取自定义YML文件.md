# SpringBoot无法读取自定义YML文件

## 起因：

有一点结构比较复杂的静态数据需要读取，那么当然就写在配置文件里了。

然后对于这种复杂数据（Map），用 properties 的有点麻烦，然后想到了 YML 天生支持 List、Map 这种数据结构，于是创建了一个 `xxx.yml` 配合 `@PropertySource` 注解指定来读取。

结果就是也不报错，但是获取的值就是 null

## 原因：

尝试了各种写法，都不行，最后在官方文档中找到了这么一句话：

> YAML files cannot be loaded by using the `@PropertySource` annotation. So, in the case that you need to load values that way, you need to use a properties file.
>
> Using the multi YAML document syntax in profile-specific YAML files can lead to unexpected behavior. For example, consider the following config in a file called `application-dev.yml`, with the `dev` profile being active:
>
> ```
> server:
>   port: 8000
> ---
> spring:
>   profiles: !test
>   security:
>     user:
>       password: weak
> ```
>
> In the example above, profile negation and profile expressions will not behave as expected. We recommend that you don’t combine profile-specific YAML files and multiple YAML documents and stick to using only one of them.

https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-yaml-shortcomings

PS：@ConfigurationProperties 注解的 locations 属性在 1.5.X 以后没有了，请使用 @PropertySource

## 解决：

既然官方都说了不支持，那也没什么办法了，网上有重写自动配置来实现识别的，不过比较麻烦，我就图省事直接写在 application.yml 文件中了，配置多的话使用 `---` 分割一下来区分不同的 profiles 也挺好的。

或者参考下这种：

``` java
@Component
// 通过 @PropertySource 注解指定要读取的 yaml 配置文件，
// 默认读取 application.yml 配置
@PropertySource(value = "classpath:config/myConfig.yml",
               factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "test")
public class ConfigProperties {
    //...
}
```

自定义工厂：

``` java
/**
 * 实现 yaml 配置文件加载工厂，以使用 @PropertySource 注解加载指定 yaml 文件的配置
 */
public class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {
  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {

    if (null == resource) {
      super.createPropertySource(name, resource);
    }
    return new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource()).get(0);
  }
}
```

