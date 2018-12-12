package com.codebear.keyboard.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.codebear.keyboard.R;
import com.codebear.keyboard.adapter.CBEmoticonsToolbarAdapter;
import com.codebear.keyboard.data.EmojiBean;
import com.codebear.keyboard.data.EmoticonsBean;
import com.codebear.keyboard.emoji.DefEmoticons;
import com.codebear.keyboard.fragment.CBEmoticonFragment;
import com.codebear.keyboard.fragment.ICBFragment;
import com.codebear.keyboard.interfaces.IEmoticonsView;
import com.codebear.keyboard.utils.ParseDataUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * description:默认的表情集的view
 * <p>
 * Created by CodeBearon 2017/6/28.
 */

public class CBEmoticonsView extends FrameLayout implements IEmoticonsView {

    public interface OnEmoticonClickListener {
        void onEmoticonClick(EmoticonsBean emoticon, boolean isDel);
    }

    private View rootView;
    private ViewPager vpEmoticonsContent;
    private RecyclerView rcvEmoticonsToolbar;

    private FragmentManager fragmentManager;

    private CBEmoticonsToolbarAdapter emoticonsToolbarAdapter;

    private List<EmoticonsBean> emoticonsBeanList = new ArrayList<>();
    private List<ICBFragment> emoticonFragments = new ArrayList<>();

    private OnEmoticonClickListener listener;

    private int lastPosition = 0;
    private boolean click = false;

    private List<String> emoticonName = new ArrayList<>();
    private List<String> emoticonNameBackup = new ArrayList<>();

    public void setOnEmoticonClickListener(OnEmoticonClickListener listener) {
        this.listener = listener;
        for (ICBFragment fragment : emoticonFragments) {
            fragment.setOnEmoticonClickListener(listener);
        }
    }

    public CBEmoticonsView(Context context) {
        super(context);

        rootView = LayoutInflater.from(context).inflate(R.layout.cb_view_emoticons_default, this, false);
        addView(rootView);
    }

    public void init(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        initViewPager();
        initRecycleView();
    }

    private void initViewPager() {
        vpEmoticonsContent = (ViewPager) rootView.findViewById(R.id.vp_emoticons_content);

        vpEmoticonsContent.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return emoticonFragments.get(position).getFragment();
            }

            @Override
            public int getCount() {
                return emoticonFragments.size();
            }
        });
        vpEmoticonsContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!click) {
                    if (lastPosition < position) {
                        emoticonFragments.get(position).setSeeItem(0);
                    } else if (lastPosition > position) {
                        emoticonFragments.get(position).setSeeItem(1);
                    }
                }
                click = false;
                lastPosition = position;
                emoticonsToolbarAdapter.setSelectPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initRecycleView() {
        rcvEmoticonsToolbar = (RecyclerView) rootView.findViewById(R.id.rcv_emoticons_toolbar);
        rcvEmoticonsToolbar.setHasFixedSize(true);
        rcvEmoticonsToolbar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false));

        emoticonsToolbarAdapter = new CBEmoticonsToolbarAdapter(getContext());
        rcvEmoticonsToolbar.setAdapter(emoticonsToolbarAdapter);

        emoticonsToolbarAdapter.setOnItemClickListener(new CBEmoticonsToolbarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                click = true;
                vpEmoticonsContent.setCurrentItem(position, true);
            }
        });
    }

    public void addEmoticons(EmoticonsBean bean) {
        emoticonsToolbarAdapter.add(bean);
        emoticonsBeanList.add(bean);
        emoticonsToolbarAdapter.notifyItemInserted(emoticonsBeanList.size());

        ICBFragment fragment = CBEmoticonFragment.newInstance();
        fragment.setEmoticonsBean(bean);
        fragment.setOnEmoticonClickListener(listener);
        emoticonFragments.add(fragment);
        vpEmoticonsContent.setOffscreenPageLimit(emoticonFragments.size());
        vpEmoticonsContent.getAdapter().notifyDataSetChanged();
    }

    public void addEmoticons(List<EmoticonsBean> beanList) {
        emoticonsToolbarAdapter.addAll(beanList);
        emoticonsBeanList.addAll(beanList);
        emoticonsToolbarAdapter.notifyItemRangeInserted(emoticonsBeanList.size() - beanList.size(), beanList.size());

        for (EmoticonsBean bean : beanList) {
            ICBFragment fragment = CBEmoticonFragment.newInstance();
            fragment.setOnEmoticonClickListener(listener);
            fragment.setEmoticonsBean(bean);
            emoticonFragments.add(fragment);
        }
        vpEmoticonsContent.getAdapter().notifyDataSetChanged();
    }

    public void addEmoticonsWithName(String name) {
        emoticonName.add(name);
        emoticonNameBackup.add(name);
        EmoticonsBean bean = new EmoticonsBean();
        bean.setRow(1);
        bean.setRol(1);
        addEmoticons(bean);
    }

    public void addEmoticonsWithName(String[] nameList) {
        for (String name : nameList) {
            addEmoticonsWithName(name);
        }
    }

    private void addEmoticonByName(String name) {
        EmoticonsBean bean;
        if ("default".equals(name)) {
            bean = getDefaultEmoticon();
        } else {
            bean = ParseDataUtils.parseDataFromFile(getContext(), name);
            if (null != bean) {
                for (EmoticonsBean b : bean.getEmoticonsBeanList()) {
                    b.setParentTag(name);
                }
            }
        }
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putParcelable("emoticon", bean);
        bundle.putString("name", name);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            Bundle bundle = msg.getData();
            if (bundle == null) {
                return;
            }
            EmoticonsBean bean = bundle.getParcelable("emoticon");
            String name = bundle.getString("name", "");
            if (null != bean) {
                for (int i = 0; i < emoticonNameBackup.size(); ++i) {
                    if (emoticonNameBackup.get(i).equals(name)) {
                        emoticonsToolbarAdapter.set(i, bean);
                        emoticonsToolbarAdapter.notifyItemChanged(i);
                        emoticonFragments.get(i).setEmoticonsBean(bean);
                        vpEmoticonsContent.getAdapter().notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    };

    private EmoticonsBean getDefaultEmoticon() {

        final ArrayList<EmojiBean> emojiArray = new ArrayList<>();
        Collections.addAll(emojiArray, DefEmoticons.sEmojiArray);

        EmoticonsBean emoticonsBean = new EmoticonsBean();
        emoticonsBean.setId("default");
        emoticonsBean.setName(emojiArray.get(0).emoji);
        emoticonsBean.setIconUri(emojiArray.get(0).icon);
        emoticonsBean.setShowDel(true);
        emoticonsBean.setBigEmoticon(false);
        for (EmojiBean emojiBean : emojiArray) {
            EmoticonsBean temp = new EmoticonsBean();
            temp.setParentTag("default");
            temp.setParentId(emoticonsBean.getId());
            temp.setName(emojiBean.emoji);
            temp.setIconUri(emojiBean.icon);
            emoticonsBean.getEmoticonsBeanList().add(temp);
        }
        return emoticonsBean;
    }

    @Override
    public View getView() {
        return this;
    }

    private boolean isGetEmoticon = false;

    @Override
    public void openView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isGetEmoticon) {
                    return;
                }
                isGetEmoticon = true;
                while (emoticonName.size() > 0) {
                    addEmoticonByName(emoticonName.get(0));
                    emoticonName.remove(0);
                }
            }
        }).start();
    }
}
