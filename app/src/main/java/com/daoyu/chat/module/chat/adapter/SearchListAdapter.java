package com.daoyu.chat.module.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder> {

    private Context context;
    private ArrayList<ContactFriendBean.ContactFriendData> contactFriendLists;
    private boolean isContact = false;

    public SearchListAdapter(Context context, ArrayList<ContactFriendBean.ContactFriendData> contactFriendLists) {
        this.context = context;
        this.contactFriendLists = contactFriendLists;
    }

    @NonNull
    @Override
    public SearchListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SearchListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_send_card_contact_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListViewHolder holder, int i) {
        ContactFriendBean.ContactFriendData contactFriendData = contactFriendLists.get(i);
        if (!isContact) {
            holder.tvLabel.setText("联系人");
        } else {
            holder.tvLabel.setText("群聊");
        }
        ImageUtils.setNormalImage(context, contactFriendData.headImg, holder.ivHeader);
        holder.tvNickName.setText(contactFriendData.friendNickName);
        String fremarks = contactFriendData.fremarks;
        boolean empty = TextUtils.isEmpty(fremarks);
        if (empty) {
            holder.tvRemarkName.setVisibility(View.GONE);
        } else {
            holder.tvRemarkName.setVisibility(View.VISIBLE);
            holder.tvRemarkName.setText(String.format("备注名:%s", fremarks));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener==null)return;
            listener.onSearchContactClickListener(contactFriendData,i);
        });
    }

    @Override
    public int getItemCount() {
        return contactFriendLists == null ? 0 : contactFriendLists.size();
    }

    static class SearchListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_header)
        CircleImageView ivHeader;
        @BindView(R.id.tv_nick_name)
        TextView tvNickName;
        @BindView(R.id.tv_remark_name)
        TextView tvRemarkName;
        @BindView(R.id.tv_label)
        TextView tvLabel;

        public SearchListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnSearchContactClickListener listener;

    public void setListener(OnSearchContactClickListener listener) {
        this.listener = listener;
    }

    public interface OnSearchContactClickListener {
        void onSearchContactClickListener(ContactFriendBean.ContactFriendData contactFriendData,int position);
    }
}
