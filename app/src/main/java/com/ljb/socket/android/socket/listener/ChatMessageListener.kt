package com.ljb.socket.android.socket.listener

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.ljb.socket.android.socket.SocketService
import io.socket.emitter.Emitter

/**
 * Author:Ljb
 * Time:2018/9/5
 * There is a lot of misery in life
 **/
class ChatMessageListener(val context: Context) : Emitter.Listener {

    override fun call(vararg args: Any?) {
        val result = if (args.isEmpty()) args.toString() else args[0].toString()
        log(result)
        val serviceIntent = Intent(context, SocketService::class.java)
        serviceIntent.putExtra(SocketService.CMD, SocketService.CMD_CHAT_CALLBACK)
        serviceIntent.putExtra(SocketService.MSG, result)
        startService(context, serviceIntent)
    }

    private fun log(any: Any?) {
        Log.i(SocketService.TAG, """
           接收到消息:
            -> result : $any
        """.trimIndent())
    }

    private fun startService(appContext: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }

}