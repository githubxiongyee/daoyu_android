package com.daoyu.chat.module.group.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.GroupChatViewHolder> {

    private String firstLatter = "";
    private Context context;
    private ArrayList<GroupInfoBean.GroupInfoData> groupInfos;

    public GroupChatListAdapter(Context context, ArrayList<GroupInfoBean.GroupInfoData> groupInfos) {
        this.context = context;
        this.groupInfos = groupInfos;
    }

    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GroupChatViewHolder(LayoutInflater.from(context).inflate(R.layout.item_group_chat_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatViewHolder holder, int i) {
        GroupInfoBean.GroupInfoData groupInfoData = groupInfos.get(i);
        holder.tvName.setText(groupInfoData.groupname);
        ImageUtils.setNormalImage(context, groupInfoData.groupurl, R.drawable.ic_placeholder, R.drawable.ic_placeholder, holder.ivHeader);
        if (i == 0) {
            firstLatter = groupInfoData.firstLetter;
            holder.tvFirstLetter.setVisibility(View.VISIBLE);
            holder.tvFirstLetter.setText(firstLatter);
        } else {
            if (!TextUtils.isEmpty(firstLatter) && !firstLatter.equals(groupInfoData.firstLetter)) {
                firstLatter = groupInfoData.firstLetter;
                holder.tvFirstLetter.setVisibility(View.VISIBLE);
                holder.tvFirstLetter.setText(firstLatter);
            } else {
                holder.tvFirstLetter.setVisibility(View.GONE);
            }
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemViewClick(groupInfoData, i);
        });
    }


    @Override
    public int getItemCount() {
        return groupInfos == null ? 0 : groupInfos.size();
    }


    static class GroupChatViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_first_letter)
        TextView tvFirstLetter;
        @BindView(R.id.iv_header)
        CircleImageView ivHeader;
        @BindView(R.id.tv_name)
        TextView tvName;

        public GroupChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemViewClick(GroupInfoBean.GroupInfoData groupInfoData, int position);
    }
}
