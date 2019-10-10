package com.daoyu.chat.module.home.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.module.home.bean.SearchContactBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.List;

/**
 * 搜索联系人适配器
 */
public class SearchContactAdapter extends BaseQuickAdapter<SearchContactBean.SearchContactData, BaseViewHolder> {
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SearchContactAdapter(int layoutResId, @Nullable List<SearchContactBean.SearchContactData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchContactBean.SearchContactData item) {
        CircleImageView ivHeader = helper.getView(R.id.iv_header);
        ImageUtils.setNormalImage(helper.itemView.getContext(), item.headImg, R.drawable.my_user_default, R.drawable.my_user_default, ivHeader);
        helper.setText(R.id.tv_contact_name, TextUtils.isEmpty(item.nickName) ? item.mobile : item.nickName);
        helper.setText(R.id.tv_contact_desc, TextUtils.isEmpty(item.remarks) ? "还没有设置个性签名哦" : item.remarks);

        helper.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(SearchContactAdapter.this, helper.itemView, helper.getPosition());
        });
    }
}
