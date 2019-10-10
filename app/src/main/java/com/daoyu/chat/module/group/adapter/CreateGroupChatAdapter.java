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
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateGroupChatAdapter extends RecyclerView.Adapter<CreateGroupChatAdapter.ContactViewHolder> {
    private String firstLatter = "";
    private Context context;
    private ArrayList<ContactFriendBean.ContactFriendData> contactClassLists;

    public CreateGroupChatAdapter(Context context, ArrayList<ContactFriendBean.ContactFriendData> contactClassLists) {
        this.context = context;
        this.contactClassLists = contactClassLists;
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_create_group_chat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int i) {
        ContactFriendBean.ContactFriendData contactClassData = contactClassLists.get(i);
        if (i == 0) {
            firstLatter = contactClassData.firstLetter;
            holder.tvFirstLetter.setVisibility(View.VISIBLE);
            holder.tvFirstLetter.setText(firstLatter);
        } else {
            if (!TextUtils.isEmpty(firstLatter) && !firstLatter.equals(contactClassData.firstLetter)) {
                firstLatter = contactClassData.firstLetter;
                holder.tvFirstLetter.setVisibility(View.VISIBLE);
                holder.tvFirstLetter.setText(firstLatter);
            } else {
                holder.tvFirstLetter.setVisibility(View.GONE);
            }
        }
        String fremarks = contactClassData.fremarks;
        holder.tvName.setText(TextUtils.isEmpty(fremarks) ? contactClassData.friendNickName : fremarks);
        ImageUtils.setNormalImage(context, contactClassData.headImg + "?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, holder.ivHeader);

        holder.cbChecked.setChecked(contactClassData.checked);

        holder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemViewClick(contactClassData, i);
        });
    }


    @Override
    public int getItemCount() {
        return contactClassLists == null ? 0 : contactClassLists.size();
    }


    static class ContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_first_letter)
        TextView tvFirstLetter;
        @BindView(R.id.cb_checked)
        CheckBox cbChecked;
        @BindView(R.id.iv_header)
        CircleImageView ivHeader;
        @BindView(R.id.tv_name)
        TextView tvName;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemViewClick(ContactFriendBean.ContactFriendData contactClassData, int position);
    }
}
