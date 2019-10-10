package com.daoyu.chat.module.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.home.bean.RedBagAdsBean;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.utils.GridSpacingItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 红包首页适配器
 */
public class RedBadAdapter extends RecyclerView.Adapter<RedBadAdapter.RedBadViewHolder> {

    private OnItemClickListener listener;

    private ArrayList<RedBagAdsBean.RedBagData> redBagLists;
    private ArrayList<RedBagAdsBean.RedBagData> explosionLists;
    private Context context;

    public RedBadAdapter(Context context) {
        this.context = context;
    }

    public void setRedBagLists(ArrayList<RedBagAdsBean.RedBagData> redBagLists) {
        this.redBagLists = redBagLists;
        notifyItemChanged(1);
    }

    public void setExplosionLists(ArrayList<RedBagAdsBean.RedBagData> explosionLists) {
        this.explosionLists = explosionLists;
        notifyItemChanged(0);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RedBadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RedBadViewHolder(LayoutInflater.from(context).inflate(R.layout.item_red_bag_landscape, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RedBadViewHolder holder, int i) {
        SubRedBagAdapter adapter = (SubRedBagAdapter) holder.rvRedBag.getAdapter();
        ArrayList<RedBagAdsBean.RedBagData> data = new ArrayList<>();
        if (adapter == null) {
            adapter = new SubRedBagAdapter(data, context);
        }
        adapter.setListener((sunData, subPosition) -> {
            if (listener == null) {
                return;
            }
            listener.onItemClick(i, subPosition);
        });
        holder.rvRedBag.setAdapter(adapter);
        if (i == 1) {
            holder.tvTitle.setText("品牌（天天可领）");
            data.clear();
            if (redBagLists != null && redBagLists.size() > 0) {
                data.addAll(redBagLists);
                adapter.setData(redBagLists);
            }
        } else {
            holder.tvTitle.setText("爆品（抢完即换）");
            data.clear();
            if (explosionLists != null && explosionLists.size() > 0) {
                data.addAll(explosionLists);
                adapter.setData(explosionLists);

            }
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    class RedBadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.rv_red_bag)
        RecyclerView rvRedBag;

        public RedBadViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rvRedBag.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            });
            rvRedBag.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtil.dip2px(12), true));
        }
    }
}
