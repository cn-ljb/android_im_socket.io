package com.ljb.socket.android.contract

import com.ljb.page.PageStateLayout
import com.ljb.socket.android.model.UserBean
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
interface TabContactListContract {

    interface IView : IViewContract {
        fun setContactList(data: List<UserBean>)
        fun setPage(state: PageStateLayout.PageState)
    }

    interface IPresenter : IPresenterContract {
        fun getContactList()
    }
}