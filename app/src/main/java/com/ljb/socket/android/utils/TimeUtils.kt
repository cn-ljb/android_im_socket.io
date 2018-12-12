package com.ljb.socket.android.utils

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author:Ljb
 * Time:2018/11/10
 * There is a lot of misery in life
 **/
object TimeUtils {

    private val mFormatYMD_HM by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    private val mFormatYMD_HMS by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    fun formatChatMessageTime(time: Long): String {
        val now = Date()

        val inputDate = Calendar.getInstance()
        inputDate.time = Date(time)

        val nowDate = Calendar.getInstance()
        nowDate.time = now

        val yearOfInput = inputDate.get(Calendar.YEAR)
        val monthOfInput = inputDate.get(Calendar.MONTH)
        val dayOfInput = inputDate.get(Calendar.DAY_OF_MONTH)


        val yearOfNow = nowDate.get(Calendar.YEAR)
        val monthOfNow = nowDate.get(Calendar.MONTH)
        val dayOfNow = nowDate.get(Calendar.DAY_OF_MONTH)

        val dayWeekOfInput = inputDate.get(Calendar.DAY_OF_WEEK)

        val dayStr = arrayOf("星期日 ", "星期一 ", "星期二 ", "星期三 ", "星期四 ", "星期五 ", "星期六")
        var day = dayStr[dayWeekOfInput - 1]
        if (yearOfInput == yearOfNow && monthOfInput == monthOfNow) {
            if (dayOfInput == dayOfNow) {
                day = "今天"
            } else if (dayOfInput == dayOfNow - 1) {
                day = "昨天"
            } else if (dayOfInput == dayOfNow + 1) {
                day = "明天"
            }
        }
        val ymd = mFormatYMD_HM.format(inputDate.time)
        return "$day $ymd"
    }

    fun formatVideoWaitTime(startTime: String, endTime: String): String {
        var result = ""
        val startTime = mFormatYMD_HMS.parse(startTime)
        result = mFormatYMD_HM.format(startTime)
        val endTime = mFormatYMD_HMS.parse(endTime)
        val c = Calendar.getInstance()
        c.time = endTime
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)

        result += "-${if (hour < 10) "0$hour" else hour}:${if (m < 10) "0$m" else m}"
        return result
    }


    fun getImSurplusTimeStr(endTime: Long): String {
        if (endTime <= 0) return "00:00:00"
        var surplusTime = endTime

        var hourStr = ""
        if (surplusTime > 1000 * 60 * 60) {
            //小时
            val hour = surplusTime / (1000 * 60 * 60)
            hourStr = if (hour > 9) hour.toString() else "0$hour"
            surplusTime %= 1000 * 60 * 60
        } else {
            hourStr = "00"
        }

        var mStr = ""
        if (surplusTime > 1000 * 60) {
            //小时
            val m = surplusTime / (1000 * 60)
            mStr = if (m > 9) m.toString() else "0$m"
            surplusTime %= 1000 * 60
        } else {
            mStr = "00"
        }

        var sStr = ""
        if (surplusTime > 1000) {
            //小时
            val s = surplusTime / 1000
            sStr = if (s > 9) s.toString() else "0$s"
        } else {
            sStr = "00"
        }
        return "$hourStr:$mStr:$sStr"
    }

    fun getImSurplusTime(endTime: String?): Long {
        if (TextUtils.isEmpty(endTime)) return -1
        val time = mFormatYMD_HMS.parse(endTime).time
        val surplusTime = time - System.currentTimeMillis()
        if (surplusTime <= 0) return -1
        return surplusTime
    }

}
