package com.codebear.keyboard.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * description:
 * <p>
 * 参照w446108264提供的XhsEmoticonsKeyboard开源键盘解决方案
 * github:https://github.com/w446108264/XhsEmoticonsKeyboard
 * <p>
 * Created by CodeBear on 2017/6/28.
 */

public class EmoticonsKeyboardUtils {
    private static final String EXTRA_DEF_KEYBOARDHEIGHT = "DEF_KEYBOARDHEIGHT";
    private static final int DEF_KEYBOARD_HEAGH_WITH_DP = 240;
    private static int sDefKeyboardHeight = -1;

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getDisplayWidthPixels(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getFontHeight(TextView textView) {
        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public static int getDefKeyboardHeight(Context context) {
        if (sDefKeyboardHeight < 0) {
            sDefKeyboardHeight = dip2px(context, DEF_KEYBOARD_HEAGH_WITH_DP);
        }
        int height = PreferenceManager.getDefaultSharedPreferences(context).getInt(EXTRA_DEF_KEYBOARDHEIGHT, 0);
        return sDefKeyboardHeight = height > 0 && sDefKeyboardHeight != height ? height : sDefKeyboardHeight;
    }

    public static void setDefKeyboardHeight(Context context, int height) {
        if (sDefKeyboardHeight != height) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(EXTRA_DEF_KEYBOARDHEIGHT, height)
                    .apply();
            EmoticonsKeyboardUtils.sDefKeyboardHeight = height;
        }
    }

    public static boolean isFullScreen(final Activity activity) {
        return (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    /**
     * 开启软键盘
     *
     * @param et
     */
    public static void openSoftKeyboard(EditText et) {
        if (et != null) {
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
            et.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(et, 0);
        }
    }

    /**
     * 关闭软键盘
     *
     * @param context
     */
    public static void closeSoftKeyboard(Context context) {
        if (context == null || !(context instanceof Activity) || ((Activity) context).getCurrentFocus() == null) {
            return;
        }
        try {
            View view = ((Activity) context).getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            view.clearFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭软键盘
     * 当使用全屏主题的时候,XhsEmoticonsKeyBoard屏蔽了焦点.关闭软键盘时,直接指定 closeSoftKeyboard(EditView)
     *
     * @param view
     */
    public static void closeSoftKeyboard(View view) {
        if (view == null || view.getWindowToken() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    /**
     * 得到虚拟按键的高度
     *
     * @return 虚拟按键的高度
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getNavigationBarHeight(
            Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // 获取可用的高度
        DisplayMetrics defaultDisplayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(defaultDisplayMetrics);
        int usableHeight = defaultDisplayMetrics.heightPixels;

        // 获取实际的高度
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;

        return realHeight > usableHeight ? realHeight - usableHeight : 0;
    }
}
