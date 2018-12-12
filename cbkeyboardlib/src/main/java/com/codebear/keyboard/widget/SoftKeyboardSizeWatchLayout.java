package com.codebear.keyboard.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.codebear.keyboard.utils.EmoticonsKeyboardUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 监听软键盘是否弹出的RelativeLayout
 * <p>
 * 参照w446108264提供的XhsEmoticonsKeyboard开源键盘解决方案
 * github:https://github.com/w446108264/XhsEmoticonsKeyboard
 * <p>
 * Created by CodeBear on 2017/6/28.
 */

public class SoftKeyboardSizeWatchLayout extends RelativeLayout {
    private Context mContext;
    private int mOldh = -1;
    private int mNowh = -1;
    protected int mScreenHeight = 0;
    protected boolean mIsSoftKeyboardPop = false;
    protected boolean mIsNavBarPop = false;

    public SoftKeyboardSizeWatchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                if (mScreenHeight == 0) {
                    mScreenHeight = r.bottom;
                }
                mNowh = mScreenHeight - r.bottom;
                if (mOldh != -1 && mNowh != mOldh) {
                    if (mNowh > 0) {
                        int navHeight = EmoticonsKeyboardUtils.getNavigationBarHeight(mContext);
                        boolean isNav = false;
                        if (navHeight == mNowh) {
                            isNav = true;
                            mIsNavBarPop = true;
                        } else {
                            mIsSoftKeyboardPop = true;
                            mNowh -= navHeight;
                        }
                        if (!isNav) {
                            EmoticonsKeyboardUtils.setDefKeyboardHeight(mContext, mNowh);
                        }
                        if (mListenerList != null) {
                            for (OnResizeListener l : mListenerList) {
                                if (isNav) {
                                    l.onNavBarPop(mNowh);
                                } else {
                                    l.onSoftPop(mNowh);
                                    l.onNavBarPop(navHeight);
                                }
                            }
                        }
                    } else {
                        mIsSoftKeyboardPop = false;
                        mIsNavBarPop = false;
                        if (mListenerList != null) {
                            for (OnResizeListener l : mListenerList) {
                                l.onSoftClose();
                                l.onNavBarClose();
                            }
                        }
                    }
                }
                mOldh = mNowh;
            }
        });
    }

    public boolean isSoftKeyboardPop() {
        return mIsSoftKeyboardPop;
    }

    private List<OnResizeListener> mListenerList;

    public void addOnResizeListener(OnResizeListener l) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        mListenerList.add(l);
    }

    public interface OnResizeListener {
        /**
         * 软键盘弹起
         *
         * @param height 键盘高度
         */
        void onSoftPop(int height);

        /**
         * 软键盘关闭
         */
        void onSoftClose();

        /**
         * 底部导航栏打开
         *
         * @param height 底部虚拟按键高度
         */
        void onNavBarPop(int height);

        /**
         * 底部导航栏关闭
         */
        void onNavBarClose();
    }
}
