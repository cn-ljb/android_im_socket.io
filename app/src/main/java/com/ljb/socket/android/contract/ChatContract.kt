package com.ljb.socket.android.contract

import android.widget.ImageView
import com.ljb.socket.android.model.ChatMessage
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * Author:Ljb
 * Time:2018/12/11
 * There is a lot of misery in life
 **/
interface ChatContract {

    interface IView : IViewContract {
        fun setChatHistory(isLoadMore: Boolean, data: List<ChatMessage>)
        fun addChatMessage2UI(chatMessage: ChatMessage)
        fun notifyChatMessageStatus(chatMessage: ChatMessage, status: Int)
        fun openPhotoListPage(index: Int, picList: ArrayList<String>)
        fun playVoice(position: Int, path: String, animView: ImageView)
    }


    interface IPresenter : IPresenterContract {
        fun setTopic(topic: String)
        fun initChatData(conversation: String)
        fun getChatHistory()
        fun sendTextMsg(text: String, fromId: String, toId: String)
        fun sendImgMsg(path: String, fromId: String, toId: String)
        fun sendMp3Msg(path: String, time: Long, fromId: String, toId: String)
        fun gatAllChatPic(pic: String)
        fun setVoiceIsRead(chatMessage: ChatMessage)
        fun downFile(voiceUrl: String, filePath: String, position: Int, animView: ImageView)
        fun notifyNewNum(conversation: String)
    }
}
