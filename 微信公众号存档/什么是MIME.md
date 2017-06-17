# 什么是MIME

多用途互联网**邮件扩展**（MIME，Multipurpose Internet Mail Extensions）是一个互联网标准，它扩展了电子邮件标准，使其能够支持：

-   非 ASCII 字符文本；
-   非文本格式附件（二进制、声音、图像等）；
-   由多部分（multiple parts）组成的消息体；
-   包含非 ASCII 字符的头信息（Header information）。这个标准被定义在 RFC 2045、RFC 2046、RFC 2047、RFC 2048、RFC 2049 等 RFC中。

MIME 改善了由 RFC 822 转变而来的 RFC 2822，这些旧标准规定电子邮件标准并不允许在邮件消息中使用7位 ASCII 字符集以外的字符。正因如此，一些非英语字符消息和二进制文件，图像，声音等非文字消息原本都不能在电子邮件中传输（MIME可以）。MIME 规定了用于表示各种各样的数据类型的符号化方法。

此外，在万维网中使用的 HTTP 协议中也使用了 MIME 的框架，标准被扩展为互联网媒体类型。

MIME 是通过标准化电子邮件报文的头部的附加域（fields）而实现的；这些头部的附加域，描述新的报文类型的内容和组织形式。

## 内容类型

这个应该是经常看到的了

内容类型（Content-Type），这个头部领域用于指定消息的类型。一般以下面的形式出现。

```
Content-Type: [type]/[subtype]; parameter

```

type 有下面的形式。

-   Text：用于标准化地表示的文本信息，文本消息可以是多种字符集和或者多种格式的；
-   Multipart：用于连接消息体的多个部分构成一个消息，这些部分可以是不同类型的数据；
-   Application：用于传输应用程序数据或者二进制数据；
-   Message：用于包装一个E-mail消息；
-   Image：用于传输静态图片数据；
-   Audio：用于传输音频或者音声数据；
-   Video：用于传输动态影像数据，可以是与音频编辑在一起的视频数据格式。

**subtype 用于指定 type 的详细形式。**content-type/subtype 配对的集合和与此相关的参数，将随着时间而增长。为了确保这些值在一个有序而且公开的状态下开发，MIME 使用 Internet Assigned Numbers Authority (IANA) 作为中心的注册机制来管理这些值。常用的 subtype 值如下所示：

-   text/plain（纯文本）
-   text/html（HTML文档）
-   application/xhtml+xml（XHTML文档）
-   image/gif（GIF图像）
-   image/jpeg（JPEG图像）【PHP中为：image/pjpeg】
-   image/png（PNG图像）【PHP中为：image/x-png】
-   video/mpeg（MPEG动画）
-   application/octet-stream（任意的二进制数据）
-   application/pdf（PDF文档）
-   application/msword（Microsoft Word文件）
-   application/vnd.wap.xhtml+xml (wap1.0+)
-   application/xhtml+xml (wap2.0+)
-   message/rfc822（RFC 822形式）
-   multipart/alternative（HTML邮件的HTML形式和纯文本形式，相同内容使用不同形式表示）
-   application/x-www-form-urlencoded（使用HTTP的POST方法提交的表单）
-   multipart/form-data（同上，但主要用于表单提交时伴随文件上传的场合）

此外，尚未被接受为正式数据类型的 subtype，可以使用 **x-开始的独立名称**（例如application/x-gzip）。**vnd-开始的固有名称**也可以使用（例：application/vnd.ms-excel）。

**parameter 可以用来指定附加的信息**，更多情况下是用于指定 text/plain 和 text/htm 等的文字编码方式的 charset 参数。

MIME 根据 type 制定了默认的 subtype，当客户端不能确定消息的 subtype 的情况下，消息被看作默认的 subtype 进行处理。Text 默认是 text/plain，Application 默认是 application/octet-stream 而 Multipart 默认情况下被看作 multipart/mixed。

## 总结

是的，MIME 最开始是为了解决电子邮件问题的，老外发明的原始版本的电子邮件只能发送 ASCII 编码的字符，所以说木有图片、音频等二进制文件；木有附件；当然也不能输入汉字.....

关于电子邮件我想单独再写一篇的，确实有点搞头...

反正陌生的连接不要点，外链“图片”也不用作死点，毕竟电子邮件可以理解为 HTML 嘛