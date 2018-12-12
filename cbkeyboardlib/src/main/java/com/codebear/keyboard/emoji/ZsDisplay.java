package com.codebear.keyboard.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LiuZhe on 2016/6/6.
 */
public class ZsDisplay {


    public static final int WRAP_DRAWABLE = -1;
    public static final Pattern ZS_RANGE = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]");

    public static Matcher getMatcher(CharSequence matchStr) {
        return ZS_RANGE.matcher(matchStr);
    }

    public static Spannable spannableFilter(Context context, Spannable spannable, CharSequence text, int fontSize) {
        return spannableFilter(context, spannable, text, fontSize, null);
    }

    public static Spannable spannableFilter(Context context, Spannable spannable, CharSequence text, int fontSize, EmojiDisplayListener emojiDisplayListener) {
        Matcher m = getMatcher(text);
        if (m != null) {
            while (m.find()) {
                String str = m.group();
                if(null == DefEmoticons.emojiMap.get(str)) {
                    continue;
                }
                int emojiHex = DefEmoticons.emojiMap.get(str);
                if (emojiDisplayListener == null) {
                    emojiDisplay(context, spannable, emojiHex, fontSize, m.start(), m.end());
                } else {
//                    emojiDisplayListener.onEmojiDisplay(context, spannable, emojiHex, fontSize, m.start(), m.end());
                }
            }
        }
        return spannable;
    }

    public static void emojiDisplay(Context context, Spannable spannable, int zsEmojiId, int fontSize, int start, int end) {
        Drawable drawable = getDrawable(context, zsEmojiId);
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

    public static Drawable getDrawable(Context context, int resID) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return context.getResources().getDrawable(resID, null);
            } else {
                return context.getResources().getDrawable(resID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
