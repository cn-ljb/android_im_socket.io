package com.ljb.socket.android.socket.listener

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.socket.SocketClient
import com.ljb.socket.android.socket.SocketService
import com.ljb.socket.android.utils.JsonParser
import java.util.*

/**
 * Author:Ljb
 * Time:2018/12/18
 * There is a lot of misery in life
 **/
class MsgCallError(var context: Context?, val event: String, val msg: String, val msgId: String) : Runnable {


    override fun run() {
        if (context == null) return
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            private var count = 0
            override fun run() {
                Log.i(SocketService.TAG, "socket 未连接，准备重新发送")
                count++
                if (SocketClient.isLinked) {
                    //连接成功
                    timer.cancel()
                    val intent = Intent(context, SocketService::class.java)
                    intent.putExtra(SocketService.CMD, SocketService.CMD_SEND_MSG)
                    intent.putExtra(SocketService.MSG, msg)
                    intent.putExtra(SocketService.MSG_EVENT, event)
                    intent.putExtra(SocketService.MSG_ID, msgId)
                    startService(context!!.applicationContext, intent)
                    context = null
                } else if (count > 10) {
                    timer.cancel()
                    // 尝试10次后依旧未连接,此消息失败
                    val serviceIntent = Intent(context, SocketService::class.java)
                    serviceIntent.putExtra(SocketService.CMD, SocketService.CMD_SEND_MSG_CALLBACK)
                    serviceIntent.putExtra(SocketService.MSG_ID, msgId)

                    //手动制造一个失败的ack
                    val ackChatMessage = JsonParser.fromJsonObj(msg, ChatMessage::class.java)
                    ackChatMessage.status = ChatMessage.MSG_STATUS_SEND_ERROR
                    ackChatMessage.type = ChatMessage.TYPE_CMD
                    ackChatMessage.cmd = ChatMessage.CMD_RECEIVE_ACK
                    serviceIntent.putExtra(SocketService.DATA, JsonParser.toJson(ackChatMessage))
                    startService(context!!.applicationContext, serviceIntent)
                    context = null
                }
            }
        }, 1000, 3000)
    }

    private fun startService(appContext: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }

}
