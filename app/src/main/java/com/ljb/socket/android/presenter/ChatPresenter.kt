package com.ljb.socket.android.presenter

import android.widget.ImageView
import com.ljb.socket.android.common.ex.subscribeEx
import com.ljb.socket.android.contract.ChatContract
import com.ljb.socket.android.event.NewNumEvent
import com.ljb.socket.android.model.BodyImg
import com.ljb.socket.android.model.BodyTxt
import com.ljb.socket.android.model.BodyVoice
import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.presenter.base.BaseRxLifePresenter
import com.ljb.socket.android.protocol.dao.IImHistoryDaoProtocol
import com.ljb.socket.android.protocol.dao.IInitDaoProtocol
import com.ljb.socket.android.protocol.dao.INewNumDaoProtocol
import com.ljb.socket.android.socket.SocketEvent
import com.ljb.socket.android.socket.SocketManager
import com.ljb.socket.android.table.ImConversationTable
import com.ljb.socket.android.utils.ChatUtils
import com.ljb.socket.android.utils.FileUploadManager
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.RxUtils
import com.senyint.ihospital.user.kt.db.table.ImHistoryTable
import com.senyint.ihospital.user.kt.db.table.ImNewNumTable
import dao.ljb.kt.core.DaoFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mvp.ljb.kt.presenter.getContextEx
import net.ljb.kt.utils.NetLog
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Author:Ljb
 * Time:2018/12/11
 * There is a lot of misery in life
 **/
class ChatPresenter : BaseRxLifePresenter<ChatContract.IView>(), ChatContract.IPresenter {

    private var mTopic = ""

    private val mPageSize = 10
    private var mMsgCount = 0
    private var mLimitStart = 0
    private var mLimitEnd = 0
    protected lateinit var mHistoryTable: ImHistoryTable
    protected val mNewNumTable = ImNewNumTable()

    private val mFileUploadManager by lazy { FileUploadManager(getContextEx()) }


    override fun initChatData(conversation: String) {
        mHistoryTable = ImHistoryTable(conversation)
        DaoFactory.getProtocol(IInitDaoProtocol::class.java)
                .createTableNotExists(mHistoryTable)
                .flatMap { DaoFactory.getProtocol(IInitDaoProtocol::class.java).getCountForTable(mHistoryTable) }
                .map { mMsgCount = it }
                .filter { mMsgCount > 0 }
                .map { computeLimit() }
                .flatMap { DaoFactory.getProtocol(IImHistoryDaoProtocol::class.java).getChatHistory(mHistoryTable, mLimitStart, mLimitEnd) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx(onNext = { getMvpView().setChatHistory(false, it) })
                .bindRxLifeEx(RxLife.ON_DESTROY)
    }

    override fun getChatHistory() {
        if (mMsgCount <= 0) {
            getMvpView().setChatHistory(true, listOf())
        } else {
            computeLimit()
            DaoFactory.getProtocol(IImHistoryDaoProtocol::class.java)
                    .getChatHistory(mHistoryTable, mLimitStart, mLimitEnd)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeEx(onNext = { getMvpView().setChatHistory(true, it) })
                    .bindRxLifeEx(RxLife.ON_DESTROY)
        }
    }

    override fun gatAllChatPic(pid: String) {
        DaoFactory.getProtocol(IImHistoryDaoProtocol::class.java)
                .getImageChatHistory(mHistoryTable)
                .map { transformPicData(pid, it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx(onNext = {
                    getMvpView().openPhotoListPage(it.first, it.second)
                })
    }

    private fun transformPicData(pid: String, list: List<ChatMessage>): Pair<Int, ArrayList<String>> {
        val imgList = ArrayList<String>()
        var imgIndex = 0
        for ((i, imgMsg) in list.withIndex()) {
            val bodyImg = JsonParser.fromJsonObj(imgMsg.body, BodyImg::class.java)
            imgList.add(bodyImg.url)
            if (pid == imgMsg.pid) {
                imgIndex = i
            }
        }
        return Pair(imgIndex, imgList)
    }

    override fun setVoiceIsRead(chatMessage: ChatMessage) {
        DaoFactory.getProtocol(IImHistoryDaoProtocol::class.java)
                .setVoiceMsgRead(mHistoryTable, chatMessage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx()
                .bindRxLifeEx(RxLife.ON_DESTROY)
    }

    override fun downFile(voiceUrl: String, filePath: String, position: Int, animView: ImageView) {
        mFileUploadManager.downFile(voiceUrl, filePath, object : FileUploadManager.FileDownCallBack {
            override fun onError(e: Throwable) {
                NetLog.e(e)
            }

            override fun onSuccess(path: String) {
                getMvpView().playVoice(position, path, animView)
            }
        })
    }

    override fun notifyNewNum(conversation: String) {
        DaoFactory.getProtocol(INewNumDaoProtocol::class.java).deleteNewNum(mNewNumTable, conversation = conversation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx(onNext = { EventBus.getDefault().post(NewNumEvent(conversation)) })
                .bindRxLifeEx(RxLife.ON_DESTROY)
    }


    private fun computeLimit() {
        if (mMsgCount < mPageSize) {
            mLimitStart = 0
            mLimitEnd = mMsgCount
            mMsgCount = 0
        } else {
            mMsgCount -= mPageSize
            mLimitStart = mMsgCount
            mLimitEnd = mPageSize
        }
    }

    override fun setTopic(topic: String) {
        mTopic = topic
    }


    override fun sendTextMsg(text: String, fromId: String, toId: String) {
        val body = JsonParser.toJson(BodyTxt(text))
        val chatMessage = ChatUtils.createChatMessage(mTopic, fromId, toId, ChatMessage.MSG_BODY_TYPE_TEXT, body)
        getMvpView().addChatMessage2UI(chatMessage)
        updateHistoryAndConversation(chatMessage) { sendChatMsg(chatMessage) }
    }

    override fun sendImgMsg(path: String, fromId: String, toId: String) {
        val tempBody = JsonParser.toJson(BodyImg(path))
        val chatMessage = ChatUtils.createChatMessage(mTopic, fromId, toId, ChatMessage.MSG_BODY_TYPE_IMAGE, tempBody)
        getMvpView().addChatMessage2UI(chatMessage)
        mFileUploadManager.uploadImgFile(listOf(path), object : FileUploadManager.FileUploadCallBack {

            override fun onError(e: Throwable) {
                NetLog.e(e)
            }

            override fun onSuccess(urlList: List<String>) {
                if (urlList.isEmpty()) return
                val body = JsonParser.toJson(BodyImg(urlList[0]))
                chatMessage.body = body
                updateHistoryAndConversation(chatMessage) { sendChatMsg(chatMessage) }
            }
        })
    }

    override fun sendMp3Msg(path: String, time: Long, fromId: String, toId: String) {
        val tempBody = JsonParser.toJson(BodyVoice(path, time.toString()))
        val chatMessage = ChatUtils.createChatMessage(mTopic, fromId, toId, ChatMessage.MSG_BODY_TYPE_VOICE, tempBody)
        getMvpView().addChatMessage2UI(chatMessage)
        mFileUploadManager.uploadVideoFile(path, object : FileUploadManager.FileUploadCallBack {

            override fun onError(e: Throwable) {
                NetLog.e(e)
            }

            override fun onSuccess(urlList: List<String>) {
                if (urlList.isEmpty()) return
                val body = JsonParser.toJson(BodyVoice(urlList[0], time.toString()))
                chatMessage.body = body
                updateHistoryAndConversation(chatMessage) { sendChatMsg(chatMessage) }
            }
        })
    }

    protected fun updateHistoryAndConversation(chatMsg: ChatMessage, next: (() -> Unit)? = null) {
        DaoFactory.getProtocol(IImHistoryDaoProtocol::class.java).insertHistory(mHistoryTable, chatMsg)
                .flatMap { DaoFactory.getProtocol(IImHistoryDaoProtocol::class.java).insertConversation(ImConversationTable(), chatMsg) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeEx(onNext = { next?.invoke() })
                .bindRxLifeEx(RxLife.ON_DESTROY)
    }


    private fun sendChatMsg(chatMessage: ChatMessage) {
        val json = JsonParser.toJson(chatMessage)
        SocketManager.sendMsg(getContextEx(), SocketEvent.EVENT_CHAT, json, object : SocketManager.RequestCallBack {
            override fun call(msg: String) {
                getMvpView().notifyChatMessageStatus(chatMessage, ChatMessage.MSG_STATUS_SEND_SUCCESS)
                updateHistoryAndConversation(chatMessage)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        mFileUploadManager.release()
    }
}

