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
 * 默认第二页更多
 */
public class DefaultSecondMoreFragment extends BaseFragment implements MoreMenuAdapter.OnMenuClickListener {


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
        moreMenus = new ArrayList<>();
        moreMenus.add(new MoreMenuBean("名片", R.drawable.home_icon_chat_card));
        //moreMenus.add(new MoreMenuBean("收藏", R.drawable.home_icon_chat_collection));
        adapter = new MoreMenuAdapter(getContext(), moreMenus);
        adapter.setListener(this);
        rvMore.setAdapter(adapter);
    }

    @Override
    public void onMenuClickListener(MoreMenuBean moreMenu, int position) {
        EventBus.getDefault().post(new MenuClickEvent(moreMenu.menu));
    }
}
