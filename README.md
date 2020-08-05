<div align="center">
   <img width="160" src="docs/mirai.png" alt="logo"></br>

   <img width="95" src="docs/mirai.svg" alt="title">

----

[![Gitter](https://badges.gitter.im/mamoe/mirai.svg)](https://gitter.im/mamoe/mirai?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
![Gradle CI](https://github.com/mamoe/mirai/workflows/Gradle%20CI/badge.svg?branch=master)
[![Download](https://api.bintray.com/packages/him188moe/mirai/mirai-core/images/download.svg)](https://bintray.com/him188moe/mirai/mirai-core/)  

Mirai 是一个在全平台下运行，提供 QQ Android 和 TIM PC 协议支持的高效率机器人库

这个项目的名字来源于
     <p><a href = "http://www.kyotoanimation.co.jp/">京都动画</a>作品<a href = "https://zh.moegirl.org/zh-hans/%E5%A2%83%E7%95%8C%E7%9A%84%E5%BD%BC%E6%96%B9">《境界的彼方》</a>的<a href = "https://zh.moegirl.org/zh-hans/%E6%A0%97%E5%B1%B1%E6%9C%AA%E6%9D%A5">栗山未来(Kuriyama <b>Mirai</b>)</a></p>
     <p><a href = "https://www.crypton.co.jp/">CRYPTON</a>以<a href = "https://www.crypton.co.jp/miku_eng">初音未来</a>为代表的创作与活动<a href = "https://magicalmirai.com/2019/index_en.html">(Magical <b>Mirai</b>)</a></p>
图标以及形象由画师<a href = "https://github.com/DazeCake">DazeCake</a>绘制
</div>


## BiliLinkerRevise

BiliLinkerRevise是一个基于Mirai框架的插件

它基于MoRain(墨雨橙)的 **[BiliBiliLinker](https://github.com/Karlatemp/MoRain/tree/master/BiliBiliLinker/)** 进行二次开发的插件
<br>你也可以在这里找到它的源代码

## **一切开发旨在学习，请勿用于非法用途**

**[Mirai](https://github.com/mamoe/mirai)** 一个在全平台下运行，提供 QQ Android 和 TIM PC 协议支持的高效率机器人库

你可以在这里得到更多关于 Mirai 的信息，以及获取它的源代码

## 插件介绍

一个在QQ群中辅助使用BiliBili的插件，可以解析来自b站的视频信息

## 使用方法

在任意机器人所在的QQ群中发送以下内容

- 来自b站的视频连接
- 以**av**开头的视频编号(aid)
- 以**BV**开头的视频编号(bvid)

## 涉及指令

无

## 注意事项

现在尚未对视频编号的解析进行模糊匹配相关功能的完善

所以发送视频的编号(及av和BV号)时请注意大小写，还有请不要带上多余的空格

否则机器人可能会因为匹配不到而不会对其作出任何的反应

### 错误示范

- AV1234567
- av 1234567
- bvA1B2C3D
- BV A1B2C3D

## 安装教程

下载 `资源/BiliLinkerRevise.jar` 

将其放入到您服务器的 `Mirai\plugins` 目录下

最后请重启您的机器人并登陆bot

当然，您也可以选择自行克隆源码编译jar，然后按照上面的方法安装

不过我通常并不建议你这么做

## 使用许可

遵循Mirai源项目 采用AGPL v3开源协议进行许可

您可以在此基础上对此进行任意的二次开发封装打包

但是请务必保留原作者以及我对于此项目的署名

仅供个人学习交流使用，切勿用作于违法以及商业用途

## 本插件进行了哪些修改

- 新增对aid以及bvid的解析
- 优化了对于返回内容的排版（至少原来的我感觉不太好看）
- 禁用返回内容的富文本内容（卡片），减少冻结封号的概率

## 免责声明

我不对于他人对此项目作出的任何行为承担任何的责任

本文开头的内容来自于Mirai源项目的README.md中

如果原作者 `MoRain` 认为本项目对其原项目产生侵权行为，请及时联系我删除本项目

最后，请容许我保留对此(包括但不限于)的最终解释权利