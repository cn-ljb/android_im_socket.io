package com.ljb.socket.android.presenter

import com.ljb.page.PageStateLayout
import com.ljb.socket.android.common.ex.subscribeEx
import com.ljb.socket.android.contract.TabContactListContract
import com.ljb.socket.android.presenter.base.BaseRxLifePresenter
import com.ljb.socket.android.protocol.dao.IContactListProtocol
import com.ljb.socket.android.table.ContactTable
import dao.ljb.kt.core.DaoFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
class TabContactListPresenter : BaseRxLifePresenter<TabContactListContract.IView>(), TabContactListContract.IPresenter {

    private val contactTable by lazy { ContactTable() }

    override fun getContactList() {
        DaoFactory.getProtocol(IContactListProtocol::class.java)
                .getAllContactList(contactTable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx({
                    getMvpView().setPage(PageStateLayout.PageState.STATE_SUCCESS)
                    getMvpView().setContactList(it)
                }, {
                    getMvpView().setPage(PageStateLayout.PageState.STATE_ERROR)
                }).bindRxLifeEx(RxLife.ON_DESTROY)
    }

}
