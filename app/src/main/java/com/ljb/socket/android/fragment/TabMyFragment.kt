package com.ljb.socket.android.fragment

import android.content.Intent
import com.ljb.socket.android.R
import com.ljb.socket.android.act.LoginActivity
import com.ljb.socket.android.common.fragment.BaseMvpFragment
import com.ljb.socket.android.contract.TabMyContract
import com.ljb.socket.android.img.ImageLoader
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.TabMyPresenter
import kotlinx.android.synthetic.main.fragment_tab_my.*

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
class TabMyFragment : BaseMvpFragment<TabMyContract.IPresenter>(), TabMyContract.IView {


    override fun getLayoutId() = R.layout.fragment_tab_my

    override fun registerPresenter() = TabMyPresenter::class.java

    override fun initView() {
        btn_logout.setOnClickListener { logout() }
    }

    override fun initData() {
        getPresenter().getUserInfo()
    }

    override fun goLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun logout() {
        getPresenter().logout()
    }


    override fun setUserInfo(user: UserBean) {
        ImageLoader.load(activity!!, user.headUrl, iv_header, ImageLoader.getCircleRequest())
        tv_name.text = user.name
    }

}
