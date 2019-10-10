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
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectorContactAdapter extends RecyclerView.Adapter<SelectorContactAdapter.SelectorContactViewHolder> {

    private Context context;
    private ArrayList<ContactFriendBean.ContactFriendData> contactLists;
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SelectorContactAdapter(Context context, ArrayList<ContactFriendBean.ContactFriendData> contactLists) {
        this.context = context;
        this.contactLists = contactLists;
    }


    @NonNull
    @Override
    public SelectorContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SelectorContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_selector_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectorContactViewHolder holder, int i) {
        ContactFriendBean.ContactFriendData contactClassData = contactLists.get(i);
        String fremarks = contactClassData.fremarks;
        holder.tvName.setText(TextUtils.isEmpty(fremarks) ? contactClassData.friendNickName : fremarks);
        ImageUtils.setNormalImage(context, contactClassData.headImg + "?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, holder.ivHeader);
        holder.ivClose.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(contactClassData, i);
        });
    }


    @Override
    public int getItemCount() {
        return contactLists == null ? 0 : contactLists.size();
    }


    static class SelectorContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_header)
        CircleImageView ivHeader;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.iv_close)
        ImageView ivClose;

        public SelectorContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
