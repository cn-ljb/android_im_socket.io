package com.ljb.socket.android.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ljb.page.PageStateLayout
import com.ljb.socket.android.R
import com.ljb.socket.android.act.ChatActivity
import com.ljb.socket.android.adapter.ContactListAdapter
import com.ljb.socket.android.adapter.base.BaseRVAdapter
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.common.fragment.BaseMvpFragment
import com.ljb.socket.android.contract.TabContactListContract
import com.ljb.socket.android.event.RefreshContactListEvent
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.TabContactListPresenter
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_tab_contact_list.*
import kotlinx.android.synthetic.main.layout_rv_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
class TabContactListFragment : BaseMvpFragment<TabContactListContract.IPresenter>(), TabContactListContract.IView, BaseRVAdapter.OnItemClickListener {


    override fun getLayoutId() = R.layout.fragment_tab_contact_list

    override fun registerPresenter() = TabContactListPresenter::class.java

    private lateinit var mAdapter: ContactListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initView() {
        rv_list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mAdapter = ContactListAdapter(activity!!, mutableListOf())
            mAdapter.setOnItemClickListener(this@TabContactListFragment)
            adapter = mAdapter
        }
    }

    override fun initData() {
        getPresenter().getContactList()
    }

    override fun setContactList(data: List<UserBean>) {
        mAdapter.data.clear()
        mAdapter.data.addAll(data)
        mAdapter.notifyDataSetChanged()
    }


    override fun setPage(state: PageStateLayout.PageState) {
        page_layout.setPage(state)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshContactListEvent(event: RefreshContactListEvent) {
        getPresenter().getContactList()
    }

    override fun onItemClick(view: View, position: Int) {
        val json = SPUtils.getString(Constant.SPKey.KEY_USER)
        val locUser = JsonParser.fromJsonObj(json, UserBean::class.java)
        val toUser = mAdapter.data[position]
        ChatActivity.startActivity(activity!!, locUser, toUser)
    }

}