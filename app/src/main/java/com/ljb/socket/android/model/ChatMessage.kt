package com.ljb.socket.android.model

class ChatMessage {

    companion object {
        //event
        const val EVENT_CHAT = "chat"

        //消息状态
        const val MSG_STATUS_SEND_ING = 0
        const val MSG_STATUS_SEND_SUCCESS = 1
        const val MSG_STATUS_SEND_ERROR = -1

        //消息类型
        const val TYPE_CHAT = 1
        const val TYPE_CHAT_GROUP = 2
        const val TYPE_CMD = 3

        //聊天相关cmd
        const val CMD_RECEIVE_ACK = 1
    }

    var fromId: String = ""
    var toId: String = ""
    var topic: String = ""
    var conversation: String = ""
    var pid: String = ""
    var dev: Int = 0
    var type: Int = 0
    var cmd: Int = 0
    var body: String = ""
    var bodyType: Int = 0
    var time: Long = 0L
    var status: Int = MSG_STATUS_SEND_ING
}