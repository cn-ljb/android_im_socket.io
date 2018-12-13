# android im socket.io

## 项目简介

> 基于[socket.io](https://github.com/socketio/socket.io "socket.io")实现的Android IM 实时通讯开源项目
>
> 服务端代码见 -> [service demo](https://github.com/cn-ljb/service_im_socket.io "service_im_socket.io")

## 架构

kotlin + [mvp](https://github.com/cn-ljb/mvp-kotlin "mvp-kotlin") + [socket.io](https://github.com/socketio/socket.io "socket.io") + [okhttp](https://github.com/square/okhttp "okhttp") + [retrofit](https://github.com/square/retrofit "retrofit") + [rxjava](https://github.com/ReactiveX/RxJava "rxjava") + [glide](https://github.com/bumptech/glide "glide") + [eventbus](https://github.com/greenrobot/EventBus "eventbus")

### 导入项目

修改 Constant.SOCKET_HOST IP 对应 Service IP

		  const val SOCKET_HOST = "http://172.16.201.33:9090"

### 通讯机制

* 绿色：主动发送消息
* 红色：被动接收消息

![](https://i.imgur.com/tsmqL30.png)


## 效果图

![](https://i.imgur.com/PE26eIp.png)


## 版本 

### v1.0.0

* 支持文字、图片、语音消息
* 支持emoji表情
* 支持自定义消息
* 支持离线消息

