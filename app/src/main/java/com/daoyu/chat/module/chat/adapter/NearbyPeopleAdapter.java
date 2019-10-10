package com.daoyu.chat.module.chat.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.module.chat.bean.NearbyPeopleB;
import com.daoyu.chat.utils.ImageUtils;

import java.util.List;

/**
 * 附近的人
 */
public class NearbyPeopleAdapter extends BaseQuickAdapter<NearbyPeopleB.DataBean, BaseViewHolder> {
    private OnItemClickListener listener;
    Context mContext;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NearbyPeopleAdapter(Context context,int layoutResId, @Nullable List<NearbyPeopleB.DataBean> data) {
        super(layoutResId,data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, NearbyPeopleB.DataBean item) {
        ImageView ivHeader = helper.getView(R.id.iv_header);
        ImageUtils.setRoundImageView(this.mContext, item.getHeadImg(), R.drawable.my_user_default, ivHeader);
        TextView tvName = helper.getView(R.id.tv_name);
        tvName.setText(item.getNickName());
        ImageView ivSex = helper.getView(R.id.iv_sex);
        if (item.getSex() ==null){

        }else {
            if (item.getSex().toString().equals("M")){
                ivSex.setImageResource(R.drawable.maillist_nearby_man);
            }else if (item.getSex().toString().equals("F")){
                ivSex.setImageResource(R.drawable.maillist_nearby_woman);
            }
        }

        TextView tvDisDesc = helper.getView(R.id.tv_distance_desc);
        if (item.getRemarks()==null){
            tvDisDesc.setText("2km");
        }else {
            tvDisDesc.setText("2km·"+item.getRemarks());
        }

        helper.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(NearbyPeopleAdapter.this, helper.itemView, helper.getPosition());
        });
    }
}