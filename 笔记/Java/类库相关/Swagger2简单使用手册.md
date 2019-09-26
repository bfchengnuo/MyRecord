环境当然是以 SB 为例，用惯了 SB 真的回不去传统的框架了。

默认 UI 界面地址：`/swagger-ui.html`

UI 是可以自定义的，例如 [swagger-ui-layer](https://github.com/caspar-chen/swagger-ui-layer)、[swagger-bootstrap-ui](https://github.com/xiaoymin/Swagger-Bootstrap-UI)

## 集成准备

加入依赖：

``` xml
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-swagger2</artifactId>
  <version>2.x</version>
</dependency>
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-swagger-ui</artifactId>
  <version>2.x</version>
</dependency>
```

创建配置类：

``` java
@Configuration
@EnableSwagger2
public class Swagger2 {
  @Bean
  public Docket createRestApi() {
    return new Docket(DocumentationType.SWAGGER_2)
      .apiInfo(apiInfo())
      .select()
      .apis(RequestHandlerSelectors.basePackage("com.bfchengnuo.web"))
      .paths(PathSelectors.any())
      .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
      .title("Spring Boot 中使用 Swagger2 构建 RESTful APIs")
      .description("这里是描述")
      .termsOfServiceUrl("https://bfchengnuo.com/")
      .contact("Kerronex")
      .version("1.0")
      .build();
  }
}
```

其中 `@EnableSwagger2` 这个注解可以加在任何一个配置类上，不过一般放在 SB 的引导类上就行。

如果没有特别的配置需求，只需要这一个注解完全就够了。

## 常用注解

- @Api(description = “接口**类**的描述”)

  需要注意的是在 1.5+ 版本中，这个属性被标记为过时，并且无可替代

- @ApiOperation(value = “接口**方法**的名称”, notes = “备注说明”)

- @ApiParam(name = “**参数**名称”, value = “备注说明”, required = 是否必须)

- @ApiModel

- @ApiModelProperty

-  @ApiImplicitParam

- @ApiImplicitParams

- @ApiIgnore

  用于或略该接口，不生成该接口的文档

## 示例代码

一个 controller：

``` java
@RestController
@RequestMapping(value="/users")
public class UserController {

  static Map<Long, User> users = Collections.synchronizedMap(new HashMap<Long, User>());

  @ApiOperation(value="获取用户列表", notes="")
  @RequestMapping(value={""}, method=RequestMethod.GET)
  public List<User> getUserList() {
    List<User> r = new ArrayList<User>(users.values());
    return r;
  }

  @ApiOperation(value="创建用户", notes="根据User对象创建用户")
  @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
  @RequestMapping(value="", method=RequestMethod.POST)
  public String postUser(@RequestBody User user) {
    users.put(user.getId(), user);
    return "success";
  }

  @ApiOperation(value="获取用户详细信息", notes="根据url的id来获取用户详细信息")
  @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
  @RequestMapping(value="/{id}", method=RequestMethod.GET)
  public User getUser(@PathVariable Long id) {
    return users.get(id);
  }

  @ApiOperation(value="更新用户详细信息", notes="根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long"),
    @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
  })
  @RequestMapping(value="/{id}", method=RequestMethod.PUT)
  public String putUser(@PathVariable Long id, @RequestBody User user) {
    User u = users.get(id);
    u.setName(user.getName());
    u.setAge(user.getAge());
    users.put(id, u);
    return "success";
  }

  @ApiOperation(value="删除用户", notes="根据url的id来指定删除对象")
  @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
  @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
  public String deleteUser(@PathVariable Long id) {
    users.remove(id);
    return "success";
  }
}
```

## 聚合多个项目

TODO