package com.codebear.keyboard.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.codebear.keyboard.R;
import com.codebear.keyboard.adapter.CBAppFuncAdapter;
import com.codebear.keyboard.data.AppFuncAdapterBean;
import com.codebear.keyboard.data.AppFuncBean;

import java.util.ArrayList;
import java.util.List;

/**
 * description:默认的表情集的view
 * <p>
 * Created by CodeBearon 2017/6/28.
 */

public class CBAppFuncView extends FrameLayout {

    public interface OnAppFuncClickListener {
        void onAppFunClick(AppFuncBean emoticon);
    }

    private View rootView;
    private ViewPager vpAppFuncContent;
    private CBViewPagerIndicatorView cbvpiGuideIndicator;

    private List<View> views = new ArrayList<>();
    private List<AppFuncBean> appFuncBeanList = new ArrayList<>();

    private int rol = 4;
    private int row = 2;

    private int pageSize;
    private int size;
    private int count;

    private OnAppFuncClickListener listener;

    public void setOnAppFuncClickListener(OnAppFuncClickListener listener) {
        this.listener = listener;
    }

    public CBAppFuncView(Context context) {
        super(context);

        rootView = LayoutInflater.from(context).inflate(R.layout.cb_view_app_func_default, this, false);
        addView(rootView);

        initViewPager();
    }

    private void initViewPager() {
        vpAppFuncContent = (ViewPager) rootView.findViewById(R.id.vp_app_func_content);
        cbvpiGuideIndicator = (CBViewPagerIndicatorView) rootView.findViewById(R.id.cbvpi_guide_indicator);

        vpAppFuncContent.setAdapter(new PagerAdapter() {

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
        cbvpiGuideIndicator.setViewPager(vpAppFuncContent);
    }

    private void initData() {

        if(rol == -1 || row == -1) {
            throw new IllegalArgumentException("appFunc rol and row must be > -1");
        }

        size = appFuncBeanList.size();
        count = rol * row;
        pageSize = size / count;
        if (size % count > 0) {
            pageSize++;
        }
    }

    private void initView() {
        for (int i = 0; i < pageSize; ++i) {
            View view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R
                    .layout.view_emoticon_gridview, null);
            GridView egvEmoticon = (GridView) view.findViewById(R.id.egv_emoticon);
            egvEmoticon.setNumColumns(rol);

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

            AppFuncAdapterBean appFuncAdapterBean = new AppFuncAdapterBean(appFuncBeanList.subList(left, right));
            appFuncAdapterBean.setPage(i);
            appFuncAdapterBean.setRol(rol);
            appFuncAdapterBean.setRow(row);

            CBAppFuncAdapter adapter = new CBAppFuncAdapter(getContext(), appFuncAdapterBean);
            egvEmoticon.setAdapter(adapter);
            adapter.setItemClickListener(new CBAppFuncAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(AppFuncBean data, int position, int page) {
                    if (null != listener) {
                        listener.onAppFunClick(data);
                    }
                }
            });

            views.add(view);
        }
        vpAppFuncContent.getAdapter().notifyDataSetChanged();
        cbvpiGuideIndicator.setPageCount(pageSize);
    }

    public void setAppFuncBeanList(List<AppFuncBean> appFuncBeanList) {
        this.appFuncBeanList = appFuncBeanList;
        initData();
        initView();
    }

    public void setRol(int rol) {
        this.rol = rol;
        initData();
        initView();
    }

    public void setRow(int row) {
        this.row = row;
        initData();
        initView();
    }
}
