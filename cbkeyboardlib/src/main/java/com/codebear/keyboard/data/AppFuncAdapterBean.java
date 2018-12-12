package com.codebear.keyboard.data;

import com.codebear.keyboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class AppFuncAdapterBean {
    protected int rol = 4;
    protected int row = 2;
    protected int page = 0;

    protected double mItemHeightMaxRatio = 1.6;
    protected int mItemHeight = R.dimen.item_big_emoticon_size_default;

    protected List<AppFuncBean> mData = new ArrayList<>();

    public AppFuncAdapterBean(List<AppFuncBean> mData) {
        this.mData.addAll(mData);
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public double getmItemHeightMaxRatio() {
        return mItemHeightMaxRatio;
    }

    public void setmItemHeightMaxRatio(double mItemHeightMaxRatio) {
        this.mItemHeightMaxRatio = mItemHeightMaxRatio;
    }

    public int getmItemHeight() {
        return mItemHeight;
    }

    public void setmItemHeight(int mItemHeight) {
        this.mItemHeight = mItemHeight;
    }

    public List<AppFuncBean> getmData() {
        return mData;
    }

    public void setmData(List<AppFuncBean> mData) {
        this.mData = mData;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
