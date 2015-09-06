1. WebApp 
==========

  WebApp模块集成了多个开源组件，包括Jetty，SpringMvc，SpringSecurity等，其应用组装逻辑较为复杂，参考`net.happyonroad.WebAppConfig`，加载的次序为:
  
```
Spring Component AppLauncher
   |- Web App Config :     "组件应用入口"
   |   |- JettyServer:     "内嵌的Web容器"
   |   |   |- AnnotationConfiguration : "Web应用配置"
   |   |   |   |- SpringMvcLoader : "Spring Mvc应用作为Web Servlet的加载器"
   |   |   |   |   |- SpringSecurityConfig: "Spring 安全配置，需要与实际的用户管理，权限/授权系统对接"
   |   |   |   |   |   |- SpringMvcConfig : "Spring MVC的配置"
```

JettyServer读取系统以下两个属性，将HTTP服务绑定在本地Socket端口；

  1. app.host=...
    * app.host不填默认为主机第一个有效对外ip
    * app.host设为0.0.0.0则绑定在本机所有ip上
    * app.host设为127.0.0.1则限制将web服务绑定在本地地址上，仅有本机访问者可见，当Insight Server与nginx安装在同一台机器上时这个设置安全可靠
  2. http.port=<8000>
    * 该端口不是nginx的对外端口
    * 该端口为当前进程的web服务端口，nginx中upstream项应该指向该端口

SpringMvcLoader读取以下系统属性决定WEB服务的根Context Path

    http.url=/

开发者可以在config/server.properties文件中，配置

```
  spring_mvc.loader=dnt.monitor.server.web.MonitorMvcLoader
  spring_mvc.configuration=dnt.monitor.server.web.MonitorMvcConfig
  security.configuration=dnt.monitor.server.web.MonitorSecurityConfig
```

等属性，覆盖默认的Web应用相关配置

1.1 Spring MVC控制器开发 
-------------------


  由于MonitorServer的应用程序启动选项中已经定义了
`-Dcomponent.feature.resolvers=net.happyonroad.platform.resolver.SpringMvcFeatureResolver,...`
这说明，监控服务器支持各个组件模块分散的开发Web应用程序，具体来讲:

* 开发者应该为自己的组件配置Manifest属性Web-Repository(如果能从上级pom继承该配置亦可)

```
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <Web-Repository>dnt.monitor.web.conroller</Web-Repository>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

* SpringMvcFeatureResolver将会在平台WebApp启动时，扫描Web-Repository指定的目录(多个可以用逗号分开)，寻找所有的 `@Controller`, `@RestController` 等Spring MVC标准标记（包括 `@Component`, `@Service`, `@Configuration` 等，但不建议放Web-Repository目录中）
* 这些控制器，可以如一般的spring mvc controller一样， 通过`@Autowire`、`@Inject`注入依赖的beans和服务，通过`@RequestMapping`等处理特定路由

1.2 对Spring MVC的增强 
--------------------

1、 GET /routes 获取所有的API，以及注释

```
列出当前服务器所提供的所有API
   GET /routes?detail={boolean}&pattern={String}&method={String}

获取所有的分类信息的列表
   GET /api/categories?sort={string}&count={int}&page={int}

创建一个分类
   POST /api/categories                                   
...
```

  WebApp会扫描所有控制器的RequestMapping，形成Routes的结果，另外，以上的注释，开发者只需要为控制器方法增加`@Description`标记即可，如：

```
@RestController
@RequestMapping("/api/categories")
class CategoriesController extends GreedyPathController<Category> {
    @Autowired
    CategoryService service;

    @RequestMapping
    @Description("获取所有的分类信息的列表")
    public List<Category> index() {
        return service.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    @Description("创建一个分类")
    public Category create(@Valid @RequestBody Category category) {
    ...
   
```

2、 利用`@BeforeFilter`, `@AfterFilter`写出DRY的控制器代码

典型的控制器，show/update/delete等方法的RESTFul URL为 [GET|PUT|DELETE] /users/:id
其实现逻辑中，均要进行对象User的查询，如下：

```
@RestController
public class UsersController extends ApplicationController{
  @Autowired 
  UserService service;

  @RequestMapping
  public User show(@PathVariable("id")long id){
    User user = servce.find(id);
    if( user == null ) throw new WebClientException("id is invalid");
    return user;
  }

  @RequestMapping(method=PUT)
  public User update(@PathVariable("id")long id, @RequestBody User newUser){
    User user = servce.find(id);
    if( user == null ) throw new WebClientException("id is invalid");
    service.update(user,newUser);
  }

  @RequestMapping(method=DELETE)
  public User delete(@PathVariable("id")long id){
    User user = servce.find(id);
    if( user == null ) throw new WebClientException("id is invalid");
    service.delete(user);
  }
}
```

以上几个函数中，查询user是重复的，基于BeforeFilter，可以改写为:

```
@RestController
public class UsersController extends ApplicationController{
  @Autowired
  UserService service;
  User user;

  @RequestMapping
  public User show(){
    return this.user;
  }

  @RequestMapping(method=PUT)
  public User update(@RequestBody User newUser){
    service.update(this.user,newUser);
  }

  @RequestMapping(method=DELETE)
  public User delete(){
    service.delete(this.user);
  }

  @BeforeFilter("show", "update", "delete")
  public void initUser(@PathVariable("id") long id){
    this.user = servce.find(id);
    if( this.user == null ) throw new WebClientException("id is invalid");
  }
}
```

再有例子：

  控制器基类`ApplicationController`通过`@BeforeFilter`实现了为`index`方法解析分页信息到成员变量上`pageRequest`，再通过`@AfterFilter`将存储在`indexPage`对象上的分页信息，通过http respond header输出，从而：

* 避免控制器index方法返回 Page<Record>，导致每个客户端在返回数据体内解析分页信息
* 统一所以的分页查询相关的请求
  * page：第几页
  * count: 每页条目
  * sort: 排序方式
   
```
    protected PageRequest pageRequest;
    protected Page<T> indexPage;

    @BeforeFilter(method = RequestMethod.GET, value = "index")
    public void initDefaultPageRequest( @RequestParam(required = false, value = "page", defaultValue = "0") int page,
                                        @RequestParam(required = false, value = "size", defaultValue = "40") int size,
                                        @RequestParam(required = false, value = "sort", defaultValue = "") String sort){
        Sort theSort = parseSort(sort);
        pageRequest = new PageRequest(page, size, theSort);
    }
```
   
所以典型的 index 可以实现为:
   
```
@RestController
public class UsersController extends ApplicationController<User> {
    @Autowired
    UserService service;

    @RequestMapping
    public Page<User> index() {// Page将会被分解成为response的header和body
        logger.debug("Listing users by {}", pageRequest);
        indexPage = userService.findAll(pageRequest);// pageRequest已经由父类的beforeFilter构建为成员变量
        logger.debug("Found   users {}", indexPage);
        return indexPage;
    }
}
```

请注意
> 方法直接返回的Page&lt;User&gt; 由`@RestController`的作用标记为`@ResponseBody`，并由Spring MVC Response Processor转换为header + application/json MIME类型的JSON内容给客户端。
  
备注：
> 这两个annotation所标记的方法（如`initDefaultPageRequest`）必须是控制器的public成员方法
> 与一般的控制器方法一样，支持采用Spring MVC的标准Annotation(`@PathVariable`, `@RequestParam`,`@RequestBody`,`@RequestPart`,`@RequestHeader`,`@CookieValue`)等进行参数植入 
  
`@BeforeFilter`, `@AfterFilter`支持的参数如下：

    A. int order() default 50; //过滤器顺序，越小越靠前执行 
    B. RequestMethod[] method() default {}; //适用的HTTP方法，支持多个方法
    C. String[] value() default {}; //适用的控制器方法名称
 
1.3 Spring Security 整合 
------------------

A、 CRSF保护
通过`GET /security/csrf` 可以获取当前的CSRF信息，如下：
```json
{
  "headerName":"X-CSRF-TOKEN",
  "parameterName":"_csrf",
  "token":"541ac4a0-36d2-41eb-a073-962e163c3219"
}
```
每次POST请求之后，CSRF Token都会变化，请在客户端及时`GET /security/csrf`更新，并在下次POST请求时提交。

B、 用户认证

默认支持两种认证方式

    Form认证   
      登录：`POST /api/session` 其中带上 username, password 等认证信息
      登出：`DELETE /api/session`  
     Basic Authentication认证
      curl -u user:password http://host:port
   
  认证成功之后，当前用户名可以根据Servlet规范，从 `request.getRemoteUser()` 获得， 当前用户身份可以从 `request.getUserPrincipal()` 获得   

C、 用户授权

  请参考 [Spring Security](http://docs.spring.io/spring-security/site/docs/3.2.4.RELEASE/reference/htmlsingle/#authorization)

2. Mybatis 
=========

  由于MonitorServer的应用程序启动选项中已经定义了
`-Dcomponent.feature.resolvers=net.happyonroad.platform.resolver.MybatisFeatureResolver,...`
这说明，监控服务器支持各个组件模块分散的开发Mybatis Repository，具体来讲:

1、 开发者应该为自己的组件配置Manifest属性DB-Repository(从上级pom继承该配置亦可)

```
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <DB-Repository>dnt.monitor.repository</DB-Repository>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
2、 MybatisFeatureResolver将会在系统启动后，扫描DB-Repository指定的目录(多个可以用逗号分开)，寻找所有的Repository类

3、 Migrate脚本应该被放在各个组件包的 /META-INF/migrate目录

4、 如果对mybatis有配置，应该放在各个组件包的 /META-INF/mybatis.xml 文件中
 
3 Redis组件 
===

Redis组件被用作如下两个作用：

1. Cache组件(参见 `CacheService` )
   * 用MapContainer存储Key/Value(如缓存的元模型实例)
   * 用ListContainer存储集合(如发给监控引擎的离线消息)
2. MessageBus组件(参见`MessageBus`)
   * Publish/Subscribe消息
