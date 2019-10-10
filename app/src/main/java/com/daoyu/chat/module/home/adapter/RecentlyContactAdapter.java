package com.daoyu.chat.module.home.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.utils.ImageUtils;

import java.util.List;

/**
 * 发送最近的联系人适配器
 */
public class RecentlyContactAdapter extends BaseQuickAdapter<ChatTable, BaseViewHolder> {
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecentlyContactAdapter(int layoutResId, @Nullable List<ChatTable> data) {
        super(layoutResId,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatTable item) {
        helper.setText(R.id.tv_contact_name, item.username);
        helper.setVisible(R.id.tv_contact_desc, false);
        ImageView imageView = helper.getView(R.id.iv_header);
        ImageUtils.setNormalImage(helper.itemView.getContext(),item.avatar+"?imageMogr2/thumbnail/200/quality/40",imageView);
        helper.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(RecentlyContactAdapter.this, helper.itemView, helper.getPosition());
        });
    }
}
