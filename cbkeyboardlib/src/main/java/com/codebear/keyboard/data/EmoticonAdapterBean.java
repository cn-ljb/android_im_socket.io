package com.codebear.keyboard.data;

import com.codebear.keyboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class EmoticonAdapterBean {
    protected int rol = 7;
    protected int row = 3;
    protected int page = 0;
    protected boolean showName = false;

    protected double mItemHeightMaxRatio = 2;
    protected int mItemHeight = R.dimen.item_emoticon_size_default;

    protected List<EmoticonsBean> mData = new ArrayList<>();

    public EmoticonAdapterBean(List<EmoticonsBean> mData) {
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

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
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

    public List<EmoticonsBean> getmData() {
        return mData;
    }

    public void setmData(List<EmoticonsBean> mData) {
        this.mData = mData;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
