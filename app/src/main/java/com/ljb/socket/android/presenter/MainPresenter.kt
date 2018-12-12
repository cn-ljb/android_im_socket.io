package com.ljb.socket.android.presenter

import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.common.ex.subscribeEx
import com.ljb.socket.android.contract.MainContract
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.base.BaseRxLifePresenter
import com.ljb.socket.android.protocol.dao.IInitDaoProtocol
import com.ljb.socket.android.socket.SocketManager
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.SPUtils
import dao.ljb.kt.core.DaoFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    override fun initTable() {
        DaoFactory.getProtocol(IInitDaoProtocol::class.java)
                .initTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx({
                    getMvpView().setTableInitResult(it)
                }, {
                    getMvpView().setTableInitResult(false)
                }).bindRxLifeEx(RxLife.ON_DESTROY)
    }

}
