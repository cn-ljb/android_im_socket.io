package com.ljb.socket.android.socket.notify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi

/**
 * Author:Ljb
 * Time:2018/12/14
 * There is a lot of misery in life
 **/
object SocketNotificationChannel {

    private const val CHANNEL_NAME = "Socket.IO IM"

    @RequiresApi(Build.VERSION_CODES.O)
    fun initChannel(context: Context, channelId: String) {
        val notificationChannel = NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = longArrayOf(100, 200, 300)
        notificationChannel.setShowBadge(true)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
    }

}