package com.ljb.socket.android.socket.listener

import android.content.Context
import android.util.Log
import com.ljb.socket.android.socket.SocketService
import io.socket.client.Socket
import io.socket.emitter.Emitter

/**
 * Author:Ljb
 * Time:2018/9/5
 * There is a lot of misery in life
 **/
class ConnectTimeOutListener(val context: Context, val socket: Socket?) : Emitter.Listener {

    override fun call(vararg args: Any?) {
        Log.i(SocketService.TAG, "socket 连接超时")
        socket?.open()
    }
}

