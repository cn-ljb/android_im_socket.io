package com.ljb.socket.android.contract

import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * Author:Ljb
 * Time:2018/12/5
 * There is a lot of misery in life
 **/
interface SplashContract {

    interface IView : IViewContract {
        fun uploadImgSuccess(url: String)
        fun loginSuccess()
        fun uploadImgError()
    }

    interface IPresenter : IPresenterContract {
        fun login(userName: String, headUrl: String)
        fun uploadImg(picturePath: String)
    }
}
