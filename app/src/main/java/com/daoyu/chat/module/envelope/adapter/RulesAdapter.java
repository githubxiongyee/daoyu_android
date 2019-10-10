package com.daoyu.chat.module.envelope.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.utils.ImageUtils;

import java.util.List;

public class RulesAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public RulesAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ImageView iv = helper.getView(R.id.iv);
        iv.setImageResource(R.drawable.redenvelopes_reward_main);
    }
}
