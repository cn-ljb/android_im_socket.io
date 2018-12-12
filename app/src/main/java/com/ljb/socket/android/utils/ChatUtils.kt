package com.ljb.socket.android.utils

import com.ljb.socket.android.model.ChatMessage
import java.util.*


object ChatUtils {

    /**
     * 生成消息唯一标识
     * */
    fun getPid(): String {
        val stringBuffer = StringBuilder()
        val time = System.currentTimeMillis().toString()
        stringBuffer.append("AN")
                .append(getRandomInt())
                .append(getRandomString())
                .append(getRandomInt())
                .append(getRandomString())
                .append(getRandomInt())
                .append(time.substring(time.length - 4, time.length))
        return stringBuffer.toString()
    }

    /**
     * 随机产生一个4个字节的int
     */
    fun getRandomInt(): Int {
        val min = 10
        val max = 99
        val random = Random()
        return random.nextInt(max - min + 1) + min
    }

    /**
     * 随机产生字符串
     */
    fun getRandomString(): String {
        val str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        val sf = StringBuffer()
        for (i in 0..1) {
            val number = random.nextInt(62)// 0~61
            sf.append(str[number])

        }
        return sf.toString()
    }


    /**
     * 生成会话
     */
    fun createConversation(topic: String, fromUid: String, toUid: String): String {
        val str = arrayOf(fromUid, toUid, topic)
        Arrays.sort(str)
        val buf = StringBuilder()
        for (s in str) {
            buf.append(s)
        }
        return EncodeMD5.hash(buf.toString())
    }

    fun createChatMessage(topic: String, fromId: String, toId: String, bodyType: Int, body: String): ChatMessage {
        val chatMessage = ChatMessage()
        chatMessage.fromId = fromId
        chatMessage.toId = toId
        chatMessage.topic = topic
        chatMessage.conversation = createConversation(topic, fromId, toId)
        chatMessage.pid = getPid()
        chatMessage.type = ChatMessage.TYPE_CHAT
        chatMessage.bodyType = bodyType
        chatMessage.body = body
        chatMessage.status = ChatMessage.MSG_STATUS_SEND_ING
        chatMessage.time = System.currentTimeMillis()
        chatMessage.dev = ChatMessage.DEV_ANDROID
        return chatMessage
    }

    fun getAck(chatMessage: ChatMessage): String {
        chatMessage.type = ChatMessage.TYPE_CMD
        chatMessage.cmd = ChatMessage.CMD_RECEIVE_ACK
        return JsonParser.toJson(chatMessage)
    }


}