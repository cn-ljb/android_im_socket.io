package com.codebear.keyboard.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codebear.keyboard.R;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/29.
 */

public class CBEmoticonsViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlEmoticonsBg;
    public ImageView ivEmoticon;

    public CBEmoticonsViewHolder(View itemView) {
        super(itemView);

        rlEmoticonsBg = (RelativeLayout) itemView.findViewById(R.id.rl_emoticon_bg);
        ivEmoticon = (ImageView) itemView.findViewById(R.id.iv_emoticon);
    }
}
