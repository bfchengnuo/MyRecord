首先，配置文件一定要记得归类、归类、归类！

善用 include 进行分类导入，然后记得使用 `nginx -t` 测试一下

## 跨域
如果项目里面已经加了跨域处理，也就是增加了请求头，那么 Nginx 转发里就别设置

`add_header Access-Control-Allow-Origin *;`

如果设置了就会重复，引起错误；

关于这个跨域，会先发一个预处理的请求，要记得放行，也就是个 OPTIONS 的请求，相关知识待补充。。。。


## 增加请求头
为了避免认证，我想让 Nginx 转发的时候自动带上 Authorization 令牌，免得我不好处理，但是使用 add_header 发现不好使；

最终尝试了下将 add_header 改为 proxy_set_header 后就 OK 了。

---

proxy_set_header 和 add_header 的区别：

> proxy_set_header 是 Nginx 设置请求头信息给上游服务器，add_header 是 Nginx 设置响应头信息给浏览器。
