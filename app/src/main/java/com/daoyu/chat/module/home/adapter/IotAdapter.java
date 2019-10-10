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
import com.daoyu.chat.module.home.bean.MineMenuBean;
import com.daoyu.chat.utils.GridSpacingItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IotAdapter extends RecyclerView.Adapter<IotAdapter.IotViewHolder> {

    private Context context;
    private OnItemClickListener listener;

    public IotAdapter(Context context) {
        this.context = context;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public IotAdapter.IotViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new IotAdapter.IotViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mine, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IotAdapter.IotViewHolder holder, int i) {
        SubMineMenuAdapter adapter = (SubMineMenuAdapter) holder.rvMineMenu.getAdapter();
        ArrayList<MineMenuBean> data = new ArrayList<>();
        data.clear();
        if (adapter == null) {
            adapter = new SubMineMenuAdapter(R.layout.item_mine_menu, data);
        }
        adapter.setListener((sunData, subPosition) -> {
            if (listener == null) {
                return;
            }
            listener.onItemClick(i, subPosition);
        });
        holder.rvMineMenu.setAdapter(adapter);
        if (i == 0) {
            data.clear();
            data.add(new MineMenuBean(R.drawable.iot_lock, "智能锁"));
            data.add(new MineMenuBean(R.drawable.iot_lock, "智能锁"));
            data.add(new MineMenuBean(R.drawable.iot_lock, "智能锁"));
            data.add(new MineMenuBean(R.drawable.iot_lock, "智能锁"));
            data.add(new MineMenuBean(R.drawable.iot_lock, "智能锁"));
            data.add(new MineMenuBean(R.drawable.iot_lock, "智能锁"));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class IotViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.rv_mine_menu)
        RecyclerView rvMineMenu;

        public IotViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rvMineMenu.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            });
            rvMineMenu.addItemDecoration(new GridSpacingItemDecoration(2, itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.dp_1), true));
        }
    }

}

