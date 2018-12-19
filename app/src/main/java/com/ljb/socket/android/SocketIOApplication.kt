package com.ljb.socket.android

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.os.Bundle
import android.util.Log
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.db.DatabaseHelperImpl
import com.ljb.socket.android.protocol.dao.ProtocolConfig
import com.ljb.socket.android.socket.notify.SocketNotificationManager
import com.ljb.socket.android.utils.SPUtils
import com.squareup.leakcanary.LeakCanary
import dao.ljb.kt.DaoConfig
import net.ljb.kt.HttpConfig

/**
 * Author:Ljb
 * Time:2018/12/5
 * There is a lot of misery in life
 **/
class SocketIOApplication : Application(), Application.ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        SPUtils.init(this)

        HttpConfig.init(Constant.HTTP_HOST, isLog = BuildConfig.DEBUG)

        DaoConfig.init(DatabaseHelperImpl(this, Constant.DB.NAME, Constant.DB.VERSION), ProtocolConfig)

        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
        Log.i("====", "切入前台")
        SocketNotificationManager.setShowImNotification(false)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.i("====", "遁入后台")
            SocketNotificationManager.setShowImNotification(true)
        }
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

}
