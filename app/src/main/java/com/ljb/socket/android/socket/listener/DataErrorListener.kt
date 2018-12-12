package com.ljb.socket.android.socket.listener

import android.content.Context
import android.util.Log
import com.ljb.socket.android.socket.SocketService
import io.socket.emitter.Emitter

/**
 * Author:Ljb
 * Time:2018/9/5
 * There is a lot of misery in life
 **/
class DataErrorListener(val context: Context) : Emitter.Listener {

    override fun call(vararg args: Any?) {
        val result = if (args.isNotEmpty()) args[0].toString() else args.toString()
        Log.i(SocketService.TAG, "socket error -> $result")
    }
}
