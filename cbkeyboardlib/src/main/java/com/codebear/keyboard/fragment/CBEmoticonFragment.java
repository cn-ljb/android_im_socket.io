package com.codebear.keyboard.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codebear.keyboard.R;
import com.codebear.keyboard.adapter.CBEmoticonAdapter;
import com.codebear.keyboard.data.BigEmoticonAdapterBean;
import com.codebear.keyboard.data.EmoticonAdapterBean;
import com.codebear.keyboard.data.EmoticonsBean;
import com.codebear.keyboard.widget.CBEmoticonsView;
import com.codebear.keyboard.widget.CBViewPagerIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/29.
 */

public class CBEmoticonFragment extends Fragment implements ICBFragment {

    private Context mContext;
    private View mRootView;

    private ViewPager vpEmoticonContent;
    private CBViewPagerIndicatorView cbvpiGuideIndicator;

    private EmoticonsBean emoticonsBean = new EmoticonsBean();

    private List<View> views = new ArrayList<>();

    private boolean hadLoadData = false;
    private boolean userVisible = false;
    private boolean viewCreate = false;

    private int pageSize;
    private int size;
    private int count;

    private Dialog previewDialog;

    private CBEmoticonsView.OnEmoticonClickListener listener;

    public static ICBFragment newInstance() {
        return new CBEmoticonFragment();
    }

    @Override
    public void setOnEmoticonClickListener(CBEmoticonsView.OnEmoticonClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_emoticons, container, false);
        mContext = getContext();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vpEmoticonContent = (ViewPager) mRootView.findViewById(R.id.vp_emoticon_content);
        cbvpiGuideIndicator = (CBViewPagerIndicatorView) mRootView.findViewById(R.id.cbvpi_guide_indicator);

        initViewPager();
        initData();
        viewCreate = true;
        if (!hadLoadData && userVisible) {
            hadLoadData = true;
            initView();
        }
    }

    private void initViewPager() {
        vpEmoticonContent.setAdapter(new PagerAdapter() {

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position));

                return views.get(position);
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return (view == object);
            }
        });
        cbvpiGuideIndicator.setViewPager(vpEmoticonContent);
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            userVisible = true;
            if (!hadLoadData && viewCreate) {
                hadLoadData = true;
                initView();
            }
        }
    }

    private void initData() {

        if (emoticonsBean.getRow() == -1 || emoticonsBean.getRow() == -1) {
            throw new IllegalArgumentException("emoticon rol and row must be > -1");
        }

        if (null == emoticonsBean.getEmoticonsBeanList()) {
            return;
        }

        size = emoticonsBean.getEmoticonsBeanList().size();
        count = emoticonsBean.getRol() * emoticonsBean.getRow();
        if (emoticonsBean.isShowDel()) {
            int p = count - 1;
            while (p <= size) {
                emoticonsBean.getEmoticonsBeanList().add(p, new EmoticonsBean(true));
                p += count;
                size += 1;
            }
            if (size % count > 0) {
                for (int i = size; i < p; ++i) {
                    emoticonsBean.getEmoticonsBeanList().add(i, new EmoticonsBean());
                }
                emoticonsBean.getEmoticonsBeanList().add(p, new EmoticonsBean(true));
                p++;
                size = p;
            }
        }
        pageSize = size / count;
        if (size % count > 0) {
            pageSize++;
        }
    }

    private void initView() {
        if (!viewCreate) {
            return;
        }
        views.clear();
        for (int i = 0; i < pageSize; ++i) {
            View view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R
                    .layout.view_emoticon_gridview, null);
            GridView egvEmoticon = (GridView) view.findViewById(R.id.egv_emoticon);
            egvEmoticon.setNumColumns(emoticonsBean.getRol());

            egvEmoticon.setMotionEventSplittingEnabled(false);
            egvEmoticon.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            egvEmoticon.setCacheColorHint(0);
            egvEmoticon.setSelector(new ColorDrawable(Color.TRANSPARENT));
            egvEmoticon.setVerticalScrollBarEnabled(false);

            final int left = i * count;
            int right = (i + 1) * count;
            if (right > size) {
                right = size;
            }

            EmoticonAdapterBean emoticonAdapterBean;
            if (emoticonsBean.isBigEmoticon()) {
                emoticonAdapterBean = new BigEmoticonAdapterBean(emoticonsBean.getEmoticonsBeanList().subList(left,
                        right));
            } else {
                emoticonAdapterBean = new EmoticonAdapterBean(emoticonsBean.getEmoticonsBeanList().subList(left,
                        right));
            }
            emoticonAdapterBean.setPage(i);
            emoticonAdapterBean.setRol(emoticonsBean.getRol());
            emoticonAdapterBean.setRow(emoticonsBean.getRow());
            emoticonAdapterBean.setShowName(emoticonsBean.isShowName());

            CBEmoticonAdapter adapter = new CBEmoticonAdapter(mContext, emoticonAdapterBean);
            egvEmoticon.setAdapter(adapter);
            adapter.setItemClickListener(new CBEmoticonAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(EmoticonsBean data, int position, int page) {
                    data.setParentId(emoticonsBean.getId());
                    data.setBigEmoticon(emoticonsBean.isBigEmoticon());
                    if (null != listener) {
                        listener.onEmoticonClick(data, emoticonsBean.isShowDel() && EmoticonsBean.DEL.equals(data
                                .getId()));
                    }
                }
            });
            adapter.setItemTouchListener(new CBEmoticonAdapter.OnItemTouchListener() {
                @Override
                public boolean onItemTouch(EmoticonsBean data, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            hidePreview();
                            break;
                    }
                    return false;
                }
            });
            adapter.setOnItemLongClickListener(new CBEmoticonAdapter.OnItemLongClickListener() {

                @Override
                public boolean onItemLongCLick(EmoticonsBean data) {
                    if (emoticonsBean.isBigEmoticon()) {
                        showPreview(data);
                    }
                    return false;
                }
            });

            views.add(view);
        }
        vpEmoticonContent.getAdapter().notifyDataSetChanged();
        cbvpiGuideIndicator.setPageCount(pageSize);
    }

    private ImageView previewBigEmoticon;

    private void showPreview(EmoticonsBean emoticon) {
        if (previewDialog == null) {
            View view = View.inflate(mContext, R.layout.view_preview_big_emoticon, null);
            previewBigEmoticon = (ImageView) view.findViewById(R.id.iv_preview_big_emotion);
            previewDialog = new Dialog(mContext, R.style.preview_dialog_style);
            previewDialog.setContentView(view);
        }
        if (emoticon.getIconType().equals("gif")) {
            Glide.with(mContext).asGif().load(emoticon.getIconUri()).into(previewBigEmoticon);
        } else {
            Glide.with(mContext).asBitmap().load(emoticon.getIconUri()).into(previewBigEmoticon);
        }
        if (!previewDialog.isShowing()) {
            previewDialog.show();
        }
    }

    private void hidePreview() {
        if (previewDialog != null && previewDialog.isShowing()) {
            Glide.with(mContext).clear(previewBigEmoticon);
            previewDialog.dismiss();
        }
    }

    @Override
    public void setSeeItem(int which) {
        if (which == 0) {
            vpEmoticonContent.setCurrentItem(0, false);
        } else if (which == 1) {
            vpEmoticonContent.setCurrentItem(pageSize);
        }
    }

    @Override
    public void setEmoticonsBean(EmoticonsBean emoticonsBean) {
        this.emoticonsBean = emoticonsBean;
        initData();
        initView();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }
}
