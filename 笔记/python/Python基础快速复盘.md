# Python基础快速复盘

几年前，详细的把 py 的教程看了好几遍，也写过几个爬虫，甚至让我印象深刻的把 self 多继承都搞明白了，然而现在已经差不多两年不看了，真的基本全忘了。

让我再看一遍笔记，我都看不下去了，毕竟不是急着用，这里简略的进行抽取，方便复习。

## 数据类型

计算数字的幂使用：`**`，长数字可以使用下划线分割。

二进制：0b

八进制：0o

十六进制：0x

字符串格式化：

``` python
a = '字符串：%3.5'%('bbbbb')

# %f = float
# %d = 数字，小数部分直接舍去

f = f'格式化字符串{a}'

# 字符串的重复
a = a * 10

a = None
```

使用 % 格式化时，如果写了最小长度，那么不够会在前面补空格。

常见函数和方法（方法属于对象）：

``` python
int()
float()
str()
bool()

len()
max()
min()

list('abcd')
# 生成自然数的序列，开始，结束，步长，除了结束，其他都可省略
range(0，5，1)

# 集合方法
s.index()
s.count()
s.append()
s.insert(1, 'x')
s.extend([11,22])
s.clear()
s.pop(index) # 默认最后一个
s.remove()
s.reverse()
s.sort() # 可选：reverse = True
s.copy()
```

容器相关操作：

``` python
## 列表
list = [1,2,3,4]

list = list + [5]
list = list * 2

print(1 in list)
print(1 not in list)

del list[0]

## 切片：创建一个子集合

# 包含 0 不包含 3，步长默认 1
ll = list[0:3]
ll = list[:3]
ll = list[0:]
ll = list[0:3:2]

print(ll[::2])

list[0:2] = [11,22] # 修改
list[0:0] = [11] # 插入

for x in ll:
  print(x)

## 元组（不可变的序列）
m_tuple = (1,2,3)
m_tuple = 1,2,3
m_tuple = 1,
# 解包, * 只能有一个
a,b,c = m_tuple
a,b,*c = m_tuple
*a,b,c = m_tuple

# 交换两个变量
a,b = b,a

## 字典
map = {k:v,k2:v2}
map = dict(name=n,age=2)
map = dict([('name','nn'), ('age', 12)])
# 存在，返回存在的值，不存在添加
map.setdefault('name', 'vvv')
# keys()  values()  items()

## 集合，不可变，无序（自然顺序），非重复，只能存储不可变对象
m_set = {1,1,2,3,4}
m_set = set([1,2,3,3])

## 基本运算
rst = s1 & s2
rst = s1 | s2
rst = s1 - s2
rst = s1 ^ s2
# 判断是否子集关系，类似的有 <、>=、>
rst = s1 <= s2
```

需要关注的是，index 可以是负数，步长也可以是负数（倒着）

字符串也是一种序列，还说不可变序列，可以进行切片等操作。

> 关于对比判断，== 或者 != 比较的是值，而 is 、not is 比较的是引用地址，也就是内存地址。

## 函数

函数也是一个对象，注意与方法进行区别。

``` python
def fun(str, tt):
  print('aaa', str)
  

# 可混合使用，关键字参数必须做后面
fun(tt='abc', str='str')

```

