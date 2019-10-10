package com.daoyu.chat.module.home.adapter;

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
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BatchesMoveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String firstLatter = "";
    private Context context;
    private ArrayList<ContactFriendBean.ContactFriendData> contactClassLists;
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public BatchesMoveAdapter(Context context, ArrayList<ContactFriendBean.ContactFriendData> contactClassLists) {
        this.context = context;
        this.contactClassLists = contactClassLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_batches_move_contact_class, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ContactViewHolder) {
            ContactViewHolder contactLifeViewHolder = (ContactViewHolder) holder;

            ContactFriendBean.ContactFriendData contactClassData = contactClassLists.get(i);
            if (i == 0) {
                firstLatter = contactClassData.firstLetter;
                contactLifeViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
                contactLifeViewHolder.tvFirstLetter.setText(firstLatter);
            } else {
                if (!TextUtils.isEmpty(firstLatter) && !firstLatter.equals(contactClassData.firstLetter)) {
                    firstLatter = contactClassData.firstLetter;
                    contactLifeViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
                    contactLifeViewHolder.tvFirstLetter.setText(firstLatter);
                } else {
                    contactLifeViewHolder.tvFirstLetter.setVisibility(View.GONE);
                }
            }
            contactLifeViewHolder.cbContact.setChecked(contactClassData.checked);
            contactLifeViewHolder.tvName.setText(contactClassData.friendNickName);
            ImageUtils.setNormalImage(context, contactClassData.head_img, R.drawable.my_user_default, R.drawable.my_user_default, contactLifeViewHolder.ivHeader);

            contactLifeViewHolder.itemView.setOnClickListener(v -> {
                boolean checked = contactLifeViewHolder.cbContact.isChecked();
                contactClassData.checked = !checked;
                notifyDataSetChanged();
                if (listener == null) return;
                listener.onItemClick(contactClassData, i);
            });
        }

    }

    @Override
    public int getItemCount() {
        return contactClassLists == null ? 0 : contactClassLists.size();
    }


    static class ContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_first_letter)
        TextView tvFirstLetter;
        @BindView(R.id.cb_contact)
        CheckBox cbContact;
        @BindView(R.id.iv_header)
        CircleImageView ivHeader;
        @BindView(R.id.tv_name)
        TextView tvName;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
