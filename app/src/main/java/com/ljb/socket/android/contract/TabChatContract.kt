package com.ljb.socket.android.contract

import com.ljb.page.PageStateLayout
import com.ljb.socket.android.model.ConversationBean
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * Author:Ljb
 * Time:2018/12/13
 * There is a lot of misery in life
 **/
interface TabChatContract {

    interface IView : IViewContract {
        fun setPage(state: PageStateLayout.PageState)
        fun setAllConversation(data: List<ConversationBean>)
        fun notifyAdapterPosition(position: Int, bean: ConversationBean)
        fun addConversationData(it: ConversationBean)
    }

    interface IPresenter : IPresenterContract {
        fun getAllConversation()
        fun queryNewNumByConversation(conversation: String, data: MutableList<ConversationBean>)
    }
}
