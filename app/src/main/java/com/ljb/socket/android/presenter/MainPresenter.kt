package com.ljb.socket.android.presenter

import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.contract.MainContract
import com.ljb.socket.android.im.socket.SocketManager
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.base.BaseRxLifePresenter
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.SPUtils
import mvp.ljb.kt.presenter.getContextEx

/**
 * Author:Ljb
 * Time:2018/12/6
 * There is a lot of misery in life
 **/
class MainPresenter : BaseRxLifePresenter<MainContract.IView>(), MainContract.IPresenter {

    override fun initSocket() {
        val userJson = SPUtils.getString(Constant.SPKey.KEY_USER)
        val user = JsonParser.fromJsonObj(userJson, UserBean::class.java)
        val socketToken = SocketManager.getSocketToken(user.uid, user.name, user.headUrl)
        SocketManager.loginSocket(getContextEx().applicationContext, socketToken)
    }

}
