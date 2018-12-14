package com.ljb.socket.android.socket.notify

import android.app.PendingIntent
import android.graphics.Bitmap

/**
 * Author:Ljb
 * Time:2018/12/14
 * There is a lot of misery in life
 **/
class NotificationData {
    var notifId: Int = 0
    var pIntent: PendingIntent? = null
    var title: String = ""
    var content: String = ""
    var bitmap: Bitmap? = null
}
