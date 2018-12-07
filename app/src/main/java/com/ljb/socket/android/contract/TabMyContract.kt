package com.ljb.socket.android.contract

import com.ljb.socket.android.model.UserBean
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
class TabMyContract {
    interface IView : IViewContract {
        fun setUserInfo(user: UserBean)
        fun goLogin()
    }

    interface IPresenter : IPresenterContract {
        fun getUserInfo()
        fun logout()
    }
}
