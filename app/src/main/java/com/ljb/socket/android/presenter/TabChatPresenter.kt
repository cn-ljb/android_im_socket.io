package com.ljb.socket.android.presenter

import com.ljb.page.PageStateLayout
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.common.ex.subscribeEx
import com.ljb.socket.android.contract.TabChatContract
import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.model.ConversationBean
import com.ljb.socket.android.presenter.base.BaseRxLifePresenter
import com.ljb.socket.android.protocol.dao.IChatHistoryDaoProtocol
import com.ljb.socket.android.protocol.dao.IContactProtocol
import com.ljb.socket.android.protocol.dao.INewNumDaoProtocol
import com.ljb.socket.android.table.ContactTable
import com.ljb.socket.android.table.ImConversationTable
import com.ljb.socket.android.utils.SPUtils
import com.ljb.socket.android.table.ImNewNumTable
import dao.ljb.kt.core.DaoFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Author:Ljb
 * Time:2018/12/13
 * There is a lot of misery in life
 **/
class TabChatPresenter : BaseRxLifePresenter<TabChatContract.IView>(), TabChatContract.IPresenter {

    private val mConversationTable = ImConversationTable()
    private val mContactTable = ContactTable()
    private val mNewNumTable = ImNewNumTable()

    override fun getAllConversation() {
        DaoFactory.getProtocol(IChatHistoryDaoProtocol::class.java)
                .getAllConversation(mConversationTable)
                .map { transformUserAndNewNum(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx({
                    getMvpView().setPage(PageStateLayout.PageState.STATE_SUCCESS)
                    getMvpView().setAllConversation(it)
                }, {
                    getMvpView().setPage(PageStateLayout.PageState.STATE_ERROR)
                }).bindRxLifeEx(RxLife.ON_DESTROY)
    }

    private fun transformUserAndNewNum(list: List<ChatMessage>): List<ConversationBean> {
        val data = ArrayList<ConversationBean>()
        val locUid = SPUtils.getString(Constant.SPKey.KEY_UID)
        for (chatMessage in list) {
            val uid = if (locUid == chatMessage.fromId) chatMessage.toId else chatMessage.fromId
            val user = DaoFactory.getProtocol(IContactProtocol::class.java).queryContactByIdImpl(mContactTable, uid)
            val newNum = DaoFactory.getProtocol(INewNumDaoProtocol::class.java).queryNewNumImpl(mNewNumTable, chatMessage.conversation)
            if (user == null) continue
            data.add(ConversationBean(chatMessage, user, newNum))
        }
        return data
    }

    private fun transformUserAndNewNum(chatMessage: ChatMessage): ConversationBean? {
        val locUid = SPUtils.getString(Constant.SPKey.KEY_UID)
        val uid = if (locUid == chatMessage.fromId) chatMessage.toId else chatMessage.fromId
        val user = DaoFactory.getProtocol(IContactProtocol::class.java).queryContactByIdImpl(mContactTable, uid)
        val newNum = DaoFactory.getProtocol(INewNumDaoProtocol::class.java).queryNewNumImpl(mNewNumTable, chatMessage.conversation)
        if (user == null) return null
        return ConversationBean(chatMessage, user, newNum)
    }

    override fun queryNewNumByConversation(conversation: String, data: MutableList<ConversationBean>) {
        DaoFactory.getProtocol(IChatHistoryDaoProtocol::class.java)
                .queryConversation(mConversationTable, conversation)
                .map { transformUserAndNewNum(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx(onNext = {
                    if (it == null) return@subscribeEx
                    handleConversation(it, data)
                }).bindRxLifeEx(RxLife.ON_DESTROY)
    }

    @Synchronized // 解决可能出现多条同一个会话bug
    private fun handleConversation(bean: ConversationBean, data: MutableList<ConversationBean>) {
        val index = indexOfConversation(bean.chatMessage.conversation, data)
        if (index == -1) {
            getMvpView().addConversationData(bean)
        } else {
            getMvpView().notifyAdapterPosition(index, bean)
        }
    }

    private fun indexOfConversation(conversation: String, data: MutableList<ConversationBean>): Int {
        var index = -1
        for ((i, bean) in data.withIndex()) {
            if (conversation == bean.chatMessage.conversation) {
                index = i
                break
            }
        }
        return index
    }


}
