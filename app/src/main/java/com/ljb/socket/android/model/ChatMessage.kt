package com.ljb.socket.android.model

class ChatMessage {

    companion object {

        //聊天类型
        const val TYPE_CHAT = 1
        const val TYPE_CHAT_GROUP = 2
        const val TYPE_CMD = 3

        //聊天相关cmd
        const val CMD_RECEIVE_ACK = 1
        const val CMD_CONTACT_LIST = 2

        //消息状态
        const val MSG_STATUS_SEND_ING = 0
        const val MSG_STATUS_SEND_SUCCESS = 1
        const val MSG_STATUS_SEND_ERROR = -1

        //语音是否已读
        const val MSG_VOICE_NOT_READ = 0
        const val MSG_VOICE_HAS_READ = 1

        //消息体类型
        const val MSG_BODY_TYPE_TEXT = 0 // 文本
        const val MSG_BODY_TYPE_IMAGE = 1 // 图片
        const val MSG_BODY_TYPE_VOICE = 2 // 语音

        //设备类型
        const val DEV_ANDROID = 1
        const val DEV_IOS = 2
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
    var read: Int = MSG_VOICE_NOT_READ
}