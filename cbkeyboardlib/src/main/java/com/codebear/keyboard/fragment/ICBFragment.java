package com.codebear.keyboard.fragment;

import android.support.v4.app.Fragment;

import com.codebear.keyboard.data.EmoticonsBean;
import com.codebear.keyboard.widget.CBEmoticonsView;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public interface ICBFragment {
    void setSeeItem(int which);
    Fragment getFragment();
    void setEmoticonsBean(EmoticonsBean emoticonsBean);
    void setOnEmoticonClickListener(CBEmoticonsView.OnEmoticonClickListener listener);
}
