package com.daoyu.chat.module.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LifeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SEARCH_LAYOUT = 0;
    public static final int CONTACT_LAYOUT = 1;
    private String firstLatter = "";
    private boolean isbalck = false;

    public void setIsbalck(boolean isbalck) {
        this.isbalck = isbalck;
        notifyDataSetChanged();
    }

    private Context context;
    private ArrayList<ContactFriendBean.ContactFriendData> contactClassLists;

    public LifeAdapter(Context context, ArrayList<ContactFriendBean.ContactFriendData> contactClassLists) {
        this.context = context;
        this.contactClassLists = contactClassLists;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return SEARCH_LAYOUT;
        } else {
            return CONTACT_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == SEARCH_LAYOUT) {
            return new SearchHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_class, viewGroup, false));
        } else {
            return new ContactLifeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact_class, viewGroup, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof SearchHeaderViewHolder) {
            SearchHeaderViewHolder searchHeaderViewHolder = (SearchHeaderViewHolder) holder;
            searchHeaderViewHolder.editSearch.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus){
                    searchHeaderViewHolder.editSearch.setCursorVisible(true);
                }else {
                    searchHeaderViewHolder.editSearch.setCursorVisible(false);
                }
            });
            searchHeaderViewHolder.editSearch.addTextChangedListener(new CommonTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (listener == null) return;
                    listener.onEditViewAfterTextChanged(s);
                }
            });
        } else if (holder instanceof ContactLifeViewHolder) {
            ContactLifeViewHolder contactLifeViewHolder = (ContactLifeViewHolder) holder;
            int index = (i - 1);
            ContactFriendBean.ContactFriendData contactClassData = contactClassLists.get(index);
            if (index == 0) {
                firstLatter = contactClassData.firstLetter;
                contactLifeViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
                contactLifeViewHolder.tvFirstLetter.setText(firstLatter);
            } else {
                if (!TextUtils.isEmpty(firstLatter)&&!firstLatter.equals(contactClassData.firstLetter)) {
                    firstLatter = contactClassData.firstLetter;
                    contactLifeViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
                    contactLifeViewHolder.tvFirstLetter.setText(firstLatter);
                } else {
                    contactLifeViewHolder.tvFirstLetter.setVisibility(View.GONE);
                }
            }
            if (isbalck){
                contactLifeViewHolder.tvMove.setText("移出");
            }else {
                contactLifeViewHolder.tvMove.setText("移动");
            }
            String fremarks = contactClassData.fremarks;
            contactLifeViewHolder.tvName.setText(TextUtils.isEmpty(fremarks) ? contactClassData.friendNickName : fremarks);
            ImageUtils.setNormalImage(context, contactClassData.headImg+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, contactLifeViewHolder.ivHeader);
            contactLifeViewHolder.tvMove.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onMoveViewClick(contactClassData, i);
            });
            contactLifeViewHolder.itemView.setOnClickListener(v -> {
                if (listener == null) return;
                listener.onItemViewClick(contactClassData, i);
            });

        }
    }

    @Override
    public int getItemCount() {
        return contactClassLists == null ? 1 : contactClassLists.size() + 1;
    }

    static class SearchHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.edit_search)
        EditText editSearch;

        public SearchHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ContactLifeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_first_letter)
        TextView tvFirstLetter;
        @BindView(R.id.iv_header)
        CircleImageView ivHeader;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_move)
        TextView tvMove;

        public ContactLifeViewHolder(@NonNull View itemView) {
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

        void onMoveViewClick(ContactFriendBean.ContactFriendData contactClassData, int position);

        void onEditViewAfterTextChanged(Editable s);
    }
}
