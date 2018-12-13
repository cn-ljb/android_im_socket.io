package com.ljb.socket.android.socket

import android.content.Context
import android.util.Log
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.socket.listener.*
import com.ljb.socket.android.utils.RxUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Author:Ljb
 * Time:2018/9/5
 * There is a lot of misery in life
 **/
object SocketClient {

    @JvmStatic
    private var mSocket: Socket? = null
    //"uid=$uid&token=$token&device=1"
    @JvmStatic
    private var mToken: String? = null

    @JvmStatic
    private var isLinked = false

    private var mConnectListener: Emitter.Listener? = null

    private var mDisConnectListener: Emitter.Listener? = null

    private var mConnectErrorListener: Emitter.Listener? = null

    private var mConnectTimeOutListener: Emitter.Listener? = null

    private var mDataErrorListener: Emitter.Listener? = null

    private var mLoginConflictListener: Emitter.Listener? = null

    private var mChatMessageListener: Emitter.Listener? = null


    /**
     * 根据Token初始化一个Socket
     * */
    fun init(context: Context, token: String) {
        //是否复用Socket
        if (mSocket != null) {
            //用户信息发生改变，重新创建Socket对象，并销毁之前的Socket对象
            if (mToken != token) {
                newSocket(context, token)
            } else {
                Log.i(SocketService.TAG, "socket 复用")
            }
        } else {
            newSocket(context, token)
        }
    }


    /***
     * 断开连接
     */
    fun close() {
        mSocket?.let {
            Log.i(SocketService.TAG, "socket 链接已关闭")
            it.disconnect()
        }

    }

    /**
     * 开启连接，调用前请先初始化 @init()
     * */
    fun open() {
        mSocket?.connect()
    }

    /**
     * 销毁当前Socket
     * */
    fun release() {
        mSocket?.let {
            close()
            removeSocketListener()
            mSocket = null
            Log.i(SocketService.TAG, "socket 已释放")
        }
        RxUtils.dispose(reNewSocketSubscription)
    }


    /**
     * 发送Ask已接收通知
     * */
    fun sendAsk(context: Context, event: String, msg: String) {
        if (checkSocketStatus()) {
            sendAskImpl(event, msg)
        } else {
            reSend(context) { sendAskImpl(event, msg) }
        }
    }

    private fun sendAskImpl(event: String, msg: String) {
        mSocket!!.let {
            Log.i(SocketService.TAG, "socket 发送Ask：$msg")
            it.emit(event, JSONObject(msg))
        }
    }

    /**
     * 重新创建socket 并且重新发送
     * */
    private fun reSend(context: Context, sendFun: () -> Unit) {
        init(context, mToken!!)
        val timer = Timer()
        Log.i(SocketService.TAG, "reSend()")
        timer.schedule(object : TimerTask() {
            private var count = 0
            override fun run() {
                Log.i(SocketService.TAG, "socket 未连接，准备重新发送")
                count++
                if (isLinked) {
                    timer.cancel()
                    sendFun.invoke()
                } else if (count > 10) {
                    timer.cancel()
                }
            }
        }, 1000, 3000)
    }

    /**
     * 检查Socket当前是否可用
     * */
    private fun checkSocketStatus(): Boolean {
        return mSocket != null && isLinked
    }


    /**
     * 发送消息
     * */
    fun sendMsg(context: Context, event: String, msg: String, msgId: String) {
        if (checkSocketStatus()) {
            sendMsgImpl(context, event, msg, msgId)
        } else {
            reSend(context) { sendMsgImpl(context, event, msg, msgId) }
        }
    }

    private fun sendMsgImpl(context: Context, event: String, msg: String, msgId: String) {
        mSocket?.let {
            Log.i(SocketService.TAG, "socket 发送消息：$msg")
            it.emit(event, JSONObject(msg), MsgCallAck(context, event, msgId))
        }
    }


    /**
     * 创建新的 Socket 对象
     * */
    private fun newSocket(context: Context, token: String) {
        Log.i(SocketService.TAG, "socket 创建  -> $token")
        mToken = token
        val opts = IO.Options()
        opts.forceNew = false
        opts.reconnection = true
        opts.reconnectionDelay = 3000
        opts.reconnectionDelayMax = 5000
        opts.timeout = -1
        opts.query = token  //uid=uid&token=token&device=1
        try {
            mSocket = IO.socket(Constant.SOCKET_HOST, opts)
            //添加全局监听器
            initSocketListener(context)
            //连接
            open()
            Log.i(SocketService.TAG, "socket 创建成功  -> $token")
        } catch (e: Exception) {
            Log.e(SocketService.TAG, "socket 创建失败 -> $token")
            e.printStackTrace()
        }
    }

    private var reNewSocketSubscription: Disposable? = null


    /**
     * 初始化监听器
     * */
    private fun initSocketListener(context: Context) {
        mChatMessageListener = ChatMessageListener(context)
        mLoginConflictListener = LoginConflictListener(context)

        //链接已断开监听
        mDisConnectListener = Emitter.Listener { args ->
            val result = if (args.isNotEmpty()) args[0].toString() else args.toString()
            Log.i(SocketService.TAG, "socket 链接已断开 -> $result")
            isLinked = false
            release()
            //延时3秒重新开启Socket
            RxUtils.dispose(reNewSocketSubscription)
            reNewSocketSubscription = Observable.timer(3000, TimeUnit.MILLISECONDS)
                    .subscribe {
                        init(context, mToken!!)
                    }
        }

        //链接成功监听
        mConnectListener = Emitter.Listener { args ->
            isLinked = true
            Log.i(SocketService.TAG, "socket 连接成功")
        }

        //链接发生错误监听
        mConnectErrorListener = Emitter.Listener { args ->
            val result = if (args.isNotEmpty()) args[0].toString() else args.toString()
            Log.i(SocketService.TAG, "socket 连接发生错误 -> $result")
            isLinked = false
//            release()
//            //延时3秒重新开启Socket
//            RxUtils.unsubscribe(reNewSocketSubscription)
//            reNewSocketSubscription = Observable.timer(3000, TimeUnit.MILLISECONDS)
//                    .subscribe { checkSocketStatus(context) }
        }
        mConnectTimeOutListener = ConnectTimeOutListener(context, mSocket)
        mDataErrorListener = DataErrorListener(context)
        addSocketListener()
    }

    /**
     * 添加监听器
     */
    private fun addSocketListener() {
        mSocket?.apply {
            Log.i(SocketService.TAG, "socket 添加消息监听器")
            on(SocketEvent.EVENT_CHAT, mChatMessageListener)
            on(SocketEvent.EVENT_CONFLICT, mLoginConflictListener)
            on(Socket.EVENT_CONNECT, mConnectListener)
            on(Socket.EVENT_DISCONNECT, mDisConnectListener)
            on(Socket.EVENT_CONNECT_ERROR, mConnectErrorListener)
            on(Socket.EVENT_CONNECT_TIMEOUT, mConnectTimeOutListener)
            on(Socket.EVENT_ERROR, mDataErrorListener)
        }
    }

    /**
     * 清除监听器
     */
    fun removeSocketListener() {
        mSocket?.apply {
            Log.i(SocketService.TAG, "socket 清空消息监听器")
            off(SocketEvent.EVENT_CHAT, mChatMessageListener)
            off(SocketEvent.EVENT_CONFLICT, mLoginConflictListener)
            off(Socket.EVENT_CONNECT, mConnectListener)
            off(Socket.EVENT_DISCONNECT, mDisConnectListener)
            off(Socket.EVENT_CONNECT_ERROR, mConnectErrorListener)
            off(Socket.EVENT_CONNECT_TIMEOUT, mConnectTimeOutListener)
            off(Socket.EVENT_ERROR, mDataErrorListener)
        }
    }

}