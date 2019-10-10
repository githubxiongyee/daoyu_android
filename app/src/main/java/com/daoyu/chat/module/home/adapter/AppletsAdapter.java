package com.daoyu.chat.module.home.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.utils.ImageUtils;

import java.util.List;

/**
 * 小程序
 */
public class AppletsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public AppletsAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ImageView ivIcon = helper.getView(R.id.iv_applets_icon);
        TextView tvAppletsName = helper.getView(R.id.tv_applets_name);
        if (helper.getPosition() == 3) {
            ivIcon.setImageResource(R.drawable.redenvelopes_smallprocedures_bg_more);
            tvAppletsName.setVisibility(View.INVISIBLE);
        } else if (helper.getPosition() == 2){
            ImageUtils.setRoundImageView(helper.itemView.getContext(),"http://image.futruedao.com/1eb304f27c9a41cc869ad02054ead67e.png",R.drawable.my_user_default,ivIcon);
            //ivIcon.setImageResource(R.drawable.redenvelopes_smallprocedures_bg);
            tvAppletsName.setVisibility(View.VISIBLE);
            tvAppletsName.setText("三湖黄桃");
        }else if (helper.getPosition() == 1){
            ImageUtils.setRoundImageView(helper.itemView.getContext(),"http://image.futruedao.com/4db4501836b44f18b9df435793867c2b.png",R.drawable.my_user_default,ivIcon);
            //ivIcon.setImageResource(R.drawable.redenvelopes_smallprocedures_bg);
            tvAppletsName.setVisibility(View.VISIBLE);
            tvAppletsName.setText("低脂可可");
        }else if (helper.getPosition() == 0){
            ImageUtils.setRoundImageView(helper.itemView.getContext(),"http://image.futruedao.com/e4f098b6458e401b9320d7d5c6da2ad8.png",R.drawable.my_user_default,ivIcon);
            //ivIcon.setImageResource(R.drawable.redenvelopes_smallprocedures_bg);
            tvAppletsName.setVisibility(View.VISIBLE);
            tvAppletsName.setText("大宋御盏");
        }
    }
}
