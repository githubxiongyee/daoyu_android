package com.daoyu.chat.module.group.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.module.group.bean.GroupRedpackageBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedBagGroupChatAdapter extends RecyclerView.Adapter<RedBagGroupChatAdapter.ContactViewHolder> {

    private Context context;
    private ArrayList<GroupRedpackageBean.GroupRedpackageData> mGroupRedpackageData;

    public RedBagGroupChatAdapter(Context context, ArrayList<GroupRedpackageBean.GroupRedpackageData> groupRedpackageData) {
        this.context = context;
        mGroupRedpackageData = groupRedpackageData;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_group_red_bag, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int i) {
        GroupRedpackageBean.GroupRedpackageData groupRedpackageData = mGroupRedpackageData.get(i);
        ImageUtils.setNormalImage(context,groupRedpackageData.headeImag,holder.ivReceiveHeader);
        holder.tvReceiveName.setText(groupRedpackageData.name);
        holder.tvTime.setText(ToolsUtil.formatTime(groupRedpackageData.createTime,"MM-DD HH-mm"));
        holder.tvRadPackageMoney.setText(String.format("%.2f元", groupRedpackageData.vipCredit));
    }


    @Override
    public int getItemCount() {
        return mGroupRedpackageData == null ? 0 : mGroupRedpackageData.size();
    }


    static class ContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_receive_header)
        CircleImageView ivReceiveHeader;
        @BindView(R.id.tv_receive_name)
        TextView tvReceiveName;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_rad_package_money)
        TextView tvRadPackageMoney;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
