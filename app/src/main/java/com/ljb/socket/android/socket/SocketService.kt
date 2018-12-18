package com.ljb.socket.android.socket

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.ljb.socket.android.BuildConfig
import com.ljb.socket.android.R
import com.ljb.socket.android.socket.notify.SocketNotificationChannel

/**
 * IM通讯Service
 * Author:Ljb
 * Time:2018/9/5
 * There is a lot of misery in life
 **/
class SocketService : Service() {

    companion object {
        const val TAG = "SocketService"
        const val CMD = "cmd"
        const val DATA = "data"
        const val MSG_EVENT = "event"
        const val MSG_ID = "pid"
        const val MSG = "msg"

        const val ID_NOTIFICATION = 0x10099

        const val CMD_INIT_SOCKET = 0
        const val CMD_RELEASE_SOCKET = 1
        const val CMD_SEND_MSG = 2
        const val CMD_SEND_MSG_CALLBACK = 3
        const val CMD_SEND_ASK = 4
        const val CMD_CHAT_CALLBACK = 5
        const val CMD_REMOVE_LISTENER = 6


    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand()")
        intent?.let {
            val cmd = it.getIntExtra(CMD, -1)
            when (cmd) {
                CMD_INIT_SOCKET -> initSocket(it)
                CMD_RELEASE_SOCKET -> releaseSocket()
                CMD_SEND_MSG -> sendMsg2Socket(it)
                CMD_SEND_MSG_CALLBACK -> callRequest2UI(it)
                CMD_SEND_ASK -> sendAck2Socket(it)
                CMD_CHAT_CALLBACK -> callResponse2UI(it)
                CMD_REMOVE_LISTENER -> removeListener()
            }
        }
        return START_STICKY
    }


    override fun onCreate() {
        Log.i(TAG, "onCreate()")
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SocketNotificationChannel.initChannel(this, BuildConfig.APPLICATION_ID)
            val icon = BitmapFactory.decodeResource(resources, R.mipmap.icon_app)
            val notification = Notification.Builder(this)
                    .setChannelId(BuildConfig.APPLICATION_ID)
                    .setContentTitle("Socket.IO Service")
                    .setContentText("IM Chat")
                    .setSmallIcon(R.mipmap.icon_app)
                    .setLargeIcon(icon)
                    .build()
            startForeground(ID_NOTIFICATION, notification)
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy()")
        super.onDestroy()
    }


    private fun callResponse2UI(intent: Intent) {
        val result = intent.getStringExtra(MSG)
        val callIntent = Intent(SocketManager.ResponseChatMsgCallReceiver.ACTION_CHAT)
        callIntent.putExtra(SocketManager.ResponseChatMsgCallReceiver.RESULT, result)
        sendBroadcast(callIntent)
    }

    private fun callRequest2UI(intent: Intent) {
        val msgId = intent.getStringExtra(MSG_ID)
        val result = intent.getStringExtra(DATA)

        val callIntent = Intent()
        callIntent.action = SocketManager.RequestChatMsgCallReceiver.ACTION
        callIntent.putExtra(SocketManager.RequestChatMsgCallReceiver.MSG_ID, msgId)
        callIntent.putExtra(SocketManager.RequestChatMsgCallReceiver.RESULT, result)
        sendBroadcast(callIntent)
    }


    private fun sendAck2Socket(intent: Intent) {
        val msg = intent.getStringExtra(MSG)
        val event = intent.getStringExtra(MSG_EVENT)
        SocketClient.sendAck(this, event, msg)
    }


    private fun sendMsg2Socket(intent: Intent) {
        val msg = intent.getStringExtra(MSG)
        val event = intent.getStringExtra(MSG_EVENT)
        val msgId = intent.getStringExtra(MSG_ID)
        SocketClient.sendMsg(this, event, msg, msgId)
    }

    private fun initSocket(intent: Intent) {
        val token = intent.getStringExtra(DATA)
        SocketClient.init(this, token)
    }

    private fun releaseSocket() {
        SocketClient.releaseAll()
        stopSelf()
    }


    private fun removeListener() {
        SocketClient.removeSocketListener()
    }


}
