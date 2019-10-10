package com.daoyu.chat.module.mine.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.home.adapter.MineAdapter;
import com.daoyu.chat.utils.GridSpacingItemDecoration;
import com.daoyu.chat.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 表情列表
 */
public class ExpressionListAdapter extends RecyclerView.Adapter {
    private Context context;
    private OnItemClickListener listener;
    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    public ExpressionListAdapter(Context context) {
        this.context = context;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return VIEW_TYPE_TOP;
        }else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_TOP:
                viewHolder = new ExpressionTopViewHolder(LayoutInflater.from(context).inflate(R.layout.item_expression_top_img, viewGroup, false));
                break;
            case VIEW_TYPE_NORMAL:
                viewHolder = new ExpressionListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_expression_word, viewGroup, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_TOP:
                //ImageUtils.setNormalImage(context,"",R.drawable.my_user_default,R.drawable.my_user_default,((ExpressionTopViewHolder)viewHolder).iv);
                break;
            case VIEW_TYPE_NORMAL:
                if (position == 1){
                    ((ExpressionListViewHolder)viewHolder).tvTitle.setText("热门表情");

                }else if (position == 2){
                    ((ExpressionListViewHolder)viewHolder).tvTitle.setText("更多表情");
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    static class ExpressionTopViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_expression_top)
        ImageView iv;
        public ExpressionTopViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    static class ExpressionListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.rv_hot)
        RecyclerView rv;

        public ExpressionListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rv.setLayoutManager(new LinearLayoutManager(itemView.getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            });
            rv.setAdapter(new ExpressionItemAdapter(R.layout.item_expression_hot,test()));
            //rv.addItemDecoration(new GridSpacingItemDecoration(3, itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.dp_1), true));
        }

        private List<String> test() {
            List<String> data= new ArrayList<>();
            for (int i = 0;i < 3;i++){
                data.add(i+"号表情");
            }
            return data;
        }
    }

}
