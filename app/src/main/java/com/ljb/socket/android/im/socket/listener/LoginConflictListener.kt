package com.ljb.socket.android.im.socket.listener

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ljb.socket.android.im.socket.SocketManager
import com.ljb.socket.android.im.socket.SocketService
import io.socket.emitter.Emitter

/**
 * Author:Ljb
 * Time:2018/9/5
 * There is a lot of misery in life
 **/
class LoginConflictListener(val context: Context) : Emitter.Listener {

    override fun call(vararg args: Any?) {
        val result = if (args.isNotEmpty()) args[0].toString() else args.toString()
        log(result)
        val intent = Intent(SocketManager.ResponseChatMsgCallReceiver.ACTION_CONFLICT)
        intent.putExtra(SocketManager.ResponseChatMsgCallReceiver.RESULT, result)
        context.sendBroadcast(intent)
    }

    private fun log(any: Any?) {
        Log.i(SocketService.TAG, """
            帐号在其它地方登录:
            -> result : $any
        """.trimIndent())
    }
}