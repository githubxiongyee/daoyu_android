package com.daoyu.chat.module.chat.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseFragment;
import com.daoyu.chat.event.MenuClickEvent;
import com.daoyu.chat.module.chat.adapter.MoreMenuAdapter;
import com.daoyu.chat.module.chat.bean.MoreMenuBean;
import com.daoyu.chat.utils.GridSpacingItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 默认第一页更多
 */
public class DefaultFirstMoreFragment extends BaseFragment implements MoreMenuAdapter.OnMenuClickListener {


    @BindView(R.id.rv_more)
    RecyclerView rvMore;
    private ArrayList<MoreMenuBean> moreMenus;
    private MoreMenuAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_default_more;
    }

    @Override
    protected void initEvent() {
        rvMore.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvMore.addItemDecoration(new GridSpacingItemDecoration(4, getResources().getDimensionPixelOffset(R.dimen.dp_24), true));
        moreMenus = new ArrayList<>();
        moreMenus.add(new MoreMenuBean("拍照", R.drawable.home_icon_chat_shot));
        moreMenus.add(new MoreMenuBean("相册", R.drawable.home_icon_chat_album));
        moreMenus.add(new MoreMenuBean("红包", R.drawable.home_icon_chat_redenvelopes));
        //moreMenus.add(new MoreMenuBean("转账", R.drawable.home_icon_chat_transfer));
        //moreMenus.add(new MoreMenuBean("语音", R.drawable.home_icon_chat_voice));
        //moreMenus.add(new MoreMenuBean("视频", R.drawable.home_icon_chat_video));
        moreMenus.add(new MoreMenuBean("位置", R.drawable.home_icon_chat_position));
        moreMenus.add(new MoreMenuBean("名片", R.drawable.home_icon_chat_card));
        //moreMenus.add(new MoreMenuBean("文件", R.drawable.home_icon_chat_file));
        adapter = new MoreMenuAdapter(getContext(), moreMenus);
        adapter.setListener(this);
        rvMore.setAdapter(adapter);
    }

    @Override
    public void onMenuClickListener(MoreMenuBean moreMenu, int position) {
        EventBus.getDefault().post(new MenuClickEvent(moreMenu.menu));
    }
}
