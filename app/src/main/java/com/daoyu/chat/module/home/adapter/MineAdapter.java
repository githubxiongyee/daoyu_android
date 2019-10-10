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

/**
 * 我的适配器
 */
public class MineAdapter extends RecyclerView.Adapter<MineAdapter.MineViewHolder> {

    private Context context;
    private OnItemClickListener listener;

    public MineAdapter(Context context) {
        this.context = context;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MineViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mine, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MineViewHolder holder, int i) {
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
            holder.tvTitle.setText("钱包");
            data.clear();
            //data.add(new MineMenuBean(R.drawable.my_icon_paymet, "收付款"));
            data.add(new MineMenuBean(R.drawable.my_icon_paymet, "收付款"));
            data.add(new MineMenuBean(R.drawable.my_icon_money, "零钱"));
            data.add(new MineMenuBean(R.drawable.my_icon_balance, "余额"));
            // data.add(new MineMenuBean(R.drawable.my_icon_balance, "余额"));
            adapter.notifyDataSetChanged();
        } else if (i == 1) {
            holder.tvTitle.setText("潜言服务");
            data.clear();
            data.add(new MineMenuBean(R.drawable.my_icon_order, "订单 "));
            //data.add(new MineMenuBean(R.drawable.my_icon_collection, "收藏"));
            data.add(new MineMenuBean(R.drawable.my_icon_skin, "皮肤"));
            //data.add(new MineMenuBean(R.drawable.my_icon_expression, "表情"));
            data.add(new MineMenuBean(R.drawable.my_icon_iot,"IOT"));
            //data.add(new MineMenuBean(R.drawable.my_icon_applets, "小程序"));
            adapter.notifyDataSetChanged();
        } else {
            holder.tvTitle.setText("第三方服务");
            data.clear();
            data.add(new MineMenuBean(R.drawable.my_icon_train, "火车票 "));
            data.add(new MineMenuBean(R.drawable.my_icon_hotel, "酒店"));
            //data.add(new MineMenuBean(R.drawable.my_icon_food, "美食"));
            data.add(new MineMenuBean(R.drawable.my_icon_shopping, "购物"));
            data.add(new MineMenuBean(R.drawable.my_icon_motion, "运动"));
            data.add(new MineMenuBean(R.drawable.my_icon_exit, "出境"));
            //data.add(new MineMenuBean(R.drawable.my_icon_redenvelopes, "红包"));
            //data.add(new MineMenuBean(R.drawable.my_icon_bodybuilding, "体育服务"));
            //data.add(new MineMenuBean(R.drawable.my_icon_leisuretime, "休闲"));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    static class MineViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.rv_mine_menu)
        RecyclerView rvMineMenu;

        public MineViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rvMineMenu.setLayoutManager(new GridLayoutManager(itemView.getContext(), 3) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            });
            rvMineMenu.addItemDecoration(new GridSpacingItemDecoration(3, itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.dp_1), true));
        }
    }

}
