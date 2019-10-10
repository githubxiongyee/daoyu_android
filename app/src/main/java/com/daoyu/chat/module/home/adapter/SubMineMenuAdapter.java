package com.daoyu.chat.module.home.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.module.home.bean.MineMenuBean;

import java.util.List;

public class SubMineMenuAdapter extends BaseQuickAdapter<MineMenuBean, BaseViewHolder> {

    private com.daoyu.chat.common.OnItemClickListener listener;

    public void setListener(com.daoyu.chat.common.OnItemClickListener listener) {
        this.listener = listener;
    }

    public SubMineMenuAdapter(int layoutResId, @Nullable List<MineMenuBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MineMenuBean item) {
        helper.setText(R.id.tv_menu, item.menu);
        helper.setImageResource(R.id.iv_menu_icon, item.icons);
        helper.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(item, helper.getPosition());
        });
    }
}
