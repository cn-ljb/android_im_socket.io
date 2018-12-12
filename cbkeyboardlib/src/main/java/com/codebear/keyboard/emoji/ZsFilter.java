package com.codebear.keyboard.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.EditText;


import com.codebear.keyboard.interfaces.EmoticonFilter;
import com.codebear.keyboard.utils.EmoticonsKeyboardUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LiuZhe on 2016/6/3.
 */
public class ZsFilter extends EmoticonFilter {
    public static final int WRAP_DRAWABLE = -1;
    private int emoticonSize = -1;
    public static final Pattern ZS_RANGE = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]");

    public static Matcher getMatcher(CharSequence matchStr) {
        return ZS_RANGE.matcher(matchStr);
    }

    @Override
    public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
        emoticonSize = emoticonSize == -1 ? EmoticonsKeyboardUtils.getFontHeight(editText) : emoticonSize;
        clearSpan(editText.getText(), start, text.toString().length());
        Matcher m = getMatcher(text.toString().substring(start, text.toString().length()));
        if (m != null) {
            while (m.find()) {
                try {
                    String emoji = m.group();
                    if (!TextUtils.isEmpty(emoji)) {
                        int icon = DefEmoticons.emojiMap.get(emoji);
                        emoticonDisplay(editText.getContext(), editText.getText(), icon, emoticonSize, start + m.start(), start + m.end());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return;
//                    emoticonDisplay(editText.getContext(), editText.getText(), -1, emoticonSize, start + m.start(), start + m.end());
                }

            }
        }
    }

    private void clearSpan(Spannable spannable, int start, int end) {
        if (start == end) {
            return;
        }
        EmojiSpan[] oldSpans = spannable.getSpans(start, end, EmojiSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            spannable.removeSpan(oldSpans[i]);
        }
    }

    public static void emoticonDisplay(Context context, Spannable spannable, int drawableId, int fontSize, int start, int end) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        if (drawable != null) {
            int itemHeight;
            int itemWidth;
            if (fontSize == WRAP_DRAWABLE) {
                itemHeight = drawable.getIntrinsicHeight();
                itemWidth = drawable.getIntrinsicWidth();
            } else {
                itemHeight = fontSize;
                itemWidth = fontSize;
            }

            drawable.setBounds(0, 0, itemHeight, itemWidth);
            EmojiSpan imageSpan = new EmojiSpan(drawable);
            spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }
}
