package com.daoyu.chat.module.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.im.module.ApplyFriendTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddContactAdapter extends RecyclerView.Adapter<AddContactAdapter.AddContactViewHolder> {

    private Context context;
    private ArrayList<ApplyFriendTable> qosAddFirents;
    private OnItemClickListener listener;
    private long timeInMillis;
    private AddContactSubAdapter adapter;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AddContactAdapter(Context context, ArrayList<ApplyFriendTable> applyFriends) {
        this.context = context;
        this.qosAddFirents = applyFriends;
        Date date = new Date();      //获取当前时间
        Calendar calendar = Calendar.getInstance(); // 得到日历
        calendar.setTime(date);// 把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -3);// 设置为3天前
        timeInMillis = calendar.getTimeInMillis();

    }

    @NonNull
    @Override
    public AddContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AddContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_friend_apply_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddContactViewHolder addContactViewHolder, int i) {
        adapter = (AddContactSubAdapter) addContactViewHolder.rvApplyList.getAdapter();
        ArrayList<ApplyFriendTable> data = new ArrayList<>();
        if (adapter == null) {
            adapter = new AddContactSubAdapter(context, data);
        }
        adapter.setListener((data1, position) -> {
            int subPosition = position;
            if (listener == null) return;
            listener.onItemClick(i, subPosition);

        });
        addContactViewHolder.rvApplyList.setAdapter(adapter);

        if (i == 0) {
            addContactViewHolder.tvTimestamp.setText("近三天");
            data.clear();
            for (int j = 0; j < qosAddFirents.size(); j++) {
                ApplyFriendTable applyFriendTable = qosAddFirents.get(j);
                long time = Long.parseLong(applyFriendTable.time);
                if (time > timeInMillis) {
                    data.add(applyFriendTable);
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            addContactViewHolder.tvTimestamp.setText("三天前");
            data.clear();
            for (int j = 0; j < qosAddFirents.size(); j++) {
                ApplyFriendTable applyFriendTable = qosAddFirents.get(j);
                long time = Long.parseLong(applyFriendTable.time);
                if (time < timeInMillis) {
                    data.add(applyFriendTable);
                }
            }
            adapter.notifyDataSetChanged();
        }


    }

    @Override
    public int getItemCount() {
        return 2;
    }

    static class AddContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_timestamp)
        TextView tvTimestamp;

        @BindView(R.id.rv_apply_list)
        RecyclerView rvApplyList;

        public AddContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rvApplyList.setLayoutManager(new LinearLayoutManager(itemView.getContext()) {
                                             @Override
                                             public boolean canScrollVertically() {
                                                 return false;
                                             }
                                         }
            );
        }
    }
}
