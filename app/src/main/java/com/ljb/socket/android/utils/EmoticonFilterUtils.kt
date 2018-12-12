package com.ljb.socket.android.utils

import android.text.SpannableStringBuilder
import android.widget.TextView
import com.codebear.keyboard.emoji.EmojiDisplay
import com.codebear.keyboard.emoji.ZsDisplay
import com.codebear.keyboard.utils.EmoticonsKeyboardUtils

/**
 * Author:Ljb
 * Time:2018/11/10
 * There is a lot of misery in life
 **/
object EmoticonFilterUtils {

    fun spannableEmoticonFilter(tv_content: TextView, content: String) {
        val spannableStringBuilder = SpannableStringBuilder(content)
        var spannable = EmojiDisplay.spannableFilter(tv_content.context,
                spannableStringBuilder,
                content,
                EmoticonsKeyboardUtils.getFontHeight(tv_content))

        spannable = ZsDisplay.spannableFilter(tv_content.context,
                spannable,
                content,
                EmoticonsKeyboardUtils.getFontHeight(tv_content),
                null)
        tv_content.text = spannable
    }
}
