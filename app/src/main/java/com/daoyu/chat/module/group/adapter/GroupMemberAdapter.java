package com.daoyu.chat.module.group.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.group.bean.UsersHeaderBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberViewHolder> {

    private Context context;
    private ArrayList<UsersHeaderBean.UsersHeaderData> userHeaders;
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public GroupMemberAdapter(Context context, ArrayList<UsersHeaderBean.UsersHeaderData> userHeaders) {
        this.context = context;
        this.userHeaders = userHeaders;
    }


    @NonNull
    @Override
    public GroupMemberViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GroupMemberViewHolder(LayoutInflater.from(context).inflate(R.layout.item_selector_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberViewHolder holder, int i) {
        UsersHeaderBean.UsersHeaderData usersHeaderData = userHeaders.get(i);
        String nickName = usersHeaderData.nickName;
        holder.tvName.setText(nickName);
        String headImg = usersHeaderData.headImg;
        ImageUtils.setNormalImage(context, TextUtils.isEmpty(headImg) ? usersHeaderData.localHeader : headImg + "?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, holder.ivHeader);

        holder.ivClose.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(usersHeaderData, i);
        });
    }


    @Override
    public int getItemCount() {
        return userHeaders == null ? 0 : userHeaders.size();
    }


    static class GroupMemberViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_header)
        CircleImageView ivHeader;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.iv_close)
        ImageView ivClose;

        public GroupMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
