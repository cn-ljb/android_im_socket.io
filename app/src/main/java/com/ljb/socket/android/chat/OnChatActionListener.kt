package com.ljb.socket.android.chat

import android.widget.ImageView

/**
 * Author:Ljb
 * Time:2018/10/17
 * There is a lot of misery in life
 **/

interface OnChatActionListener {

    fun onChatHeadImgClick(position: Int, imageView: ImageView)

    fun onChatVoiceClick(position: Int, voiceUrl: String, animView: ImageView)

    fun onChatImageClick(position: Int , imageView: ImageView)

}