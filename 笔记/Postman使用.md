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

## 环境

在 Postman 右上角哪里是可以选择自定义的环境，关于环境的详细使用等用到再说，先挖坑