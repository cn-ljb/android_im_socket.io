package com.ljb.socket.android

import android.app.Application
import com.ljb.socket.android.utils.SPUtils
import com.squareup.leakcanary.LeakCanary

/**
 * Author:Ljb
 * Time:2018/12/5
 * There is a lot of misery in life
 **/
class SocketIOApplication : Application() {

    //应避免创建全局的Application引用
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        SPUtils.init(this)
    }
}
