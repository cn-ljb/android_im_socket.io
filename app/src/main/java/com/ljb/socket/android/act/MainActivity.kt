package com.ljb.socket.android.act

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.ljb.socket.android.R
import com.ljb.socket.android.adapter.MainTabAdapter
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.common.act.BaseMvpFragmentActivity
import com.ljb.socket.android.contract.MainContract
import com.ljb.socket.android.event.NewNumEvent
import com.ljb.socket.android.fragment.TabChatListFragment
import com.ljb.socket.android.fragment.TabContactListFragment
import com.ljb.socket.android.fragment.TabMyFragment
import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.model.TabBean
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.MainPresenter
import com.ljb.socket.android.socket.notify.SocketNotificationClickReceiver
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.SPUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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

    private lateinit var mAdapter: MainTabAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        handleIntent(intent)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun registerPresenter() = MainPresenter::class.java

    override fun getLayoutId() = R.layout.activity_main

    override fun init(savedInstanceState: Bundle?) {
        getPresenter().initSocket()
    }


    override fun initData() {
        getPresenter().initTable()
        getPresenter().queryNewNum()
    }

    override fun initView() {
        mAdapter = MainTabAdapter(this, mTabList)
        tgv_group.setOnItemClickListener { openTabFragment(it) }
        tgv_group.setAdapter(mAdapter)
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

    override fun setTableInitResult(result: Boolean) {
        if (!result) {
            Toast.makeText(this, R.string.db_init_error, Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    @Subscribe
    fun onNewNumEvent(event: NewNumEvent) {
        getPresenter().queryNewNum()
    }

    override fun updateNewNum(newNum: Int) {
        mAdapter.mData[0].newNum = newNum
        mAdapter.notifyItemChanged(0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val chatMessage = intent.getParcelableExtra<ChatMessage>(SocketNotificationClickReceiver.KEY_CHAT_MESSAGE)
                ?: return
        getPresenter().getContactById(chatMessage.fromId)
    }

    override fun openIm(user: UserBean?) {
        if (user == null) return
        val locUser = JsonParser.fromJsonObj(SPUtils.getString(Constant.SPKey.KEY_USER), UserBean::class.java)
        ChatActivity.startActivity(this, locUser, user)
    }

}
