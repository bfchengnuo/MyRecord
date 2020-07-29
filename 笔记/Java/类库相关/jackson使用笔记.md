# jackson使用笔记

序列化与反序列化 JSON 这块，基本框架都集成好了，一般不需要操心；

但是在某些特殊情况下，还是需要手动处理，之前应该也看过，但是长时间不忘还是忘了。。。

好像还用过阿里的 fastJson，都差不多真猜不出来了再来补充，不过看[官方的 wiki](https://github.com/alibaba/fastjson/wiki) 基本够用了。

## 基本使用

最常用的反正就是对象与字符串之间的转换：

``` java
ObjectMapper objectMapper = new ObjectMapper();
String carJson = "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
Car car = objectMapper.readValue(carJson, Car.class);


Car car = new Car();
car.brand = "BMW";
car.doors = 4;
//写到文件中
objectMapper.writeValue( new FileOutputStream("data/output-2.json"), car);
//写到字符串中
String json = objectMapper.writeValueAsString(car);
```

如果你用了 Jackson 的依赖，ObjectMapper 这个一般可以直接从 IOC 容器中取。

Map、List 用的也比较多，有时候懒得建对象，于是我忘了，搜了下是

``` java
String jsonArray = "[{\"brand\":\"ford\"}, {\"brand\":\"Fiat\"}]";
List<Car> cars1 = objectMapper.readValue(jsonArray, new TypeReference<List<Car>>(){});

String jsonObject = "{\"brand\":\"ford\", \"doors\":5}";
Map<String, Object> jsonMap = objectMapper.readValue(jsonObject,
    new TypeReference<Map<String,Object>>(){});
```

当我看到 TypeReference 的时候，我终于知道我忘记什么东西了。。。。

以上，基本够用了。

## 序列化

其中使用比较多的是 @JsonIgnore 注解，加在字段上，反序列化、序列化都忽略；

还有一些情况需要忽略部分，指的是忽略序列化，或者忽略反序列化；这时候可以加在对应的 getter、setter 方法上；

Jackson 的新版本为 @JsonProperty 添加了 `READ_ONLY` 和 `WRITE_ONLY` 注释参数；

例如：`@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)`

这种也可以达到忽略部分的效果。

## 参考

https://www.jianshu.com/p/67b6da565f81