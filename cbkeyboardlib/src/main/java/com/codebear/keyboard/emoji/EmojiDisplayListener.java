package com.codebear.keyboard.emoji;

import android.content.Context;
import android.text.Spannable;

/**
 * description:
 * <p>
 * 参照w446108264提供的XhsEmoticonsKeyboard开源键盘解决方案
 * github:https://github.com/w446108264/XhsEmoticonsKeyboard
 * <p>
 * Created by CodeBear on 2017/7/1.
 */

public interface EmojiDisplayListener {
    void onEmojiDisplay(Context var1, Spannable var2, String var3, int var4, int var5, int var6);
}
