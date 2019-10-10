package com.daoyu.chat.module.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.module.chat.bean.MoreMenuBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreMenuAdapter extends RecyclerView.Adapter<MoreMenuAdapter.MoreMenuViewHolder> {

    private Context context;
    private List<MoreMenuBean> moreMenus;

    public MoreMenuAdapter(Context context, List<MoreMenuBean> moreMenus) {
        this.context = context;
        this.moreMenus = moreMenus;
    }

    @NonNull
    @Override
    public MoreMenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MoreMenuViewHolder(LayoutInflater.from(context).inflate(R.layout.item_more_menu, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoreMenuViewHolder moreMenuViewHolder, int i) {
        MoreMenuBean moreMenuBean = moreMenus.get(i);
        moreMenuViewHolder.ivMenuImg.setImageResource(moreMenuBean.imgIds);
        moreMenuViewHolder.tvMenu.setText(moreMenuBean.menu);
        moreMenuViewHolder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onMenuClickListener(moreMenuBean, i);
        });
    }

    @Override
    public int getItemCount() {
        return moreMenus == null ? 0 : moreMenus.size();
    }

    static class MoreMenuViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_menu_img)
        ImageView ivMenuImg;
        @BindView(R.id.tv_menu)
        TextView tvMenu;

        public MoreMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnMenuClickListener listener;

    public void setListener(OnMenuClickListener listener) {
        this.listener = listener;
    }

    public interface OnMenuClickListener {
        void onMenuClickListener(MoreMenuBean moreMenu, int position);
    }
}
