package com.daoyu.chat.module.group.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.module.group.bean.UsersHeaderBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LordTransferAdapter extends RecyclerView.Adapter<LordTransferAdapter.LordTransferViewHolder> {
    private String firstLatter = "";
    private Context context;
    private ArrayList<UsersHeaderBean.UsersHeaderData> userHeaders;

    public LordTransferAdapter(Context context, ArrayList<UsersHeaderBean.UsersHeaderData> userHeaders) {
        this.context = context;
        this.userHeaders = userHeaders;
    }

    @NonNull
    @Override
    public LordTransferViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LordTransferViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lord_transfer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LordTransferViewHolder holder, int i) {
        UsersHeaderBean.UsersHeaderData usersHeaderData = userHeaders.get(i);
        holder.tvName.setText(usersHeaderData.nickName);
        ImageUtils.setNormalImage(context, usersHeaderData.headImg, R.drawable.ic_placeholder, R.drawable.ic_placeholder, holder.ivHeader);
        holder.cbChecked.setChecked(usersHeaderData.checked);
        if (i == 0) {
            firstLatter = usersHeaderData.firstLetter;
            holder.tvFirstLetter.setVisibility(View.VISIBLE);
            holder.tvFirstLetter.setText(firstLatter);
        } else {
            if (!TextUtils.isEmpty(firstLatter) && !firstLatter.equals(usersHeaderData.firstLetter)) {
                firstLatter = usersHeaderData.firstLetter;
                holder.tvFirstLetter.setVisibility(View.VISIBLE);
                holder.tvFirstLetter.setText(firstLatter);
            } else {
                holder.tvFirstLetter.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            for (int j = 0; j < userHeaders.size(); j++) {
                userHeaders.get(j).checked = false;
            }
            usersHeaderData.checked = true;
            notifyDataSetChanged();
            if (listener == null) return;
            listener.onItemViewClick(usersHeaderData, i);
        });
    }


    @Override
    public int getItemCount() {
        return userHeaders == null ? 0 : userHeaders.size();
    }


    static class LordTransferViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_first_letter)
        TextView tvFirstLetter;
        @BindView(R.id.iv_header)
        CircleImageView ivHeader;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.cb_checked)
        CheckBox cbChecked;

        public LordTransferViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemViewClick(UsersHeaderBean.UsersHeaderData usersHeaderData, int position);
    }
}
