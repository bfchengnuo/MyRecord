# SSH配置文件相关

SSH 程序可以从以下途径获取配置参数：

> 用户配置文件 (`~/.ssh/config`)
> 系统配置文件 (`/etc/ssh/ssh_config`)

配置文件可分为多个配置区段，**每个配置区段使用”Host”来区分**。我们可以在命令行中输入不同的Host来加载不同的配置段。

## 配置项Config

下面先介绍一些常用的SSH配置项

- Host 别名
- HostName 主机名
- Port 端口
- User 用户名
- IdentityFile 密钥文件的路径
- IdentitiesOnly 只接受SSH key 登录
- PreferredAuthentications 强制使用 Public Key 验证

我们可以在配置文件中配置 SSH 信息来进行会话管理。

参考：https://mp.weixin.qq.com/s/gaeu6nGxxQPRbbbWbwDiiA

### 基本使用示例

使用指定别名登录到 www.hi-linux.com 这台主机。

```
Host www
    HostName www.hi-linux.com
    Port 22
    User root
    IdentityFile  ~/.ssh/id_rsa
    IdentitiesOnly yes
$ ssh www
```

不同主机使用同一私钥进行登陆。

```
Host github.com git.coding.net
    HostName %h
    Port 22
    User git
    IdentityFile  ~/.ssh/id_rsa_blog
    IdentitiesOnly yes
$ ssh -T git@git.coding.net
$ ssh -T git@github.com
```

可以通过 `man ssh_config`，查看 ~/.ssh/config 的语法。

---

免密登陆需要服务器在 `~/.ssh/authorized_keys ` 中配置你的公钥才行（可配置多个）。

或者可以使用便捷工具：ssh-copy-id (`ssh-copy-id -i ~/.ssh/id_rsa.pub user@server`)

## known_hosts

A 通过 ssh 首次连接到 B，B 会将公钥1（host key）传递给 A，A 将公钥 1 存入 known_hosts 文件中，以后 A 再连接 B 时，B 依然会传递给 A 一个公钥 2，OpenSSH 会核对公钥，通过对比公钥 1 与公钥 2 是否相同来进行简单的验证，如果公钥不同，OpenSSH 会发出警告， 避免你受到 DNS Hijack 之类的攻击。

几种签名方式：

- RSA

  RSA 是目前计算机密码学中最经典算法，也是目前为止使用最广泛的数字签名算法，RSA 数字签名算法的密钥实现与 RSA 的加密算法是一样的，算法的名称都叫 RSA。

  密钥的产生和转换都是一样的，包括在售的所有 SSL 数字证书、代码签名证书、文档签名以及邮件签名大多都采用 RSA 算法进行加密。

  RSA 数字签名算法主要包括 MD 和 SHA 两种算法，例如我们熟知的 MD5 和 SHA-256 即是这两种算法中的一类。

- DSA

  DSA全称Digital Signature Algorithm，DSA 只是一种算法，当初的设计是**用于数字签名而不用于密钥交换的算法**，所以它比 RSA 要快很多，其安全性与 RSA 相比差不多。

  DSA 的一个重要特点是两个素数公开，这样，当使用别人的 p 和 q 时，即使不知道私钥，你也能确认它们是否是随机产生的，还是作了手脚。RSA 算法却做不到。

- ECDSA

  又称为椭圆曲线数字签名算法，ECDSA 是**用于数字签名**，是 ECC 与 DSA 的结合，整个签名过程与 DSA 类似，所不一样的是签名中采取的算法为 ECC，最后签名出来的值也是分为 r,s。

  而 ECC（全称 Elliptic Curves Cryptography）是一种椭圆曲线密码编码学。

ECC 与 RSA 相比，有以下的优点：

- 相同密钥长度下，安全性能更高，如 160 位 ECC 已经与 1024 位 RSA、DSA 有相同的安全强度。
- 计算量小，处理速度快，在私钥的处理速度上（解密和签名），ECC 远比 RSA、DSA 快得多。
- 存储空间占用小 ECC 的密钥尺寸和系统参数与 RSA、DSA 相比要小得多， 所以占用的存储空间小得多。
- 带宽要求低使得 ECC 具有广泛得应用前景。

目前密钥交换 + 签名有三种主流选择：

- RSA 密钥交换（无需签名）；
- ECDHE 密钥交换、RSA 签名；
- ECDHE 密钥交换、ECDSA 签名；

ECDH 每次用一个固定的 DH key，导致不能向前保密（forward secrecy），所以一般都是用 ECDHE（ephemeral）或其他版本的 ECDH 算法。ECDH 则是基于 ECC 的 DH（ Diffie-Hellman）密钥交换算法。

**速度方面：**

对于 ECDSA 来说，生成签名与验证签名的开销相差不大，而对于 RSA 来说，验证签名比生成签名要高效得多，这是因为 RSA 可以选用小公钥指数，而安全强度不变。

如果只看单次操作，那么 ECDSA 的 Sign 操作比 RSA 的性能更好，而 RSA 的 Verify 要比 ECDSA 更好。

---

所以，RSA 签名算法适合于 Verify 操作频度高，而 Sign 操作频度低的应用场景，例如分布式中；

ECDSA 签名算法适合于 Sign 和 Verify 操作频度相当的应用场景。比如点对点的安全信道建立。

> 密钥交换与数字签名：
>
> 记得在 SSL/TLS 中说过，它的过程分为两段，与 SSH 的认证类似，首先是验证身份，通过非对称加密，也就是所谓的数字签名，签名的时候用私钥，验证签名的时候用公钥。
>
> 之后会随机生成一个对称加密的密码，因为非对称效率太低了，传输这个密码的过程就是密钥交换。
>
> 密钥交换（英语：Key exchange，也称key establishment）是密码学中两方交换密钥以允许使用某种加密算法的过程。

参考：https://zhuanlan.zhihu.com/p/33195438

## 密钥登陆

密钥登录比密码登录安全，主要是因为他使用了非对称加密，登录过程中需要用到**密钥对**。整个登录流程如下：

1. 远程服务器持有公钥，当有用户进行登录，服务器就会随机生成一串字符串，然后发送给正在进行登录的用户。
2. 用户收到远程服务器发来的字符串，使用与**远程服务器公钥配对的私钥**对字符串进行加密，再发送给远程服务器。
3. 服务器使用公钥对用户发来的加密字符串进行解密，得到的解密字符串如果与第一步中发送给客户端的随机字符串一样，那么判断为登录成功。

整个登录的流程就是这么简单，但是在实际使用 ssh 登录中还会碰到一些小细节，这里演示一遍 ssh 远程登录来展示下这些细节问题。

https://segmentfault.com/a/1190000012333003