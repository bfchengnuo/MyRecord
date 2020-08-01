# Postman使用

Postman 名声还是很大的啊，web 测试利器啊（尤其是测试 API），今天在使用的时候遇到了一些问题......

其实还是因为不会用，对 HTTP 的了解还是不够！

Get 方式其实没什么可说的，因为它本身就很简单，在使用过程中也没出现什么问题。

## Post

选择 Post 方式后一般都会在 body 里写数据吧，在 body 上面有四个选项：

-   **form-data**

    这又是一个常见的 POST 数据提交的方式。我们使用表单上传文件时，必须让 `<form>` 表单的 enctype 等于 **multipart/form-data**。

-   **x-www-form-urlencoded**

    这应该是最常见的 POST 提交数据的方式了。浏览器的原生 `<form>` 表单，如果不设置 enctype 属性，那么最终就会以 **application/x-www-form-urlencoded** 方式提交数据。

    大部分服务端语言都对这种方式有很好的支持.

    很多时候，我们用 Ajax 提交数据时，也是使用这种方式。例如 JQuery 和 QWrap 的 Ajax，Content-Type 默认值都是「**application/x-www-form-urlencoded;charset=utf-8**」

-   **raw**

    这个选项我们多用来传输 JSON 类型的数据，数据比较灵活，选中后右边默认是 text，可以选择其他的数据格式，会自动的在 Headers 中添加相应的头信息

-   **binary**

    这个就没啥说的了，直接上传二进制文件，比如图片、音频、视频等；相比 form-data 它没有 key，直接就是上传文件

PS:说个小技巧，在地址栏里输入好数据后右键有 encode/decode 选项，自动编码

## 全局变量

开发中，目前很流行使用 token 认证，在 http 请求头里加上 token 来进行认证；

如果在 postman 中每个接口都加一下，太麻烦了，可以巧用全局变量来自动添加。

首先熟悉下两个概念：

- Pre-request-Script

  在请求发送之前执行的脚本

- Tests

  在请求完成之后执行的脚本

对于我们上面简单的需求，我们可以在登陆接口完成后将 token 保存到全局变量中，发其他请求的时候自己带上；

Test 脚本：

``` js
// 是否请求成功
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

var jsonDate = JSON.parse(responseBody) //将响应体转换为 JSON 格式的字符串
pm.globals.set("token", jsonDate.data.token); // 获取Token 值，并设置到全局变量
```

这样可以在右上角里面查看变量。

使用的话，我这里使用比较简单方式，在需要的地方直接使用 `{{token}}` 来引用变量。

PS：postman 的界面右边有大量的脚本提示，并且给你准备好了模版，点一下就能用。

## 环境

在 Postman 右上角哪里是可以选择自定义的环境，关于环境的详细使用等用到再说，先挖坑

## 压力测试

TBD