#  FFmpeg基础使用

FFmpeg 的名称来自 MPEG 视频编码标准，前面的“FF”代表 “Fast Forward”，FFmpeg 是一套可以用来记录、转换数字音频、视频，并能将其转化为流的开源计算机程序。可以轻易地实现多种视频格式之间的相互转换。包括如下几个部分：

- libavformat：用于各种音视频封装格式的生成和解析，包括获取解码所需信息以生成解码上下文结构和读取音视频帧等功能，包含 demuxers 和 muxer 库。
- libavcodec：用于各种类型声音/图像编解码。
- libavutil：包含一些公共的工具函数。
- libswscale：用于视频场景比例缩放、色彩映射转换。
- libpostproc：用于后期效果处理。
- **ffmpeg：是一个命令行工具，用来对视频文件转换格式，也支持对电视卡实时编码。**
- ffsever：是一个 HTTP 多媒体实时广播流服务器，支持时光平移。
- **ffplay：是一个简单的播放器，使用 ffmpeg 库解析和解码，通过 SDL 显示。**
- **ffprobe：收集多媒体文件或流的信息，并以人和机器可读的方式输出。**

使用之前，要确保对基本的视频格式与原理有一定的了解。

## ffmpeg使用

使用 ffmpeg 快速的进行音视频转换。使用方法：

>  ffmpeg [全局选项] {[输入文件选项] -i ‘输入文件’} ... {[输出文件选项] ‘输出文件’} 

常用命令：

``` shell
# 查看音视频信息
ffmpeg -i video.mp4
ffmpeg -i video.mp4 -hide_banner

# 分离视频音频流，an 禁止音频录制，vn 禁止视频录制，指定格式 -f mp3
ffmpeg -i input_file -vcodec copy -an output_file_video　　# 分离视频流
ffmpeg -i input_file -acodec copy -vn output_file_audio　　# 分离音频流

# 视频格式转换，维持原视频质量增加 -qscale 0
ffmpeg -i video.mp4 video.avi
ffmpeg -i input.webm -qscale 0 output.mp4
ffmpeg -formats # 查看支持的格式

# 修改视频分辨率
ffmpeg -i input.mp4 -s 1280x720 -c:a copy output.mp4
# 压缩视频，酌情考虑 -crf 的值
ffmpeg -i input.mp4 -vf scale=1280:-1 -c:v libx264 -preset veryslow -crf 24 output.mp4

# 视频剪切
ffmpeg –i test.avi –r 1 –f image2 image-%3d.jpeg  # 提取图片，-r 提取图像的频率
# 剪切视频，-ss 开始时间，-t 持续时间
ffmpeg -ss 0:1:30 -t 0:0:20 -i input.avi -vcodec copy -acodec copy output.avi

# 拆分视频
ffmpeg -i input.mp4 -t 00:00:30 -c copy part1.mp4 -ss 00:00:30 -codec copy part2.mp4
# 合并视频，出现错误使用 -safe 0 选项
ffmpeg -f concat -i join.txt -c copy output.mp4
# txt file
# file /home/sk/myvideos/part1.mp4
# file /home/sk/myvideos/part2.mp4
# file /home/sk/myvideos/part3.mp4
```

## 参考

https://zhuanlan.zhihu.com/p/67878761