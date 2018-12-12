package com.ljb.socket.android

import android.app.Application
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.db.DatabaseHelperImpl
import com.ljb.socket.android.protocol.dao.ProtocolConfig
import com.ljb.socket.android.utils.SPUtils
import com.squareup.leakcanary.LeakCanary
import dao.ljb.kt.DaoConfig
import net.ljb.kt.HttpConfig

/**
 * Author:Ljb
 * Time:2018/12/5
 * There is a lot of misery in life
 **/
class SocketIOApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        SPUtils.init(this)

        HttpConfig.init(Constant.HTTP_HOST, isLog = BuildConfig.DEBUG)

        DaoConfig.init(DatabaseHelperImpl(this, Constant.DB.NAME, Constant.DB.VERSION), ProtocolConfig)

    }
}
