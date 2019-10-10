package com.daoyu.chat.module.mine.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.module.mine.bean.AddressBean;

import java.util.List;

public class ChooseAddressAdapter extends BaseQuickAdapter<AddressBean, BaseViewHolder> {
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ChooseAddressAdapter(int layoutResId, @Nullable List<AddressBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddressBean item) {
        TextView tvAddress = helper.getView(R.id.tv_address);
        tvAddress.setText(item.name);
        helper.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(this, tvAddress, helper.getPosition());
        });
    }
}
