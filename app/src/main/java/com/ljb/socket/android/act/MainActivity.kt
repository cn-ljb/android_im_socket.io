package com.ljb.socket.android.act

import android.os.Bundle
import com.ljb.socket.android.R
import com.ljb.socket.android.common.act.BaseMvpFragmentActivity
import com.ljb.socket.android.contract.MainContract
import com.ljb.socket.android.presenter.MainPresenter

class MainActivity : BaseMvpFragmentActivity<MainContract.IPresenter>(), MainContract.IView {

    override fun registerPresenter() = MainPresenter::class.java

    override fun getLayoutId() = R.layout.activity_main

    override fun init(savedInstanceState: Bundle?) {
        getPresenter().initSocket()
    }

}
