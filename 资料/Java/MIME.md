# MIME　　

**MIME 类型就是设定某种扩展名的文件用一种应用程序来打开的方式类型**，当该扩展名文件被访问的时候，浏览器会自动使用指定应用程序来打开。多用于指定一些客户端自定义的文件名，以及一些媒体文件打开方式。

MIME 的英文全称是 "Multipurpose Internet Mail Extensions" 多功能Internet 邮件扩充服务，它是一种多用途网际邮件扩充协议，**在1992年最早应用于电子邮件系统**，但后来也应用到浏览器。

服务器会将它们发送的多媒体数据的类型告诉浏览器，而通知手段就是说明该多媒体数据的MIME类型，从而让浏览器知道接收到的信息哪些是MP3文件，哪些是Shockwave文件等等。

服务器将MIME标志符放入传送的数据中来告诉浏览器使用哪种插件读取相关文件。

**MIME 能够支持非 ASCII 字符、二进制格式附件等多种格式的邮件消息**。这个标准被定义在 RFC 2045， RFC 2046， RFC 2047， RFC 2048， RFC 2049等RFC中。 

**由 RFC 822转变而来的RFC 2822，规定电子邮件标准并不允许在邮件消息中使用7位ASCII字符集以外的字符**。正因如此，一些非英语字符消息和二进制文件，图像，声音等非文字消息都不能在电子邮件中传输。MIME规定了用于表示各种各样的数据类型的符号化方法。

浏览器接收到文件后，会进入插件系统进行查找，查找出哪种插件可以识别读取接收到的文件。如果浏览器不清楚调用哪种插件系统，它可能会告诉用户缺少某插件，或者直接选择某现有插件来试图读取接收到的文件，后者可能会导致系统的崩溃。

传输的信息中缺少MIME标识可能导致的情况很难估计，因为某些计算机系统可能不会出现什么故障，但某些计算机可能就会因此而崩溃。

如果服务器没有正确标明其发送的数据的类型，服务器管理员应该正确添加相关信息，具体操作方法非常简单快捷。

在把输出结果传送到浏览器上的时候，浏览器必须启动适当的应用程序来处理这个输出文档。这可以通过多种类型MIME（多功能网际邮件扩充协议）来完成。在HTTP中，MIME类型被定义在Content-Type header中。

例如，假设你要传送一个Microsoft Excel文件到客户端。那么这时的MIME类型就是“application/vnd.ms-excel”。在大多数实际情况中，这个文件然后将传送给Execl来处理（假设我们设定Execl为处理特殊MIME类型的应用程序）。在ASP中，设定MIME类型的方法是通过Response对象的ContentType属性。


## 多媒体文件格式MIME

最早的HTTP协议中，并没有附加的数据类型信息，所有传送的数据都被客户程序解释为超文本标记语言 HTML 文档，而为了支持多媒体数据类型，HTTP 协议中就使用了附加在文档之前的MIME数据类型信息来标识数据类型。

MIME意为多目Internet邮件扩展，它设计的最初目的是为了在发送电子邮件时附加多媒体数据，让邮件客户程序能根据其类型进行处理。然而当它被HTTP协议支持之后，它的意义就更为显著了。它使得HTTP传输的不仅是普通的文本，而变得丰富多彩。

每个MIME类型由两部分组成，前面是数据的大类别，例如声音audio、图象image等，后面定义具体的种类。

### 常见的MIME类型

- 超文本标记语言文本 `.html,.html text/html`
- 普通文本 `.txt text/plain`
- RTF文本 `.rtf application/rtf`
- GIF图形 `.gif image/gif`
- JPEG图形 `.jpeg,.jpg image/jpeg`
- au声音文件 `.au audio/basic`
- MIDI音乐文件 `.mid,.midi audio/midi,audio/x-midi`
- RealAudio音乐文件 `.ra,.ram audio/x-pn-realaudio`
- MPEG文件 `.mpg,.mpeg video/mpeg`
- AVI文件 `.avi video/x-msvideo`
- GZIP文件 `.gz application/x-gzip`
- TAR文件 `.tar application/x-tar`

Internet中有一个专门组织IANA来确认标准的MIME类型，但Internet发展的太快，很多应用程序等不及IANA来确认他们使用的MIME类型为标准类型。因此他们使用在类别中以x-开头的方法标识这个类别还没有成为标准，例如：x-gzip，x-tar等。

事实上这些类型运用的很广泛，已经成为了事实标准。只要客户机和服务器共同承认这个MIME类型，即使它是不标准的类型也没有关系，客户程序就能根据MIME类型，采用具体的处理手段来处理数据。

而Web服务器和浏览器（包括操作系统）中，缺省都设置了标准的和常见的MIME类型，只有对于不常见的 MIME类型，才需要同时设置服务器和客户浏览器，以进行识别。

**由于MIME类型与文档的后缀相关，因此服务器使用文档的后缀来区分不同文件的MIME类型，服务器中必须定义文档后缀和MIME类型之间的对应关系**。而客户程序从服务器上接收数据的时候，它只是从服务器接受数据流，并不了解文档的名字，因此服务器必须使用附加信息来告诉客户程序数据的MIME类型。服务器在发送真正的数据之前，就要先发送标志数据的MIME类型的信息，这个信息使用Content-type关键字进行定义，例如对于HTML文档，服务器将首先发送以下两行MIME标识信息,这个标识并不是真正的数据文件的一部分。

`Content-type: text/html`
注意，第二行为一个空行，这是必须的，使用这个空行的目的是将MIME信息与真正的数据内容分隔开。

MIME利用了一个事实就是，**RFC 822在消息体的内容中做了一点限制：唯一的限制就是只能使用简单的ASCII文本**。

所以，MIME信息由正常的Internet文本邮件组成，文本邮件拥有一些特别的符合RFC 822的信息头和格式化过的信息体（用ASCII 的子集来表示的附件）。这些MIME头给出了一种在邮件中表示附件的特别的方法。

## MIME信息的剖析 

一个普通的文本邮件的信息包含一个头部分（To: From: Subject: 等等）和一个体部分（Hello Mr.,等等）。在一个符合MIME的信息中，也包含一个信息头并不奇怪，邮件的各个部分叫做MIME段，每段前也缀以一个特别的头。MIME邮件只是基于RFC 822邮件的一个扩展。然而它有着自己的RFC规范集。

### 头字段 

MIME头根据在邮件包中的位置，大体上分为MIME信息头和MIME段头。（译者：MIME信息头指整个邮件的头，而MIME段头只每个MIME段的头。） 

MIME信息头有：

- MIME-Version

    这个头提供了所用MIME的版本号。这个值习惯上为1.0。 

- Content-Type

    它定义了数据的类型，以便数据能被适当的处理。有效的类型有：

    text，image，audio，video， applications，multipart和message。

    注意任何一个二进制附件都应该被叫做application/octet- stream。

    这个头的一些用例为：image/jpg, application/mswork，multipart/mixed，这只是很少的一部分。 

- Content-Transfer-Encoding

    这是所有头中最重要的一个，因为它说明了对数据所执行的编码方式，客户/MUA 将用它对附件进行解码。对于每个附件，可以使用7bit，8bit，binary ，quoted-printable，base64和custom中的一种编码方式。

    7bit编码是用在US ASCII字符集上的常用的一种编码方式，也就是，保持它的原样。8bit和binary编码一般不用。对人类可读的标准文本，如果传输要经过对格式有影响的网关时对其进行保护，可以使用quoted printable 。

    **Base64是一种通用方法**，在需要决定使用哪一种编码方法时，它提供了一个不用费脑子的选择；它通常用在二进制，非文本数据上。注意，任何非7bit 数据必须用一种模式编码，这样它就可以通过Internet邮件网关！ 

- Content-ID

    如果 Content-Type 是 message/external-body 或 multipart/alternative 时，这个头就有用了。它超出了本文的范围。 

- Content-Description

    这是一个可选的头。它是任何信息段内容的自由文本描述。描述必须使用us-ascii码。 

- Content-Disposition

    一个试验性的头，它用于给客户程序/MUA提供提示，来决定是否在行内显示附件或作为单独的附件。 

MIME 段头（出现在实际的MIME附件部分的头），除了 MIME-Version头，可以拥有以上任何头字段。如果一个MIME头是信息块的一部分，它将作用于整个信息体。例如，如果Content-Transfer-Encoding显示在信息（指整个信息）头中，它应用于整个信息体，但是如果它显示在一个MIME段里，它"只能"用于那个段中. 

注意：其可以对自动对收到的邮件进行解密 

## MIME类型大全

格式前面为后辍名，后面为对应的MIME型（例如：rar application/x-rar-compressed 表示.RAR对应的是application/x-rar-compressed ）

TODO