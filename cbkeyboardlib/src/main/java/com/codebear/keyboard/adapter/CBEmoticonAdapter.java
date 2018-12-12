package com.codebear.keyboard.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codebear.keyboard.R;
import com.codebear.keyboard.data.EmoticonAdapterBean;
import com.codebear.keyboard.data.EmoticonsBean;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class CBEmoticonAdapter extends BaseAdapter {

    public interface OnItemClickListener {
        void onItemClick(EmoticonsBean data, int position, int page);
    }

    public interface OnItemTouchListener {
        boolean onItemTouch(EmoticonsBean data, MotionEvent motionEvent);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongCLick(EmoticonsBean data);
    }

    private Context mContext;
    private EmoticonAdapterBean mData;

    private double mItemHeightMaxRatio;
    private int mItemHeightMax;
    private int mItemHeightMin;
    private int mItemHeight;

    private OnItemClickListener itemClickListener;
    private OnItemTouchListener itemTouchListener;
    private OnItemLongClickListener itemLongClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemTouchListener(OnItemTouchListener itemTouchListener) {
        this.itemTouchListener = itemTouchListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public CBEmoticonAdapter(Context mContext, EmoticonAdapterBean mData) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_emoticon, null);
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
        if (mData.getmData().get(position).getIconUri() != null) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT && "gif".equals(mData.getmData().get(position)
                    .getIconType())) {

                Glide.with(mContext).asBitmap().load(mData.getmData().get(position).getIconUri()).into
                        (new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                resource = eraseColor(resource, -1);
                                resource = eraseColor(resource, -16777216);
                                viewHolder.ivIcon.setImageBitmap(resource);
                            }
                        });
            } else {
                Glide.with(mContext).asBitmap().load(mData.getmData().get(position).getIconUri()).into(viewHolder.ivIcon);
            }

            viewHolder.rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (null != itemTouchListener) {
                        return itemTouchListener.onItemTouch(mData.getmData().get(position), motionEvent);
                    }
                    return false;
                }
            });
            viewHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (null != itemLongClickListener) {
                        itemLongClickListener.onItemLongCLick(mData.getmData().get(position));
                    }
                    return false;
                }
            });
            viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != itemClickListener) {
                        itemClickListener.onItemClick(mData.getmData().get(position), position, mData.getPage());
                    }
                }
            });
        }
        if (mData.isShowName()) {
            viewHolder.tvTitle.setVisibility(View.VISIBLE);
            viewHolder.tvTitle.setText(mData.getmData().get(position).getName());
        }
    }

    //BitmapUtil中擦除Bitmap像素的方法
    private Bitmap eraseColor(Bitmap src, int color) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            if (pixels[i] == color) {
                pixels[i] = 0;
            }
        }
        b.setPixels(pixels, 0, width, 0, 0, width, height);
        return b;
    }

    private void updateUI(ViewHolder viewHolder, ViewGroup parent) {
        mItemHeightMax = this.mItemHeightMax != 0 ? this.mItemHeightMax : (int) (mItemHeight * mItemHeightMaxRatio);
        mItemHeightMin = this.mItemHeightMin != 0 ? this.mItemHeightMin : mItemHeight;
        int realItemHeight = ((View) parent.getParent()).getMeasuredHeight() / mData.getRow();
        realItemHeight = Math.min(realItemHeight, mItemHeightMax);
        realItemHeight = Math.max(realItemHeight, mItemHeightMin);
        viewHolder.ivIcon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                mItemHeight));
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
