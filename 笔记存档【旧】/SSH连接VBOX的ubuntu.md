# 用SSH连接VBOX里的ubuntu


----------
1. 网络
    - VBOX
    
    设置第一块网卡为`网络地址转换NAT`用于使用本机网络让虚拟机联网
    
    设置第二块网卡为桥连模式，如果windows10需要VBOX5.X的版本才可以
    
    主机ping虚拟机测试是否ping通，虚拟机ping主机测试是否ping通
    
2. ssh服务

    - 查看是否安装ssh服务

        在ubuntu终端命令界面键入：`ssh localhost`
        
        如果出现下面提示则表示还没有安装ssh服务
        
        ssh: connect to hostlocalhost port 22: Connection refused 

    - 如果通过上面步骤查看没有安装sshserver、则键入命令安装
    
        ~~`sudo apt-getinstall –y openssh-server`~~
        
        ~~`sudo apt-get install openssh-server`~~
        
        `sudo apt-get install ssh`

    - 安装完成后启动ssh `service ssh start` 

    - 启动完成之后可以使用命令：
    
   		 ~~`ps –e | grep ssh`~~
    
  		  `netstat -tlp`  
    
   		 来查看ssh状态.
    
	       ~~6455 ?        00:00:00 sshd~~
	       
	        tcp        0      0 *:ssh      *:*    LISTEN 
	        
	    	则表明启动成功。

3. 连接linux

    用SSH工具输入IP连接即可
    
    查看自己的IP
    
    linux：ifconfig
    
    windows：ipconfig
