# README

SteamCMD 是 Valve 提供的 Steam 的命令行版本，用于在 Linux 搭建各个游戏的 Dedicated Server。

## 安装SteamCMD

首先还是要解决依赖问题啊，然后再安装：

```shell
# 32bit RedHat系Linux
yum install glibc libstdc++
# 64bit RedHat系Linux
yum install glibc.i686 libstdc++.i686

mkdir ~/steamcmd
cd ~/steamcmd
wget https://steamcdn-a.akamaihd.net/client/installer/steamcmd_linux.tar.gz
tar -zxvf steamcmd_linux.tar.gz
```

配置 SteamCMD

```shell
cd ~/steamcmd
./steamcmd.sh

# 匿名登陆
Steam>login anonymous
# 指定下载目录
Steam>force_install_dir ~/l4d2
# 下载
Steam>app_update 222860 validate
quit
```

有些游戏的Server需要你有游戏才可以下载，有些匿名登录可以下，具体表格请参见:[https://developer.valvesoftware.com/wiki/Dedicated_Servers_List](https://developer.valvesoftware.com/wiki/Dedicated_Servers_List)

## 配置L4D2

在 `l4d2/left4dead2/cfg/` 目录下新建一个 **server.cfg** 具体说明是：

```
hostname "servername"    //游戏服务器名
rcon_password "password" //远程管理密码
//sv_search_key yourkey  //搜索此服务器的关键词
//sv_region 255          //服务器地区，255表示全球
//sv_gametypes "coop,versus,survival,scavenge" //游戏模式
//map c5m1_waterfront    //游戏地图
//sv_voiceenable 1       //开启语音服务
//sv_lan 0				 //是否是局域网游戏
//sv_cheats "0"			 //是否允许作弊

//sv_steamgroup "01234"  //Steam组号
//sv_steamgroup_exclusive 1 //将服务器设为Steam组私有
```

我认为只要设置前两个就行，后两个根据情况加，因为地图啥的可以手动在游戏里选

---

l4d2 文件夹里有个 motd.txt 的记事本文件，可以放一个 html 连接或者图片连接是欢迎页
还有个 host.txt，是右上角 logo 图标

## 运行

记得用 scree 运行

```shell
cd ~/l4d2
./srcds_run -game left4dead2 +exec server.cfg
```

把它放在脚本里也许会好点....

端口默认是 27015；记得放行

## END

或许可以试试 LinuxGSM 这个工具一键搭建：https://gameservermanagers.com/#supportedgames

L4D2 服务器虽然占用 cpu 和内存不多，但是硬盘需要 8G+ ；额，需要下载好长时间呢