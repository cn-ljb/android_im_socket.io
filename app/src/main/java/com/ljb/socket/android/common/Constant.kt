package com.ljb.socket.android.common


/**
 * 常量池
 * Created by L on 2017/7/11.
 */
object Constant {

    //socket host url
    const val SOCKET_HOST = "http://172.16.201.33:9090"  //TODO loc ip  本机调试使用自己的IP，并运行service端
//    const val SOCKET_HOST = "http://39.96.10.124:9090"  //my service ip 服务器端IP 已部署外网
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
        const val SWITCH_NOTIFICATION = "switch_notification"
    }

    object DB {
        const val NAME = "dbSocketIO"
        const val VERSION = 1
    }

}