package com.codebear.keyboard.data;

import com.codebear.keyboard.R;

import java.util.List;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class BigEmoticonAdapterBean extends EmoticonAdapterBean {

    public BigEmoticonAdapterBean(List<EmoticonsBean> mData) {
        super(mData);
        rol = 4;
        row = 3;
        mItemHeightMaxRatio = 1.6;
        mItemHeight = R.dimen.item_big_emoticon_size_default;
    }


}
