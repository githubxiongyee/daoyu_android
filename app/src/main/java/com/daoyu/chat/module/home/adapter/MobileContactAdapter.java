package com.daoyu.chat.module.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.utils.ContactMobileBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MobileContactAdapter extends RecyclerView.Adapter<MobileContactAdapter.MobileContactViewHolder> {

    private Context context;
    private ArrayList<ContactMobileBean.ContactMobileData> allContacts;
    private String currentLetter;
    private OnItemViewClickMobile clickMobile;

    public void setClickMobile(OnItemViewClickMobile clickMobile) {
        this.clickMobile = clickMobile;
    }

    public MobileContactAdapter(Context context, ArrayList<ContactMobileBean.ContactMobileData> allContacts) {
        this.context = context;
        this.allContacts = allContacts;
    }

    @NonNull
    @Override
    public MobileContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MobileContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mobile_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MobileContactViewHolder mobileContactViewHolder, int i) {
        ContactMobileBean.ContactMobileData contactMobileData = allContacts.get(i);
        String tag = contactMobileData.firstLetter;
        if (i == 0) {
            currentLetter = tag;
            mobileContactViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
            mobileContactViewHolder.tvFirstLetter.setText(tag);
        } else {
            if (!currentLetter.equals(tag)) {
                currentLetter = tag;
                mobileContactViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
                mobileContactViewHolder.tvFirstLetter.setText(tag);
            } else {
                mobileContactViewHolder.tvFirstLetter.setVisibility(View.GONE);
            }
        }
        mobileContactViewHolder.tvMobile.setText(contactMobileData.mobile);
        mobileContactViewHolder.tvName.setText(contactMobileData.name);
        String isUser = contactMobileData.isUser;
        if ("N".equals(isUser)) {//不是我们的用户
            mobileContactViewHolder.tvAddToContact.setText("邀请");
            mobileContactViewHolder.tvAddToContact.setEnabled(true);
        } else {
            String isFriend = contactMobileData.isFriend;
            if ("N".equals(isFriend)) {
                mobileContactViewHolder.tvAddToContact.setText("添加");
                mobileContactViewHolder.tvAddToContact.setEnabled(true);
            } else {
                mobileContactViewHolder.tvAddToContact.setText("已添加");
                mobileContactViewHolder.tvAddToContact.setEnabled(false);
            }
        }

        mobileContactViewHolder.itemView.setOnClickListener(v -> {
            if (clickMobile == null) return;
            clickMobile.onItemClickListener(i, contactMobileData, mobileContactViewHolder.tvAddToContact.getText().toString().trim());
        });
    }

    @Override
    public int getItemCount() {
        return allContacts == null ? 0 : allContacts.size();
    }

    static class MobileContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_first_letter)
        TextView tvFirstLetter;
        @BindView(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_mobile)
        TextView tvMobile;
        @BindView(R.id.tv_add_to_contact)
        TextView tvAddToContact;
        public MobileContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemViewClickMobile {
        void onItemClickListener(int position, ContactMobileBean.ContactMobileData contactMobileData, String type);
    }
}
