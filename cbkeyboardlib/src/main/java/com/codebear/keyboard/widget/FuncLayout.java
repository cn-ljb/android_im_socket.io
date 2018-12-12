package com.codebear.keyboard.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.codebear.keyboard.utils.EmoticonsKeyboardUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description:功能布局
 * <p>
 * 参照w446108264提供的XhsEmoticonsKeyboard开源键盘解决方案
 * github:https://github.com/w446108264/XhsEmoticonsKeyboard
 * <p>
 * Created by CodeBear on 2017/6/28.
 */

public class FuncLayout extends LinearLayout {
    public final int DEF_KEY = Integer.MIN_VALUE;

    private final SparseArray<View> mFuncViewArrayMap = new SparseArray<>();

    private int mCurrentFuncKey = DEF_KEY;

    protected int mHeight = 0;
    protected int navHeight = 0;

    public FuncLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void addFuncView(int key, View view) {
        if (mFuncViewArrayMap.get(key) != null) {
            mFuncViewArrayMap.delete(key);
        }
        mFuncViewArrayMap.put(key, view);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        addView(view, params);
        view.setVisibility(GONE);
    }

    public void hideAllFuncView() {
        mCurrentFuncKey = DEF_KEY;
        setVisibility(false);
        for (int i = 0; i < mFuncViewArrayMap.size(); i++) {
            int keyTemp = mFuncViewArrayMap.keyAt(i);
            mFuncViewArrayMap.get(keyTemp).setVisibility(GONE);
        }
    }

    public void toggleFuncView(int key, boolean isSoftKeyboardPop, EditText editText) {
        if (getCurrentFuncKey() == key) {
            if (isSoftKeyboardPop) {
                if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
                    EmoticonsKeyboardUtils.closeSoftKeyboard(editText);
                } else {
                    EmoticonsKeyboardUtils.closeSoftKeyboard(getContext());
                }
            } else {
                EmoticonsKeyboardUtils.openSoftKeyboard(editText);
            }
        } else {
            if (isSoftKeyboardPop) {
                if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
                    EmoticonsKeyboardUtils.closeSoftKeyboard(editText);
                } else {
                    EmoticonsKeyboardUtils.closeSoftKeyboard(getContext());
                }
            }
            showFuncView(key);
        }
    }

    public void showFuncView(int key) {
        if (mFuncViewArrayMap.get(key) == null) {
            return;
        }
        for (int i = 0; i < mFuncViewArrayMap.size(); i++) {
            int keyTemp = mFuncViewArrayMap.keyAt(i);
            if (keyTemp == key) {
                mFuncViewArrayMap.get(keyTemp).setVisibility(VISIBLE);
            } else {
                mFuncViewArrayMap.get(keyTemp).setVisibility(GONE);
            }
        }
        mCurrentFuncKey = key;
        setVisibility(true);

        if (onFuncChangeListener != null) {
            onFuncChangeListener.onFuncChange(mCurrentFuncKey);
        }
    }

    public int getCurrentFuncKey() {
        return mCurrentFuncKey;
    }

    public void updateHeight(int height) {
        this.mHeight = height;
    }

    public void updateNavHeight(int navHeight) {
        this.navHeight = navHeight;
        setVisibility(visibility);
    }

    private boolean visibility = false;

    public void setVisibility(boolean b) {
        visibility = b;
        LayoutParams params = (LayoutParams) getLayoutParams();
        if (b) {
            params.height = mHeight;
            if (mListenerList != null) {
                for (OnFuncKeyBoardListener l : mListenerList) {
                    l.onFuncPop(mHeight + navHeight);
                }
            }
        } else {
            params.height = 0;
            if (mListenerList != null) {
                for (OnFuncKeyBoardListener l : mListenerList) {
                    l.onFuncClose();
                }
            }
        }
        params.bottomMargin = navHeight;
        setLayoutParams(params);
    }

    public boolean isOnlyShowSoftKeyboard() {
        return mCurrentFuncKey == DEF_KEY;
    }

    private List<OnFuncKeyBoardListener> mListenerList;

    public void addOnKeyBoardListener(OnFuncKeyBoardListener l) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        mListenerList.add(l);
    }

    public interface OnFuncKeyBoardListener {
        /**
         * 功能布局弹起
         *
         * @param height 弹起的高度(包括底部导航栏高度)
         */
        void onFuncPop(int height);

        /**
         * 功能布局关闭
         */
        void onFuncClose();
    }

    private OnFuncChangeListener onFuncChangeListener;

    public interface OnFuncChangeListener {
        void onFuncChange(int key);
    }

    public void setOnFuncChangeListener(OnFuncChangeListener listener) {
        this.onFuncChangeListener = listener;
    }
}
