package com.codebear.keyboard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * description:用不能滑动的gridView来显示表情集
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class EmoticonGridView extends GridView {
    public EmoticonGridView(Context context) {
        super(context);
    }

    public EmoticonGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmoticonGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
