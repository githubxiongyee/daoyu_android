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
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER_LAYOUT = 0;
    public static final int CONTACT_LAYOUT = 1;

    private Context context;
    private ArrayList<ContactFriendBean.ContactFriendData> contactFriends;
    private OnContactItemViewClickListener listener;
    private String currentLetter;
    public int number = 0;

    public void setNumber(int number) {
        this.number = number;
        notifyItemChanged(0);
    }

    public ContactAdapter(Context context, ArrayList<ContactFriendBean.ContactFriendData> contactFriends) {
        this.context = context;
        this.contactFriends = contactFriends;
    }

    public void setListener(OnContactItemViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_LAYOUT;
        } else {
            return CONTACT_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == HEADER_LAYOUT) {
            return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact_menu, viewGroup, false));
        }
        return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact_friend, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.tvAddContact.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onContactMenu(Constant.InnerContact.ADD_CONTACT);
            });
            headerViewHolder.tvLife.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onContactMenu(Constant.InnerContact.LIFT);
            });
            headerViewHolder.tvWork.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onContactMenu(Constant.InnerContact.WORK);
            });
            headerViewHolder.tvGroup.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onContactMenu(Constant.InnerContact.GROUP);
            });
            headerViewHolder.tvNearby.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onContactMenu(Constant.InnerContact.NEARBY);
            });
            headerViewHolder.tvBlacklist.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onContactMenu(Constant.InnerContact.BLACKLIST);
            });
            if (number == 0) {
                headerViewHolder.tvNumber.setVisibility(View.GONE);
            } else {
                headerViewHolder.tvNumber.setText(String.valueOf(number));
                headerViewHolder.tvNumber.setVisibility(View.VISIBLE);
            }


        } else if (holder instanceof ContactViewHolder) {
            List<Integer> pos = new ArrayList<>();
            pos.clear();
            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            int index = position - 1;//2
            ContactFriendBean.ContactFriendData friendData = contactFriends.get(index);
            String tag = friendData.firstLetter;
            if (index == 0) {
                currentLetter = tag;
                contactViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
                contactViewHolder.tvFirstLetter.setText(tag);
            } else {
                if (!currentLetter.equals(tag)) {
                    currentLetter = tag;
                    contactViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
                    contactViewHolder.tvFirstLetter.setText(tag);
                } else {
                    contactViewHolder.tvFirstLetter.setVisibility(View.GONE);
                }
            }
            ImageUtils.setNormalImage(context, friendData.headImg+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, contactViewHolder.ivAvatar);
            contactViewHolder.tvName.setText(TextUtils.isEmpty(friendData.fremarks) ? friendData.friendNickName : friendData.fremarks);
            contactViewHolder.itemView.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onContactFriend(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return contactFriends == null ? 1 : contactFriends.size() + 1;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_add_contact)
        TextView tvAddContact;
        @BindView(R.id.tv_life)
        TextView tvLife;
        @BindView(R.id.tv_work)
        TextView tvWork;
        @BindView(R.id.tv_group)
        TextView tvGroup;
        @BindView(R.id.tv_nearby)
        TextView tvNearby;
        @BindView(R.id.tv_blacklist)
        TextView tvBlacklist;
        @BindView(R.id.tv_number)
        TextView tvNumber;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar)
        CircleImageView ivAvatar;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_first_letter)
        TextView tvFirstLetter;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnContactItemViewClickListener {
        void onContactMenu(int menu);


        void onContactFriend(int position);
    }
}
