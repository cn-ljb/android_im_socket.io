package com.ljb.socket.android.act

import android.os.Bundle
import com.ljb.socket.android.R
import com.ljb.socket.android.adapter.MainTabAdapter
import com.ljb.socket.android.common.act.BaseMvpFragmentActivity
import com.ljb.socket.android.contract.MainContract
import com.ljb.socket.android.fragment.TabChatListFragment
import com.ljb.socket.android.fragment.TabContactListFragment
import com.ljb.socket.android.fragment.TabMyFragment
import com.ljb.socket.android.model.TabBean
import com.ljb.socket.android.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMvpFragmentActivity<MainContract.IPresenter>(), MainContract.IView {

    private var mCurIndex: Int = 0

    private val mFragments = listOf(
            TabChatListFragment(),
            TabContactListFragment(),
            TabMyFragment()
    )

    private val mTabList = listOf(
            TabBean(R.drawable.bottom_tab_chat, R.string.chat),
            TabBean(R.drawable.bottom_tab_contact, R.string.contact),
            TabBean(R.drawable.bottom_tab_my, R.string.my)
    )

    override fun registerPresenter() = MainPresenter::class.java

    override fun getLayoutId() = R.layout.activity_main

    override fun init(savedInstanceState: Bundle?) {
        getPresenter().initSocket()
    }

    override fun initView() {
        tgv_group.setOnItemClickListener { openTabFragment(it) }
        tgv_group.setAdapter(MainTabAdapter(this, mTabList))
        openTabFragment(mCurIndex)
    }

    private fun openTabFragment(position: Int) {
        tgv_group.setSelectedPosition(position)
        val ft = supportFragmentManager.beginTransaction()
        ft.hide(mFragments[mCurIndex])
        var f = supportFragmentManager.findFragmentByTag(mFragments[position].javaClass.simpleName)
        if (f == null) {
            f = mFragments[position]
            ft.add(R.id.fl_content, f, f.javaClass.simpleName)
        }
        ft.show(f).commit()
        mCurIndex = position
    }

}
