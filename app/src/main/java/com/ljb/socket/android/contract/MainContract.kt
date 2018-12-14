package com.ljb.socket.android.contract

import com.ljb.socket.android.model.UserBean
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * Author:Ljb
 * Time:2018/12/6
 * There is a lot of misery in life
 **/
interface MainContract {

    interface IView : IViewContract {
        fun setTableInitResult(result: Boolean)
        fun updateNewNum(newNum: Int)
        fun openIm(user: UserBean?)
    }


    interface IPresenter : IPresenterContract {
        fun initSocket()
        fun initTable()
        fun queryNewNum()
        fun getContactById(fromId: String)
    }

}
