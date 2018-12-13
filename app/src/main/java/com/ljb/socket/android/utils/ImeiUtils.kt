package com.ljb.socket.android.utils

import android.Manifest
import android.content.Context
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import java.util.*


/**
 * Author:Ljb
 * Time:2018/12/13
 * There is a lot of misery in life
 **/
object ImeiUtils {

    private var mDeviceImei: String? = null

    fun getImei(context: Context): String {
        if (!TextUtils.isEmpty(mDeviceImei)) {
            return mDeviceImei!!
        }
        var imei: String? = ""
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (PermissionUtils.checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                imei = tm.deviceId
            }
        } catch (e: Exception) {
            imei = ""
        }

        if (TextUtils.isEmpty(imei) || "0" == imei) {
            // 如果imei号为空或0，取mac地址作为imei号传递
            try {
                val wifi = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
                if (PermissionUtils.checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                    val info = wifi.connectionInfo
                    imei = info.macAddress
                }
            } catch (e: Exception) {
                imei = ""
            }

            // 如果mac地址为空或0，则通过uuid生成的imei号来传递
            if (TextUtils.isEmpty(imei) || "0" == imei) {
                imei = getUUID()
                if (TextUtils.isEmpty(imei)) {
                    return "0"
                }
            }
        }

        imei = imei!!.toLowerCase(Locale.US)

        mDeviceImei = imei
        return imei
    }

    fun getUUID(): String {
        var uuidStr = ""
        try {
            val uuid = UUID.randomUUID()
            uuidStr = uuid.toString()
            uuidStr = (uuidStr.substring(0, 8) + uuidStr.substring(9, 13) + uuidStr.substring(14, 18)
                    + uuidStr.substring(19, 23) + uuidStr.substring(24))
        } catch (e: Exception) {
            uuidStr = ""
        }
        return uuidStr
    }
}
