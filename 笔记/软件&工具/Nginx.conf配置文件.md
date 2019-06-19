原文：https://segmentfault.com/a/1190000002797601#articleHeader8
---

Nginx 配置文件主要分成四部分：

- main（全局设置）

  main 部分设置的指令将影响其它所有部分的设置；
- server（主机设置）

  server 部分的指令主要用于指定虚拟主机域名、IP和端口；
- upstream（上游服务器设置，主要为反向代理、负载均衡相关配置）

  upstream 的指令用于设置一系列的后端服务器，设置反向代理及后端服务器的负载均衡；
- location（URL匹配特定位置后的设置）

  location 部分用于匹配网页位置（比如，根目录“/”,“/images”,等等）

每部分包含若干个指令。

他们之间的关系式：**server 继承 main，location 继承 server；upstream 既不会继承指令也不会被继承。它有自己的特殊指令，不需要在其他地方的应用。**

比如，下面的一个示例配置就是用 Nginx 来处理静态资源 Tomcat 来处理动态资源：

``` conf
user  www www;
worker_processes  2;

error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

pid        logs/nginx.pid;


events {
    use epoll;
    worker_connections  2048;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    # tcp_nopush     on;

    keepalive_timeout  65;

  # gzip压缩功能设置
    gzip on;
    gzip_min_length 1k;
    gzip_buffers    4 16k;
    gzip_http_version 1.0;
    gzip_comp_level 6;
    gzip_types text/html text/plain text/css text/javascript application/json application/javascript application/x-javascript application/xml;
    gzip_vary on;

  # http_proxy 设置
    client_max_body_size   10m;
    client_body_buffer_size   128k;
    proxy_connect_timeout   75;
    proxy_send_timeout   75;
    proxy_read_timeout   75;
    proxy_buffer_size   4k;
    proxy_buffers   4 32k;
    proxy_busy_buffers_size   64k;
    proxy_temp_file_write_size  64k;
    proxy_temp_path   /usr/local/nginx/proxy_temp 1 2;

  # 设定负载均衡后台服务器列表 
    upstream  backend  { 
              #ip_hash; 
              server   192.168.10.100:8080 max_fails=2 fail_timeout=30s ;  
              server   192.168.10.101:8080 max_fails=2 fail_timeout=30s ;  
    }

  # 很重要的虚拟主机配置
    server {
        listen       80;
        server_name  itoatest.example.com;
        root   /apps/oaapp;

        charset utf-8;
        access_log  logs/host.access.log  main;

        #对 / 所有做负载均衡+反向代理
        location / {
            root   /apps/oaapp;
            index  index.jsp index.html index.htm;

            proxy_pass        http://backend;  
            proxy_redirect off;
            # 后端的Web服务器可以通过X-Forwarded-For获取用户真实IP
            proxy_set_header  Host  $host;
            proxy_set_header  X-Real-IP  $remote_addr;  
            proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;
            proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;

        }

        #静态文件，nginx自己处理，不去backend请求tomcat
        location  ~* /download/ {  
            root /apps/oa/fs;  

        }
        location ~ .*\.(gif|jpg|jpeg|bmp|png|ico|txt|js|css)$   
        {   
            root /apps/oaapp;   
            expires      7d; 
        }
        location /nginx_status {
            stub_status on;
            access_log off;
            allow 192.168.10.0/24;
            deny all;
        }

        location ~ ^/(WEB-INF)/ {   
            deny all;   
        }
        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }

  ## 其它虚拟主机，server 指令开始
}
```

最主要的是 server 的配置

## 指令说明

### main全局配置

nginx 在运行时与具体业务功能（比如 http 服务或者 email 服务代理）无关的一些参数，比如工作进程数，运行的身份等。

- `woker_processes 2`

  在配置文件的顶级*main*部分，worker 角色的工作进程的个数，master 进程是接收并分配请求给 worker 处理。

  **这个数值简单一点可以设置为 cpu 的核数**`grep ^processor /proc/cpuinfo | wc -l`，也是 auto 值，如果开启了ssl和 gzip 更应该设置成与逻辑 CPU 数量一样甚至为 2 倍，可以减少 I/O 操作。如果 nginx 服务器还有其它服务，可以考虑适当减少。

- `worker_cpu_affinity`

  也是写在*main*部分。在高并发情况下，通过设置 cpu 粘性来降低由于多 CPU 核切换造成的寄存器等现场重建带来的性能损耗。如`worker_cpu_affinity 0001 0010 0100 1000;` （四核）。

- `worker_connections 2048`

  **写在*events*部分**。每一个 worker 进程能并发处理（发起）的最大连接数（包含与客户端或后端被代理服务器间等所有连接数）。

  nginx **作为反向代理服务器**，计算公式 `最大连接数 = worker_processes * worker_connections/4`，所以这里客户端最大连接数是 1024，这个可以增到到 8192 都没关系，看情况而定，但不能超过后面的`worker_rlimit_nofile`。

  当 nginx 作为 http 服务器时，计算公式里面是除以 2。

- `worker_rlimit_nofile 10240`

  写在*main*部分。默认是没有设置，可以限制为操作系统最大的限制 65535。

- `use epoll`

  写在`events`部分。

  在 Linux 操作系统下，nginx 默认使用 epoll 事件模型，得益于此，nginx 在 Linux 操作系统下效率相当高。

  同时 Nginx 在 OpenBSD 或 FreeBSD 操作系统上采用类似于 epoll 的高效事件模型 kqueue。在操作系统不支持这些高效模型时才使用 select。

### http服务器

与提供 http 服务相关的一些配置参数（控制请求方式为 HTTP 的）。例如：是否使用 keepalive 啊，是否使用 gzip 进行压缩等。

- `sendfile on`

  开启高效文件传输模式，sendfile 指令指定 nginx 是否调用 sendfile 函数来输出文件，减少用户空间到内核空间的上下文切换。

  对于普通应用设为 on，如果用来进行下载等应用磁盘 IO 重负载应用，可设置为 off，以平衡磁盘与网络I/O处理速度，降低系统的负载。

- `keepalive_timeout 65` 

  长连接超时时间，单位是秒，**这个参数很敏感**，涉及浏览器的种类、后端服务器的超时设置、操作系统的设置，可以另外起一片文章了。

  长连接请求大量小文件的时候，可以减少重建连接的开销，但假如有大文件上传，65s 内没上传完成会导致失败。如果设置时间过长，用户又多，长时间保持连接会占用大量资源。

- `send_timeout` 

  用于指定响应客户端的超时时间。这个超时仅限于两个连接活动之间的时间，如果超过这个时间，客户端没有任何活动，Nginx 将会关闭连接。

- `client_max_body_size 10m`

  允许客户端请求的最大单文件字节数。如果有上传较大文件，请设置它的限制值

- `client_body_buffer_size 128k`

  缓冲区代理缓冲用户端请求的最大字节数

#### **模块 http_proxy：**

这个模块实现的是 nginx 作为**反向代理服务器的功能**，包括缓存功能（另见[文章](http://segmentfault.com/a/1190000002873747)）

- `proxy_connect_timeout 60`

  nginx 跟后端服务器连接超时时间(代理连接超时)

- `proxy_read_timeout 60`

  连接成功后，与后端服务器两个成功的响应操作之间超时时间(代理接收超时)

- `proxy_buffer_size 4k`

  设置代理服务器（nginx）从后端realserver读取并保存用户**头**信息的缓冲区大小，默认与proxy_buffers大小相同，其实可以将这个指令值设的小一点

- `proxy_buffers 4 32k`

  proxy_buffers 缓冲区，nginx 针对单个连接缓存来自后端 realserver 的**响应**，网页平均在32k以下的话，这样设置

- `proxy_busy_buffers_size 64k`

  高负荷下缓冲大小（proxy_buffers*2）

- `proxy_max_temp_file_size`

  当 proxy_buffers 放不下后端服务器的响应内容时，会将一部分保存到硬盘的临时文件中，这个值用来设置最大临时文件大小，默认1024M，它与 proxy_cache 没有关系。大于这个值，将从 upstream 服务器传回。设置为0禁用。

- `proxy_temp_file_write_size 64k`
  当缓存被代理的服务器响应到临时文件时，这个选项限制每次写临时文件的大小。`proxy_temp_path`（可以在编译的时候）指定写到哪那个目录。

proxy_pass，proxy_redirect见 location 部分。

#### **模块http_gzip：**

- gzip on 

  开启 gzip 压缩输出，减少网络传输。

  - `gzip_min_length 1k` ： 

    设置允许压缩的页面最小字节数，页面字节数从 header 头得 content-length 中进行获取。默认值是20。

    建议设置成大于 1k 的字节数，小于 1k 可能会越压越大。

  - `gzip_buffers 4 16k` ：

     设置系统获取几个单位的缓存用于存储gzip的压缩结果数据流。

    4 16k 代表以 16k 为单位，安装原始数据大小以16k为单位的4倍申请内存。

  - `gzip_http_version 1.0` ：

     用于识别 http 协议的版本，早期的浏览器不支持 Gzip 压缩，用户就会看到乱码，所以为了支持前期版本加上了这个选项，如果你用了 Nginx 的反向代理并期望也启用 Gzip 压缩的话，由于末端通信是 http/1.0，故请设置为 1.0。

  - `gzip_comp_level 6` ： 

    gzip 压缩比，1压缩比最小处理速度最快，9压缩比最大但处理速度最慢(传输快但比较消耗cpu)

  - `gzip_types` ：

    匹配 mime 类型进行压缩，无论是否指定,”text/html”类型总是会被压缩的。

  - `gzip_proxied any` ：

    Nginx 作为反向代理的时候启用，决定开启或者关闭后端服务器返回的结果是否压缩，匹配的前提是后端服务器必须要返回包含”Via”的 header 头。

  - `gzip_vary on` ：

    和 http 头有关系，会在响应头加个 Vary: Accept-Encoding ，可以让前端的缓存服务器缓存经过gzip压缩的页面，例如，用Squid缓存经过 Nginx 压缩的数据。

### server虚拟主机

http服务上支持若干虚拟主机。每个虚拟主机一个对应的server配置项，配置项里面包含该虚拟主机相关的配置。在提供mail服务的代理时，也可以建立若干server。每个server通过监听地址或端口来区分。

- `listen`
  监听端口，默认80，小于1024的要以root启动。可以为`listen *:80`、`listen 127.0.0.1:80`等形式。
- `server_name`
  服务器名，如`localhost`、`www.example.com`，可以通过正则匹配。

#### **模块http_stream**

这个模块通过一个简单的调度算法来实现客户端 IP 到后端服务器的负载均衡，`upstream`后接负载均衡器的名字，后端realserver以 `host:port options;` 方式组织在 {} 中。

如果后端被代理的只有一台，也可以直接写在 proxy_pass 。

###  location

http 服务中，某些特定的 URL 对应的一系列配置项。

- `root /var/www/html`

  **定义服务器的默认网站根目录位置**。如果`location`URL 匹配的是子目录或文件，`root`没什么作用，一般放在`server`指令里面或`/`下。

- `index index.jsp index.html index.htm`

  定义路径下默认访问的文件名，一般跟着`root`放

- `proxy_pass http:/backend`

  **请求转向 backend 定义的服务器列表，即反向代理**，对应 `upstream` 负载均衡器。
  
  也可以 `proxy_pass http://ip:port`。

- `proxy_redirect off;`
  `proxy_set_header Host $host;`
  `proxy_set_header X-Real-IP $remote_addr;`
  `proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;`

  这四个暂且这样设，如果深究的话，每一个都涉及到很复杂的内容，也将通过另一篇文章来解读。

关于 location 匹配规则的写法，可以说尤为关键且基础的，参考文章 [nginx配置location总结及rewrite规则写法](http://segmentfault.com/a/1190000002797606);

## 其他

关于 Win 下使用的几个命令：

`start nginx`		// 开启

`nginx -s stop`	// 停止

`nginx -s reload`  // 重新加载配置

`nginx -t`  // 检查配置文件是否正确，以及可以指定文件，还有例如 `nginx -t -c /usr/local/nginx/conf/nginx.conf`

## 反向代理配置模板

建议是分文件夹独立存放，例如新建一个 App 文件夹，在主配置文件中（nginx.conf）导入：

```
...
http {
	include App/*.conf;
}
...
```

然后是具体的一个例子：

```
server {
	listen 80;
	autoindex on;
	server_name app.bfchengnuo.com;
	access_log c:/access.log combined;
	index index.html index.htm index.jsp index.php; 
	#error_page 404 /404.html;
	if ( $query_string ~* ".*[\;'\<\>].*" ){
		return 404;
	} 
	location /proxy/ { 
		proxy_pass http://127.0.0.1:10021/;
		add_header Access-Control-Allow-Origin *;
	}
	
	location / { 
		root D:\Web\app;
		add_header Access-Control-Allow-Origin *;
	}
}
```

