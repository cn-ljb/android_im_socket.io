package com.ljb.page.utils

import android.content.Context

/**
 * Author:Ljb
 * Time:2018/9/2
 * There is a lot of misery in life
 **/
object DipUtils {

    fun dip2px(context: Context, dip: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dip * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, px: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }

}
