package com.codebear.keyboard.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.codebear.keyboard.R;
import com.codebear.keyboard.utils.EmoticonsKeyboardUtils;

/**
 * description:
 * <p>
 * 参照w446108264提供的XhsEmoticonsKeyboard开源键盘解决方案
 * github:https://github.com/w446108264/XhsEmoticonsKeyboard
 * <p>
 * Created by CodeBear on 2017/6/28.
 */

public abstract class AutoHeightLayout extends SoftKeyboardSizeWatchLayout implements SoftKeyboardSizeWatchLayout
        .OnResizeListener {

    private static final int ID_CHILD = R.id.id_autolayout;

    protected int mSoftKeyboardHeight;
    protected int mMaxParentHeight;
    protected Context mContext;
    protected boolean mConfigurationChangedFlag = false;

    public AutoHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mSoftKeyboardHeight = EmoticonsKeyboardUtils.getDefKeyboardHeight(mContext);
        addOnResizeListener(this);
    }

    @SuppressLint("ResourceType")
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int childSum = getChildCount();
        if (childSum > 1) {
            throw new IllegalStateException("can host only one direct child");
        }
        super.addView(child, index, params);
        if (childSum == 0) {
            if (child.getId() < 0) {
                child.setId(ID_CHILD);
            }
            LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(ALIGN_PARENT_BOTTOM);
            child.setLayoutParams(paramsChild);
        } else if (childSum == 1) {
            LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(ABOVE, ID_CHILD);
            child.setLayoutParams(paramsChild);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onSoftKeyboardHeightChanged(mSoftKeyboardHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mMaxParentHeight == 0) {
            mMaxParentHeight = h;
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mConfigurationChangedFlag = true;
        mScreenHeight = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mConfigurationChangedFlag) {
            mConfigurationChangedFlag = false;
            Rect r = new Rect();
            ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            if (mScreenHeight == 0) {
                mScreenHeight = r.bottom;
            }
            mMaxParentHeight = mScreenHeight - r.bottom;
        }

        if (mMaxParentHeight != 0) {
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int expandSpec = MeasureSpec.makeMeasureSpec(mMaxParentHeight, heightMode);
            super.onMeasure(widthMeasureSpec, expandSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onSoftPop(int height) {
        if (mSoftKeyboardHeight != height) {
            mSoftKeyboardHeight = height;
            onSoftKeyboardHeightChanged(mSoftKeyboardHeight);
        }
    }

    @Override
    public void onSoftClose() {
    }

    @Override
    public void onNavBarPop(int height) {

    }

    @Override
    public void onNavBarClose() {

    }

    public abstract void onSoftKeyboardHeightChanged(int height);

}
