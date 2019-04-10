---
title: 关于onTouch和onClick
date: 2016-09-23 22:47:11
tags: [Android,事件分发]
categories: Android
---

Android组件中的onTouch，onClick，onLongClick事件是有联系的，也可以说是会产生一定影响的，今天看到个按到按钮持续执行一个方法的需求，这样click事件是没法做到了，顺带着把这些相似的事件了解下~
<!-- more -->
本文转载自医生的博文：http://blog.csdn.net/eclipsexys/article/details/8785149

配合<u>[Android事件分发机制](http://bfchengnuo.info/2016/09/03/Android%E4%B8%AD%E7%9A%84%E4%BA%8B%E4%BB%B6%E5%88%86%E5%8F%91%E6%9C%BA%E5%88%B6/)</u>食用，效果更佳。

## 概况

- onTouch返回false
  首先是onTouch事件的down事件发生，此时，如果长按，触发onLongClick事件；
  然后是onTouch事件的up事件发生，**up完毕，最后触发onClick事件。**

- onTouch返回true
  首先是onTouch事件的down事件发生，然后是onTouch事件的up事件发生；**期间不触发onClick和onLongClick事件**

- onTouch：down返回true，up返回false：结果同上。

- onTouch：down返回false，up返回true
  首先是onTouch事件的down事件发生，此时：
  长按，触发onLongClick事件，然后是onTouch事件的up事件发生，完毕。
  短按，先触发onTouch的up事件， 到一定时间后，自动触发onLongClick事件。

## 机制分析

***对于前三种来说：***
onTouch事件中：**down事件返回值标记此次事件是否为点击事件**（返回false，是点击事件；返回true，不记为点击事件），**而up事件标记此次事件结束时间，也就是判断是否为长按。**
只要当down返回true时候，系统将不把本次事件记录为点击事件，也就不会触发onClick或者onLongClick事件了。因此尽管当up的时候返回false，系统也不会继续触发onClick事件了。
***对于最后一种来说：***
当down返回false，标记此次事件为点击事件，而up返回了true，则表示此次事件一直没有结束，也就是一直长按下去了，达到长按临界时间后，自然触发长按事件，而onClick事件没有触发到

## View的响应

首先，该View会先响应ACTION_DOWN事件，并返回一个boolean值，这里有两种判断：

1. 返回True，表示该View接受此按下动作，就是说这个点击动作的按下操作被中止，然后就是响应ACTION_UP事件。点击动作的按下操作被ACTION_DOWN接受之后就结束了，所以之后的OnClick/OnLongClick事件就不会响应了。

2. 返回false，表示该View不接受此按下动作，响应完之后，按下操作继续往下发，之后是响应ACTION_UP事件，这里又有一个判断：
   如果ACTION_UP事件返回True，表示ACTION_UP接受松开操作，松开操作中止；View会一直处于按下状态，之后View便会响应OnLongClick事件。
   如果ACTION_UP事件返回false，表示ACTION_UP不接收松开操作，松开操作继续下发；因为按下与松开操作都没有被中止，所以之后View就会响应OnClick事件。

**多个View之间的事件响应：**
如果有多层View，一个layout布局，在此布局上放一个TextView，并将TextView的宽高设置为match_parent（确保点击的是在TextVIew上）。给Activity与TextView都设置OnTouchListener事件。**事件响应的顺序是先从顶层的View开始的**。所以，当点击屏幕时（TextView所在区域的屏幕）。
TextView响应ACTION_DOWN，返回false，ACTION_DOWN继续下发，Activity响应ACTION_DOWN事件。如果返回true，表示ACTION_DOWN到TextView就被中止了，而不会继续往下被Activity捕获了。
ACTION_UP的响应方式有点不同，响应ACTION_UP的有两种条件，必须满足其一：

- 是最下面一层（此处是Activity）。
- 其对应的ACTION_DOWN是终止点，即其ACTION_DOWN的返回值为true；