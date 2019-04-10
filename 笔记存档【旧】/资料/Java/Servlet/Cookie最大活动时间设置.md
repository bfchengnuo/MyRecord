指定 Cookie的有效时间，以**秒为单位**，MaxAge 可以有三种方式设置:

1. 正数：有效时间
  从当前时间开始向后推移，并且把信息写到硬盘中，超出时间表示 Cookie失效，即 Cookie过期后，key,value,domin,age,path等
相关值就得不到了，但 Cookie保存在硬盘中不会销毁

2. 负数：当前 Cookie采用的是会话 Cookie，信息只是保存在浏览器缓存中，但片段不会写到硬盘中，HttpSesesion没有负数

3. 0：销毁 Cookie，要立刻销毁当前应用域在客户本地硬盘中创建的 Cookie文件