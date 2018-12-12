package com.ljb.socket.android.chat

import com.lqr.audio.IAudioRecordListener

/**
 * Author:Ljb
 * Time:2018/11/14
 * There is a lot of misery in life
 **/

abstract class SimpleAudioRecordListener : IAudioRecordListener{

    override fun initTipView() {
    }

    override fun setTimeoutTipView(counter: Int) {
    }

    override fun setRecordingTipView() {
    }

    override fun setAudioShortTipView() {
    }

    override fun setCancelTipView() {
    }

    override fun destroyTipView() {
    }

    override fun onStartRecord() {
    }
}