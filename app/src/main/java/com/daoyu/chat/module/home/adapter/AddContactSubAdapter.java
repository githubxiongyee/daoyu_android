package com.daoyu.chat.module.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.im.module.ApplyFriendTable;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddContactSubAdapter extends RecyclerView.Adapter<AddContactSubAdapter.AddContactSubViewHolder> {
    private Context context;
    private ArrayList<ApplyFriendTable> qosAddContacts;
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AddContactSubAdapter(Context context, ArrayList<ApplyFriendTable> qosAddContacts) {
        this.context = context;
        this.qosAddContacts = qosAddContacts;
    }

    @NonNull
    @Override
    public AddContactSubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AddContactSubViewHolder(LayoutInflater.from(context).inflate(R.layout.item_friend_apply_sub_list, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddContactSubViewHolder holder, int i) {
        ApplyFriendTable qosAddContact = qosAddContacts.get(i);
        ImageUtils.setNormalImage(context, qosAddContact.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, holder.ivHeadImg);
        holder.tvTitle.setText(TextUtils.isEmpty(qosAddContact.username) ? "" : qosAddContact.username);
        holder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(qosAddContact, i);
        });
        String status = qosAddContact.status;
        switch (status) {
            case "1":
                holder.tvAddContact.setText("可添加");
                holder.tvAddContact.setEnabled(true);
                holder.tvAddContact.setTextColor(context.getResources().getColor(R.color.colorWhite));
                break;
            case "2":
                holder.tvAddContact.setEnabled(false);
                holder.tvAddContact.setTextColor(context.getResources().getColor(R.color.color_808080));
                holder.tvAddContact.setText("已添加");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return qosAddContacts == null ? 0 : qosAddContacts.size();
    }

    static class AddContactSubViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_headImg)
        CircleImageView ivHeadImg;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_add_contact)
        TextView tvAddContact;

        public AddContactSubViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
