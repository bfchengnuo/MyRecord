使用 iView 框架开发，用到了 Model 对话框，因为是在对话框里做表单校验和提交，所以很多情况是需要提示用户修改数据重新提交的。

然而坑爹的是 Model 自带的 loading 状态只能阻止一次关闭，第二次点，不管你怎么弄对话框都会关闭。

官网的例子非常简单，看不出任何套路。

我尝试了各种方法，定义 change 事件，各种标志位，loading 状态的重置，最终无法完美解决，要么还是关闭，要么就关闭不了，还没有了 loading 提示，纠结。。。

最后在 Google 一搜，在 Github 的 issues 中找到了作者给出的方案，虽然很无语，大概是设计问题，有好多踩坑的同学表示无法理解。

> see  https://github.com/iview/iview/issues/597

虽然按照作者的方案解决了，但是“踩”的人很多，果然是设计有点问题的。

下面是作者的答案：

```html
<template>
  <div>
    <Button type="primary" @click="modal1 = true">显示对话框</Button>
    <Modal
           v-model="modal1"
           title="普通的Modal对话框标题"
           @on-ok="ok"
           :loading="loading">
      <p>对话框内容</p>
      <p>对话框内容</p>
      <p>对话框内容</p>
    </Modal>
  </div>
</template>
<script>
  export default {
    data () {
      return {
        modal1: false,
        loading: true
      }
    },
    methods: {
      ok () {
        this.$Message.info('异步验证数据');
        setTimeout(() => {
          this.loading = false;
          this.$nextTick(() => {
            // 恢复初始状态，要不然下一次没 loading 提示，还会自动关闭
            this.loading = true;
          });
        }, 2000);
      }
    }
  }
</script>
```

总结一下，关键的代码是：

```javascript
this.loading = false;
this.$nextTick(() => {
  // 恢复初始状态，要不然下一次没 loading 提示，还会自动关闭
  this.loading = true;
});
```

把这两句放到校验失败的地方就可以了。

:)
