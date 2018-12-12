package com.ljb.socket.android.common


/**
 * 常量池
 * Created by L on 2017/7/11.
 */
object Constant {

    //socket host url
    const val SOCKET_HOST = "http://192.168.1.5:9092"
    const val HTTP_HOST = "http://integer.wang"

    object ReqCode {
        const val CODE_PIC_LIB = 0x10001
    }

    object PermissionCode {
        const val CODE_INIT = 0x10000
    }

    object SPKey {
        const val KEY_USER = "user"
        const val KEY_UID = "uid"
    }

    object DB {
        const val NAME = "dbSocketIO"
        const val VERSION = 1
    }

}