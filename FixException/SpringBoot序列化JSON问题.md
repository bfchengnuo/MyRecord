使用 SpringBoot 配置了 jackson 日期序列化格式，但是无效。

配置：
``` properties
#日期格式化
spring.jackson.date-format=yyyy-MM-dd
spring.jackson.time-zone=GMT+8
spring.jackson.serialization.write-dates-as-timestamps=false
```

## 原因分析
项目中自定义了 WebMvcConfigurer，会导致 SpringBoot 不再进行自动配置，所以需要手动加入消息转换器。

## 解决
解决方案有很多种，我这里列举一种，我目前使用是没问题的。

``` java
@Configuration
public class Configurer extends WebMvcConfigurationSupport{
	//定义时间格式转换器
	@Bean
	public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
	    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
	    converter.setObjectMapper(mapper);
	    return converter;
	}

	//添加转换器
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	    //将我们定义的时间格式转换器添加到转换器列表中,
	    //这样jackson格式化时候但凡遇到Date类型就会转换成我们定义的格式
	    converters.add(jackson2HttpMessageConverter());
	}
}
```

参考：https://blog.csdn.net/qq_34975710/article/details/84872489
