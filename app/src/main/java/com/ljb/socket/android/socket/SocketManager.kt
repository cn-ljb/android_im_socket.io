package com.ljb.socket.android.socket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.ljb.socket.android.BuildConfig
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.event.ChatNewEvent
import com.ljb.socket.android.event.NewNumEvent
import com.ljb.socket.android.event.RefreshContactListEvent
import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.protocol.dao.IContactListProtocol
import com.ljb.socket.android.protocol.dao.IImHistoryDaoProtocol
import com.ljb.socket.android.protocol.dao.IInitDaoProtocol
import com.ljb.socket.android.protocol.dao.INewNumDaoProtocol
import com.ljb.socket.android.table.ContactTable
import com.ljb.socket.android.table.ImConversationTable
import com.ljb.socket.android.utils.ChatUtils
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.RxUtils
import com.ljb.socket.android.utils.SPUtils
import com.senyint.ihospital.user.kt.db.table.ImHistoryTable
import com.senyint.ihospital.user.kt.db.table.ImNewNumTable
import dao.ljb.kt.core.DaoFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.ljb.kt.utils.NetLog
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * 对外开放的Socket通讯类
 * Author:Ljb
 * Time:2018/9/6
 * There is a lot of misery in life
 **/
object SocketManager {

    private val mRequestCallMap = HashMap<String, RequestCallBack?>()
    private var mRequestCallReceiver: RequestChatMsgCallReceiver? = null
    private var mResponseCallReceiver: ResponseChatMsgCallReceiver? = null
    private val mRxLife by lazy { ArrayList<WeakReference<Disposable>>() }

    /**
     * 获取当前用户的Socket Token
     * */
    fun getSocketToken(uid: String, name: String, headImg: String) = "uid=$uid&name=$name&headImg=$headImg"

    /**
     * 登录Socket
     * */
    fun loginSocket(context: Context, token: String) {
        val appContext = context.applicationContext
        // 开启Socket
        val intent = Intent(context, SocketService::class.java)
        intent.putExtra(SocketService.CMD, SocketService.CMD_INIT_SOCKET)
        intent.putExtra(SocketService.DATA, token)
        startService(appContext, intent)

        // 注册发送消息广播
        if (mRequestCallReceiver == null) {
            mRequestCallReceiver = RequestChatMsgCallReceiver()
            val reqIntentFilter = IntentFilter(RequestChatMsgCallReceiver.ACTION)
            appContext.registerReceiver(mRequestCallReceiver, reqIntentFilter)
            mRequestCallMap.clear()
        }

        // 注册接收消息广播
        if (mResponseCallReceiver == null) {
            mResponseCallReceiver = ResponseChatMsgCallReceiver()
            val respIntentFilter = IntentFilter(ResponseChatMsgCallReceiver.ACTION)
            respIntentFilter.addAction(ResponseChatMsgCallReceiver.ACTION_CONFLICT)
            respIntentFilter.addAction(ResponseChatMsgCallReceiver.ACTION_CHAT)
            appContext.registerReceiver(mResponseCallReceiver, respIntentFilter)
        }
    }


    /**
     * 退出Socket
     * */
    fun logoutSocket(context: Context) {
        val appContext = context.applicationContext
        // 关闭Socket
        val intent = Intent(context, SocketService::class.java)
        intent.putExtra(SocketService.CMD, SocketService.CMD_RELEASE_SOCKET)
        startService(appContext, intent)

        // 注销发送消息广播
        if (mRequestCallReceiver != null) {
            appContext.unregisterReceiver(mRequestCallReceiver)
            mRequestCallMap.clear()
            mRequestCallReceiver = null
        }

        // 注销发送消息广播
        if (mResponseCallReceiver != null) {
            appContext.unregisterReceiver(mResponseCallReceiver)
            mResponseCallReceiver = null
        }
        clearRxLife()
    }

    private fun clearRxLife() {
        mRxLife.map { RxUtils.dispose(it.get()) }
        mRxLife.clear()
    }


    private fun startService(appContext: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }

    /**
     * 发送消息
     * */
    fun sendMsg(context: Context, event: String, msg: String, call: RequestCallBack? = null) {
        try {
            val jsonObject = JSONObject(msg)
            if (jsonObject.has(RequestChatMsgCallReceiver.MSG_ID)) {
                val msgId = jsonObject.getString(RequestChatMsgCallReceiver.MSG_ID)
                val intent = Intent(context, SocketService::class.java)
                intent.putExtra(SocketService.CMD, SocketService.CMD_SEND_MSG)
                intent.putExtra(SocketService.MSG, msg)
                intent.putExtra(SocketService.MSG_EVENT, event)
                intent.putExtra(SocketService.MSG_ID, msgId)
                startService(context, intent)
                mRequestCallMap[msgId] = call
            } else {
                Log.e(SocketService.TAG, "消息：缺少消息pid -> $msg")
            }
        } catch (e: Exception) {
            Log.e(SocketService.TAG, "消息：非法格式 -> $msg")
        }
    }

    /**
     * 发送已接收消息响应
     * */
    private fun sendAsk(context: Context, event: String, msg: String) {
        val intent = Intent(context, SocketService::class.java)
        intent.putExtra(SocketService.CMD, SocketService.CMD_SEND_ASK)
        intent.putExtra(SocketService.MSG, msg)
        intent.putExtra(SocketService.MSG_EVENT, event)
        startService(context, intent)
    }

    fun clearCallBack() {
        if (mRequestCallMap.size != 0) {
            mRequestCallMap.clear()
        }
    }

    /**
     * 发送消息的CallBack，配合软引用，避免内存泄漏
     * */
    interface RequestCallBack {
        fun call(msg: String)
    }

    /**
     * 发送消息的CallBack处理广播
     * */
    class RequestChatMsgCallReceiver : BroadcastReceiver() {

        companion object {
            const val ACTION = "${BuildConfig.APPLICATION_ID}.ACTION_CHAT_MSG_REQUEST"
            const val MSG_ID = "pid"
            const val RESULT = "callResult"
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION != intent?.action) return
            val msgId = intent.getStringExtra(MSG_ID)
            val result = intent.getStringExtra(RESULT)

            mRequestCallMap[msgId]?.let {
                Log.i(SocketService.TAG, "Socket 发送成功后，回调给UI")
                it.call(result)
            }
            mRequestCallMap.remove(msgId)
        }

    }

    /**
     * 接收消息的CallBack广播
     * */
    class ResponseChatMsgCallReceiver : BroadcastReceiver() {

        companion object {
            const val ACTION = "${BuildConfig.APPLICATION_ID}.ACTION_CHAT_MSG_RESPONSE"
            const val ACTION_CONFLICT = "${BuildConfig.APPLICATION_ID}.ACTION_CHAT_MSG_RESPONSE_CONFLICT"
            const val ACTION_CHAT = "${BuildConfig.APPLICATION_ID}.ACTION_CHAT_MSG_RESPONSE_CHAT"
            const val RESULT = "callResult"
        }

        override fun onReceive(context: Context, intent: Intent?) {
            if (TextUtils.isEmpty(intent?.action)) return
            val type = intent!!.action
            val result = intent.getStringExtra(RESULT)
            when (type) {
                ACTION_CONFLICT -> responseConflict(context, result)
                ACTION_CHAT -> responseChat(context, result)
            }
        }
    }

    /**
     * 聊天响应
     * */
    fun responseChat(context: Context, result: String?) {
        if (TextUtils.isEmpty(result)) return
        val chatMessage = JsonParser.fromJsonObj(result!!, ChatMessage::class.java)
        when (chatMessage.type) {
            ChatMessage.TYPE_CMD -> handleCmdChatMessage(chatMessage)
            ChatMessage.TYPE_CHAT -> handleImChatMessage(context, chatMessage)
        }
    }

    /**
     * 聊天信息
     * */
    private fun handleImChatMessage(context: Context, chatMessage: ChatMessage) {
        //回调服务器端，已接收到消息
        sendAsk(context, SocketEvent.EVENT_CHAT, ChatUtils.getAck(chatMessage))
        val conversation = ChatUtils.createConversation(chatMessage.topic, chatMessage.fromId, chatMessage.toId)
        val historyTable = ImHistoryTable(conversation)
        val newNumTable = ImNewNumTable()
        val conversationTable = ImConversationTable()
        val subscribe = DaoFactory.getProtocol(IImHistoryDaoProtocol::class.java).insertConversation(conversationTable, chatMessage)
                .flatMap { DaoFactory.getProtocol(IInitDaoProtocol::class.java).createTableNotExists(historyTable) }
                .flatMap { DaoFactory.getProtocol(IImHistoryDaoProtocol::class.java).insertHistory(historyTable, chatMessage) }
                .flatMap { DaoFactory.getProtocol(INewNumDaoProtocol::class.java).insertNewNum(newNumTable, 1, chatMessage.fromId, chatMessage.conversation) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sendChatEvent(chatMessage)
//                    SocketNotificationManager.showNotificationMessage(context, chatMessage)
                }, { NetLog.e(it) })
        mRxLife.add(WeakReference(subscribe))
    }

    private fun sendChatEvent(chatMessage: ChatMessage) {
        EventBus.getDefault().post(NewNumEvent(chatMessage.conversation)) //刷新消息计数
        EventBus.getDefault().post(ChatNewEvent(chatMessage))  //通知聊天页面
    }

    /**
     * 命令类型消息处理
     * */
    private fun handleCmdChatMessage(chatMessage: ChatMessage) {
        when (chatMessage.cmd) {
            ChatMessage.CMD_CONTACT_LIST -> handleContactList(chatMessage)
        }
    }

    private fun handleContactList(chatMessage: ChatMessage) {
        val table = ContactTable()
        val body = chatMessage.body
        val contactList = JsonParser.fromJsonArr(body, UserBean::class.java)
        val disposable = DaoFactory.getProtocol(IInitDaoProtocol::class.java).createTableNotExists(table)
                .map { transformContactList(contactList) }
                .flatMap { DaoFactory.getProtocol(IContactListProtocol::class.java).insertContactList(table, it) }
                .filter { it }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    EventBus.getDefault().post(RefreshContactListEvent())
                }, {
                    NetLog.e(it)
                })
        mRxLife.add(WeakReference(disposable))
    }

    private fun transformContactList(contactList: MutableList<UserBean>): MutableList<UserBean> {
        var index = -1
        val uid = SPUtils.getString(Constant.SPKey.KEY_UID)
        for ((i, item) in contactList.withIndex()) {
            if (item.uid == uid) {
                index = i
                break
            }
        }
        if (index != -1) {
            contactList.removeAt(index)
        }
        return contactList
    }


    /**
     * 用户冲突响应
     * */
    private fun responseConflict(context: Context, result: String?) {
        Toast.makeText(context.applicationContext, "您的账号在另一台设备登陆", Toast.LENGTH_SHORT).show()
        //TODO 待完善
    }


}
