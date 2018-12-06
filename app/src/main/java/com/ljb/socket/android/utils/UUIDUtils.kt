package com.ljb.socket.android.utils

import java.util.*

/**
 * Author:Ljb
 * Time:2018/12/6
 * There is a lot of misery in life
 **/
object UUIDUtils {

    private val chars = arrayOf("a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z")

    fun getUUID8(): String {
        val shortBuffer = StringBuffer()
        val uuid = UUID.randomUUID().toString().replace("-", "")
        for (i in 0..7) {
            val str = uuid.substring(i * 4, i * 4 + 4)
            val x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString()
    }

}
