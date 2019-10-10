package com.daoyu.chat.module.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.daoyu.chat.R;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.home.bean.RedBagAdsBean;
import com.daoyu.chat.utils.ImageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubRedBagAdapter extends RecyclerView.Adapter<SubRedBagAdapter.SubRedBagViewHolder> {

    private List<RedBagAdsBean.RedBagData> data;
    private Context context;

    public SubRedBagAdapter(List<RedBagAdsBean.RedBagData> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setData(List<RedBagAdsBean.RedBagData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubRedBagViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SubRedBagViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sub_reb_bag, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubRedBagViewHolder subRedBagViewHolder, int i) {
        RedBagAdsBean.RedBagData redBagData = data.get(i);
        ImageUtils.setNormalImage(context, redBagData.thumbPicUrl, R.drawable.register_user_default, R.drawable.register_user_default, subRedBagViewHolder.ivRedBagBg);

        subRedBagViewHolder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(subRedBagViewHolder.itemView, i);
        });
        String hongbaoSta = redBagData.hongbaoSta;
        if (TextUtils.isEmpty(redBagData.productDesc)){
            if ("N".equals(hongbaoSta)) {
                subRedBagViewHolder.cbReceivingRedEnvelope.setChecked(true);
                subRedBagViewHolder.cbReceivingRedEnvelope.setText("领取红包");
            } else {
                subRedBagViewHolder.cbReceivingRedEnvelope.setChecked(false);
                subRedBagViewHolder.cbReceivingRedEnvelope.setText("已领取");
            }
        }else {
            subRedBagViewHolder.cbReceivingRedEnvelope.setChecked(true);
            subRedBagViewHolder.cbReceivingRedEnvelope.setText("免费兑换");
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class SubRedBagViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_red_bag_bg)
        ImageView ivRedBagBg;
        @BindView(R.id.cb_receiving_red_envelope)
        CheckBox cbReceivingRedEnvelope;

        public SubRedBagViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
