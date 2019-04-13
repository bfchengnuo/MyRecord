# Elasticsearch入门

下载和安装不说了，提一下，启动的时候可能会出现无法分配内存的情况，这是因为 elasticsearch 5.0 默认分配 jvm 空间大小为 2g，可以手动修改下配置，编辑 `config/jvm.options` 修改为 `-Xms512m` 和 `-Xmx512m` 就好。

中文文档在此：https://es.xiaoleilu.com/010_Intro/00_README.html

为了方便，可能需要用到 elasticsearch-head 插件或者官方提供的 kibana。

> elasticsearch-head 中，粗的表示为主，细线边框为副本，一对用相同的数字标识

启动直接命令行执行 `elasticsearch.bat` 就行，后台启动使用 `-d`

## 基本概念

ES 中主要的就三类：

- 文档
  最小的数据单元，内含多个字段 ，类似于数据库中的一行数据
- 索引
  相同属性的文档集合，相当于数据库的 Database
- 类型
  索引可以定义一个或多个类型，文档必须属于一个类型，相当于数据库中的 table

分布式中，必然还少不了分片和备份，类似 kafka 的概念了，一个索引可以分片成多个块，存放在不同的节点，这样既可以提高吞吐量，也可以方便的扩展，降低单节点的压力；备份就不多说了，分布在其他的节点上。

另外 ES 的特性还有：近实时，也就是秒级；分布式架构，方便扩展，简单说就是对 Lucene 的一个封装。

## 集群

配置集群在 `elasticsearch.yml` 配置文件中加入：

``` yaml
# 主节点配置
cluster.name: bing
node.name: master
node.master: true
network.host: 127.0.0.1

# 从节点配置
cluster.name: bing
node.name: slave1
network.host: 127.0.0.1
http.port: 8200 # 默认 9200
discovery.zen.ping.unicast.hosts: ["127.0.0.1"]
```

为了方便查看，可使用插件 [elasticsearch-head](https://github.com/mobz/elasticsearch-head) 进行可视化管理，当然也配有 Chrome 插件。

## 创建索引

请求基本格式 `http://<ip>:<port>/<索引>/<类型>/<文档id>`

常用 HTTP 动词 GET/PUT/POST/DELETE；

以上可以看出 ES 提供的 RESTful 风格的 API。

创建索引的时候需要指定分片数和副本数（有默认值）。

``` javascript
// 创建索引 (put)：http://127.0.0.1:9200/people
{
  "settings":{
    // 分片数和备份数
    "number_of_shards":3,
    "number_of_replicas":1
  },
  // 标识是否是结构化索引；类似定义字段
  "mappings":{
    "man":{
      "properties":{
        "name":{
          "type":"text"
        },
        "country":{
          "type":"keyword"
        },
        "age":{
          "type":"integer"
        },
        "date":{
          "type":"date",
          "format":"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        }
      }
    },
    "woman": {}
  }
}
```

在 mappings 中定义了这个索引的类型，以及此类型拥有什么属性，类比下数据库。

## 插入

向索引中插入数据，可分为两类：

- 指定文档 id 插入
  使用 **put 请求**： `ip:port/索引/类型/id`
- 自动产生文档 id 插入
  使用 **Post 请求**：`ip:port/索引/类型`
  这个时候，es 会帮我们自己创建出一个随机的文档 id，并且会返回给你

无论那种方式 body 内的 json 数据按照之前 mappings 定义的属性来写，就是普通的 JSON 对象了。

## 更新

可使用 post 请求，格式：`ip:port/索引/类型/id/_update`

通过 doc 属性指定要修改的内容的 key 和修改后的数据，只会更新你设置的内容

``` javascript
// 修改数据 (post)：http://127.0.0.1:9200/people/man/1/_update
{
  "doc": {
    "name": "修改后的值"
  }
}
```

脚本方式修改：

``` javascript
{
  "script": {
    "lang": "painless", // es 内置脚本语言
    "inline": "ctx._source.age += 10", // ctx 表示上下文, 可使用 params.age 引用下面定义的值
    "params": {
      "age": 10
    }
  }
}
```

两种方案请求格式都是一样的，区别就是内容不同，一个是 doc 属性一个是 script 属性。

相比执行脚本方式的定制性更高，也更复杂。

## 删除

删除也分为两种：

- 删除文档（数据）
  使用 delete 请求方式，不需要参数，指定 id：`ip:port/索引/类型/id`
- 删除索引
  数据也会被删除，delete 请求方式：`ip:port/索引` ，无需参数。

## 查询

和数据库类似，查询应该是最复杂的了，花样也是最多的，一类一类的来说

### 简单查询

使用 get 方式，格式：`ip:port/索引/类型/id` ， 不需要请求体，但是需要指定具体 id

### 条件查询

使用 post 方式，格式为：`ip:port/索引/_search`

请求体格式：

``` javascript
{
  "query": {
    // "match_all": {} // 全部数据
    "match": {
      "title": "xxxx"
    }
  },
  "sort":[
    {"date": {"order":"desc"}}
  ],
  "from": 1, // 跳过开始的结果数，默认0
  "size": 1 // 结果数，默认10
}
```

最重要的是 query 这个属性，可以使用 match_all 查询全部，也可以 match 来指定范围。

返回的结果也是 json 格式的，如果不设置分页参数默认返回 10 条记录，其中有个 took 属性是表示花费的时间，默认毫秒，还有很多其他的属性。

### 聚合查询

也可以使用 post 请求哦，格式和上面条件查询是一致，数据格式类似：

``` javascript
{
  "aggs": {
    // 名字是自定义的
    "group_by_xxx": {
      "terms": {
        "field": "xxx" // 要聚合的字段
      }
    }, // 可以再加，支持多个聚合条件
    "group_by_yyy": {
      // 聚合计算，会返回最大值、最小值、平均值、合计等
      // 也可以单独指定，比如把 stats 设置成 min、max
      "stats": {
        "field": "xxx"
      }
    }
  }
}
```

和 SQL 中的 group by 是不是很像呢。

## 高级查询 QueryContext

相比上面就显得复杂一些了，也是分为好多种，一种一种来看，ES 会计算一个 `_score` 来标识匹配的程度，旨在判断目标文档与查询条件的匹配程度。

最基本的，

- **全文本查询**：针对文本类型数据；
  最简单的用法使用 match 关键字
- **字段级别查询**：针对结构化数据，如数字、日期等；
  最简单的用法：使用 term 代替 query 里的属性

### 全文本查询

先来说全文本的查询，它和条件查询很相似，所以内容参考条件查询，只不过是在条件查询的基础上加一些属性：

``` javascript
"query": {
  // 全匹配
  "match_phrase": {
    "title": "ElasticSearch入门"
  }
}

// 普通模糊查询的关键词：match
"query": {
  // 会拆分为 ElasticSearch、入门 两个进行匹配
  "match": {
    "title": "ElasticSearch入门"
  }
}
```

以下几种查询只给个例子，看例子基本能看懂了，不懂可查官方文档

#### 多字段模糊查询

``` javascript
{
  "query":{
    "multi_match":{
      "query":"loli",
      "fields":["title","autor"]
    }
  }
}
```

#### query_string 语法查询

支持通配符、正则等，AND  OR

``` javascript
{
  "query": {
    "query_string": {
      "query": "(ElasticSearch AND 哈哈哈) OR 入门",
      // 还可以指定 fields 等
      "fields":["title","autor"]
    }
  }
}
```

### 字段级别查询

适用于结构化的数据查询，最简单的例子：

``` javascript
{
  "query": {
    "term": {
      "title": "ElasticSearch入门"
    }
  }
}
```

#### 范围查询

``` javascript
{
  "query":{
    "range":{
      "count":{
        "gte":1000, // >= 去掉 e 就是 > ；也可用于日期上，可使用 now 关键词
        "lte":"2000" // <=
      }
    }
  }
}
```

### Filter Context 数据过滤查询

文档是否满足条件，只有 yes OR no

必须配合 bool 使用，es 会对结果进行缓存，比 query 快一点，主要用来做数据过滤的。

``` javascript
{
  "query":{
    "bool":{
      "filter":{
        "term":{
          "count":1000 // 只查询字数为1000的
        }
      }
    }
  }
}
```

### 复合查询

固定分数查询；布尔查询；等等

``` javascript
{
  "query": {
    // 固定分数,不支持 match 支持 filter
    "constant_score": {
      "filter": {
        "match": {
          "title": "ElasticSearch入门"
        }
      },
      "boost": 2
    }
  }
}

// ---------------------

{
  "query": {
    // 布尔查询,不支持 match 支持 filter
    "constant_score": {
      "bool": {
        // should 为或的关系；must 为必须（多个条件下）
        // must_not
        "should": [{
          // 可以使用 term
          "match": {
            "title": "ElasticSearch入门"
          } // 可以多个字段
        }],
        // 可以配合 filter
        "filter": [{"term": {"size": 100}}]
      }
    }
  }
}
```

所谓固定分数，就是根据那个匹配度的分数来的

SB 整合示例：https://github.com/rstyro/Springboot/tree/master/springboot-elasticsearch