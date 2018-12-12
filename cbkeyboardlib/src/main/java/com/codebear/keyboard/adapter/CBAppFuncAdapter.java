package com.codebear.keyboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codebear.keyboard.R;
import com.codebear.keyboard.data.AppFuncAdapterBean;
import com.codebear.keyboard.data.AppFuncBean;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class CBAppFuncAdapter extends BaseAdapter {

    public interface OnItemClickListener {
        void onItemClick(AppFuncBean data, int position, int page);
    }

    private Context mContext;
    private AppFuncAdapterBean mData;

    private double mItemHeightMaxRatio;
    private int mItemHeightMax;
    private int mItemHeightMin;
    private int mItemHeight;

    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CBAppFuncAdapter(Context mContext, AppFuncAdapterBean mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mItemHeightMaxRatio = mData.getmItemHeightMaxRatio();
        this.mItemHeight = (int) mContext.getResources().getDimension(mData.getmItemHeight());
    }

    @Override
    public int getCount() {
        return mData.getmData().size();
    }

    @Override
    public Object getItem(int i) {
        return mData.getmData().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_func, null);
            viewHolder.rootView = convertView;
            viewHolder.llRoot = (LinearLayout) convertView.findViewById(R.id.ll_root);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        showData(viewHolder, i);
        updateUI(viewHolder, viewGroup);

        return convertView;
    }

    private void showData(final ViewHolder viewHolder, final int position) {
        if (mData.getmData().get(position).getIcon() != null) {
            Glide.with(mContext).asBitmap().load(mData.getmData().get(position).getIcon()).into(viewHolder.ivIcon);

            viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != itemClickListener) {
                        itemClickListener.onItemClick(mData.getmData().get(position), position, mData.getPage());
                    }
                }
            });
        }
        viewHolder.tvTitle.setText(mData.getmData().get(position).getTitle());
    }

    private void updateUI(ViewHolder viewHolder, ViewGroup parent) {
        mItemHeightMax = this.mItemHeightMax != 0 ? this.mItemHeightMax : (int) (mItemHeight * mItemHeightMaxRatio);
        mItemHeightMin = this.mItemHeightMin != 0 ? this.mItemHeightMin : mItemHeight;
        int realItemHeight = ((View) parent.getParent()).getMeasuredHeight() / mData.getRow();
        realItemHeight = Math.min(realItemHeight, mItemHeightMax);
        realItemHeight = Math.max(realItemHeight, mItemHeightMin);
        viewHolder.ivIcon.setLayoutParams(new LinearLayout.LayoutParams(mItemHeight, mItemHeight));
        viewHolder.llRoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                realItemHeight));
    }

    private class ViewHolder {
        View rootView;
        LinearLayout llRoot;
        ImageView ivIcon;
        TextView tvTitle;
    }
}
