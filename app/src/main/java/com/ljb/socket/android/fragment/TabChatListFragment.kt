package com.ljb.socket.android.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ljb.page.PageStateLayout
import com.ljb.socket.android.R
import com.ljb.socket.android.act.ChatActivity
import com.ljb.socket.android.adapter.ChatListAdapter
import com.ljb.socket.android.adapter.base.BaseRVAdapter
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.common.fragment.BaseMvpFragment
import com.ljb.socket.android.contract.TabChatContract
import com.ljb.socket.android.event.NewNumEvent
import com.ljb.socket.android.model.ConversationBean
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.TabChatPresenter
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_tab_chat_list.*
import kotlinx.android.synthetic.main.layout_rv_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/

class TabChatListFragment : BaseMvpFragment<TabChatContract.IPresenter>(), TabChatContract.IView, BaseRVAdapter.OnItemClickListener {


    private lateinit var mAdapter: ChatListAdapter

    override fun getLayoutId() = R.layout.fragment_tab_chat_list

    override fun registerPresenter() = TabChatPresenter::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()

    }

    override fun onResume() {
        super.onResume()
        getPresenter().getAllConversation()
    }

    override fun initView() {
        rv_list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mAdapter = ChatListAdapter(activity!!, mutableListOf())
            mAdapter.setOnItemClickListener(this@TabChatListFragment)
            adapter = mAdapter
        }
    }


    override fun setPage(state: PageStateLayout.PageState) {
        page_layout.setPage(state)
    }

    override fun setAllConversation(data: List<ConversationBean>) {
        mAdapter.data.clear()
        mAdapter.data.addAll(data)
        mAdapter.notifyDataSetChanged()
    }

    override fun notifyAdapterPosition(position: Int, bean: ConversationBean) {
        mAdapter.data[position] = bean
        mAdapter.notifyItemChanged(position)
    }

    override fun addConversationData(it: ConversationBean) {
        mAdapter.data.add(0, it)
        mAdapter.notifyItemInserted(0)
    }


    override fun onItemClick(view: View, position: Int) {
        val locUser = JsonParser.fromJsonObj(SPUtils.getString(Constant.SPKey.KEY_USER), UserBean::class.java)
        val toUser = mAdapter.data[position].user
        ChatActivity.startActivity(activity!!, locUser, toUser)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewNumEvent(event: NewNumEvent) {
        getPresenter().queryNewNumByConversation(event.conversation, mAdapter.data)
    }

}