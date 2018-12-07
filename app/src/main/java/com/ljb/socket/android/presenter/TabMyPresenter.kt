package com.ljb.socket.android.presenter

import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.contract.TabMyContract
import com.ljb.socket.android.im.socket.SocketManager
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.base.BaseRxLifePresenter
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.SPUtils
import mvp.ljb.kt.presenter.getContextEx

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
class TabMyPresenter : BaseRxLifePresenter<TabMyContract.IView>(), TabMyContract.IPresenter {

    override fun logout() {
        SPUtils.putString(Constant.SPKey.KEY_USER, "")
        SocketManager.logoutSocket(getContextEx())
        getMvpView().goLogin()
    }

    override fun getUserInfo() {
        val userJSon = SPUtils.getString(Constant.SPKey.KEY_USER)
        val user = JsonParser.fromJsonObj(userJSon, UserBean::class.java)
        getMvpView().setUserInfo(user)
    }

}
