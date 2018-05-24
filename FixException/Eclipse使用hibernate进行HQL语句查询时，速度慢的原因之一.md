> http://www.imooc.com/article/19304

虽然 hibernate 已经基本不太用了，但是无奈，还是得学。

用 Eclipse 测试 HQL 语句，前面卡好长时间，加上 log4j 配置文件后查看日志发现，大部分时间卡在配置文件 cfg 和映射文件 hbm 的文件头 dtd 信息上。

这应该是用工具自动反向生成 Bean 的后遗症吧，dtd 头信息给改了

传言说：
说把这一句替换成 `"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd"` 会提高读取速度，试了一下确实管用。。。

如果你使用了 hbm 映射文件的话也是要替换的，就是替换前面的网址为 http://hibernate.sourceforge.net/。

原因目前不明，TODO
