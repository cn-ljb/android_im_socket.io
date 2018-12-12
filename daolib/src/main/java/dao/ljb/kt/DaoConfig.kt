package dao.ljb.kt

import android.annotation.SuppressLint
import dao.ljb.kt.core.DatabaseHelper
import dao.ljb.kt.core.IProtocolTransform

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
object DaoConfig {

    @SuppressLint("StaticFieldLeak")
    private var mDbHelper: DatabaseHelper? = null
    private var mTransform: IProtocolTransform? = null

    fun init(helper: DatabaseHelper, transform: IProtocolTransform) {
        mDbHelper = helper
        mTransform = transform
    }

    fun getHelper(): DatabaseHelper {
        if (mDbHelper == null) throw IllegalStateException("dao not init")
        return mDbHelper!!
    }

    fun getTransform(): IProtocolTransform {
        if (mTransform == null) throw IllegalStateException("dao not init")
        return mTransform!!
    }

}
