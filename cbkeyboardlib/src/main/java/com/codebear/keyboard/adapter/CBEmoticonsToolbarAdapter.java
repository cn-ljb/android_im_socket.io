package com.codebear.keyboard.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codebear.keyboard.R;
import com.codebear.keyboard.data.EmoticonsBean;
import com.codebear.keyboard.viewholder.CBEmoticonsViewHolder;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/29.
 */

public class CBEmoticonsToolbarAdapter extends CBRecyclerAdapter<EmoticonsBean, CBEmoticonsViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private Context mContext;

    private int selectPosition = 0;

    private OnItemClickListener onItemClickListener;

    public CBEmoticonsToolbarAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public CBEmoticonsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CBEmoticonsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_emoticon_toolbar,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final CBEmoticonsViewHolder holder, int position) {
        if (position == selectPosition) {
            holder.rlEmoticonsBg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.toolbar_btn_select));
        } else {
            holder.rlEmoticonsBg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.toolbar_btn_nomal));
        }
        Glide.with(mContext).asBitmap().load(get(position).getIconUri()).into(holder.ivEmoticon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectPosition == holder.getAdapterPosition()) {
                    return;
                }
                notifyItemChanged(selectPosition);
                selectPosition = holder.getAdapterPosition();
                notifyItemChanged(selectPosition);
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    public void setSelectPosition(int position) {
        notifyItemChanged(selectPosition);
        selectPosition = position;
        notifyItemChanged(selectPosition);
    }

    public int getSelectPosition() {
        return selectPosition;
    }
}
