## Windos

在 nginx.exe 目录，打开命令行工具(地址栏输入 cmd 回车)

`start nginx` : 启动 nginx

`nginx -s reload` ： 修改配置后重新加载生效

`nginx -s reopen` ：重新打开日志文件

`nginx -t -c /path/to/nginx.conf` or `nginx -t` ： 测试 nginx 配置文件是否正确

关闭 nginx：

`nginx -s stop` : 快速停止 nginx

`nginx -s quit` ： 完整有序的停止 nginx

## Linux

nginx 平滑重启：`kill -HUP pid`

意思为：执行后不再接受新的请求，但会把正在处理的工作做完后再关闭。

TODO

## 负载均衡

常见的 Nginx 负载均衡有：轮询、权重、ip 哈希、uri 哈希（第三方）、fair（第三方）

如果不设置，默认权重 weight=1 也就是机率都是相等的。

```
upstream mall.bfchengnuo.com{
	server 127.0.0.1:8080;
	server 127.0.0.1:9080;
}

server {
	listen 80;
	autoindex on;
	server_name mall.bfchengnuo.com;
	access_log c:/access.log combined;
	index index.html index.htm index.jsp index.php; 
	#error_page 404 /404.html;
	if ( $query_string ~* ".*[\;'\<\>].*" ){
		return 404;
	} 
	location / { 
		proxy_pass http://mall.bfchengnuo.com; 
		add_header Access-Control-Allow-Origin *;
	}
}
```

ip 哈希方式：

```
upstream mall.bfchengnuo.com{
  ip_hash;
	server 127.0.0.1:8080 down; # down 表示当前服务器不参加负载
	server 127.0.0.1:9080 weight=2;
	server 127.0.0.1:1080 backup; # 其他非 backup 机器忙时请求此服务器，一般不用
}
```

uri 哈希和 fair ：

```
upstream mall.bfchengnuo.com{
	server 127.0.0.1:8080;
	server 127.0.0.1:9080;
	hash $request_uri;
}

upstream mall.bfchengnuo.com{
	server 127.0.0.1:8080;
	server 127.0.0.1:9080;
	fair;
}
```

