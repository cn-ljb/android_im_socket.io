package com.codebear.keyboard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.codebear.keyboard.R;

/**
 * description:导航页ViewPager的指示器
 * <p>
 * Created by CodeBear on 2017/6/29.
 */

public class CBViewPagerIndicatorView extends View {
    private ViewPager mViewPager;

    /**
     * 显示的页数
     */
    private int pageCount = 1;
    /**
     * 未选择的点的画笔
     */
    private Paint unSelectPaint;
    /**
     * 选择的点的画笔
     */
    private Paint selectPaint;
    /**
     * 点的宽度
     */
    private float pointWidth = 30;
    /**
     * 点的高度
     */
    private float pointHeight = 30;
    /**
     * 画笔的宽度
     */
    private float pointStrokeWidth = 5;
    /**
     * 小圆点之间的距离
     */
    private float pointSpacing = 15;
    /**
     * 选中的点
     */
    private int selectPosition = 0;
    /**
     * 最后一页是否显示指示器
     */
    private boolean showInLastPage = true;
    /**
     * 未选中的点的颜色
     */
    private int unSelectColor = Color.WHITE;
    /**
     * 选中的点的颜色
     */
    private int selectColor = Color.WHITE;

    private int widthMeasureSpec;
    private int heightMeasureSpec;

    private Point[] points;

    private float x;
    private float y;

    public CBViewPagerIndicatorView(Context context) {
        this(context, null);
    }

    public CBViewPagerIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CBViewPagerIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (null == attrs) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CBViewPagerIndicatorView);

        pageCount = a.getInt(R.styleable.CBViewPagerIndicatorView_pageCount, 1);
        pointWidth = a.getDimension(R.styleable.CBViewPagerIndicatorView_pointWidth, 20);
        pointHeight = a.getDimension(R.styleable.CBViewPagerIndicatorView_pointHeight, 20);
        pointSpacing = a.getDimension(R.styleable.CBViewPagerIndicatorView_pointSpacing, 10);
        pointStrokeWidth = a.getDimension(R.styleable.CBViewPagerIndicatorView_pointStrokeWidth, 5);
        unSelectColor = a.getColor(R.styleable.CBViewPagerIndicatorView_unSelectColor, Color.WHITE);
        selectColor = a.getColor(R.styleable.CBViewPagerIndicatorView_selectColor, Color.WHITE);
        showInLastPage = a.getBoolean(R.styleable.CBViewPagerIndicatorView_showInLastPage, true);

        a.recycle();

    }

    private void init() {
        unSelectPaint = new Paint();
        selectPaint = new Paint();

        //初始化未选中的点的画笔
        unSelectPaint.setColor(unSelectColor);
        //设置抗锯齿
        unSelectPaint.setAntiAlias(true);
        //设置防抖动
        unSelectPaint.setDither(true);
        //设置为空心
        unSelectPaint.setStyle(Paint.Style.STROKE);
        //设置画笔宽度
        unSelectPaint.setStrokeWidth(pointStrokeWidth);

        //初始化选中的点的画笔
        selectPaint.setColor(selectColor);
        //设置抗锯齿
        selectPaint.setAntiAlias(true);
        //设置防抖动
        selectPaint.setDither(true);
        //设置为实心
        selectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //设置画笔宽度
        selectPaint.setStrokeWidth(pointStrokeWidth);
    }

    public void showIndicatorInLastPage(boolean isShow) {
        this.showInLastPage = isShow;
    }

    public void setUnSelectColor(int unSelectColor) {
        this.unSelectColor = unSelectColor;
        init();
        invalidate();
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
        init();
        invalidate();
    }

    public void setPointStrokeWidth(float pointStrokeWidth) {
        this.pointStrokeWidth = pointStrokeWidth;
        init();
        setPoints();
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        invalidate();
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
        setPoints();
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        invalidate();
    }

    public void setViewPager(ViewPager mViewPager) {
        if (mViewPager == null) {
            return;
        }
        setPageCount(mViewPager.getAdapter().getCount());
        this.mViewPager = mViewPager;
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition = position;
                if (selectPosition == pageCount - 1 && !showInLastPage) {
                    setVisibility(GONE);
                } else {
                    setVisibility(VISIBLE);
                }
                invalidate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setPoints() {
        points = new Point[pageCount];
        float x = pointWidth / 2 + pointStrokeWidth / 2;
        float y = pointHeight / 2 + pointStrokeWidth / 2;
        for (int i = 0; i < pageCount; ++i) {
            points[i] = new Point();
            points[i].set((int) x, (int) y);
            x += pointSpacing + pointWidth + pointStrokeWidth;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }


    private int measureWidth(int measureSpec) {
        float result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = pointWidth * pageCount + pointSpacing * (pageCount - 1) + pointStrokeWidth * pageCount;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return (int) result;
    }

    private int measureHeight(int measureSpec) {
        float result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = pointHeight + pointStrokeWidth * 2;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return (int) result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = pointWidth / 2 + pointStrokeWidth / 2;
        float y = pointHeight / 2 + pointStrokeWidth / 2;
        for (int i = 0; i < pageCount; ++i) {
            canvas.drawCircle(x, y, pointWidth / 2, i == selectPosition ? selectPaint : unSelectPaint);
            x += pointSpacing + pointWidth + pointStrokeWidth;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                return checkClick();
        }
        return false;
    }

    private boolean checkClick() {
        if(null != points) {
            for (int i = 0;i < points.length;++i) {
                Point p = points[i];
                float rx = p.x - x;
                float ry = p.y - y;
                if((rx * rx + ry * ry)  <= (pointWidth * pointWidth) * 1.3) {
                    if(i != selectPosition) {
                        mViewPager.setCurrentItem(i, true);
                    }
                    x = y = 0;
                    return true;
                }
            }
        }
        x = y = 0;
        return false;
    }
}

