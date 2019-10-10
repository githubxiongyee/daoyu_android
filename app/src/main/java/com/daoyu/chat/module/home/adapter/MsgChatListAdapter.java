package com.daoyu.chat.module.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.module.chat.view.SwipeMenuLayout;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MsgChatListAdapter extends RecyclerView.Adapter<MsgChatListAdapter.MsgChatListViewHolder> {
    private List<ChatTable> chatTables;
    private Context context;
    private OnMsgChatListClickListener listener;


    public void setListener(OnMsgChatListClickListener listener) {
        this.listener = listener;
    }

    public MsgChatListAdapter(List<ChatTable> chatTables, Context context) {
        this.chatTables = chatTables;
        this.context = context;

    }

    @NonNull
    @Override
    public MsgChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MsgChatListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MsgChatListViewHolder msgChatListViewHolder, int i) {
        ChatTable chatTable = chatTables.get(i);
        String friendNickName = chatTable.username;
        msgChatListViewHolder.tvTitle.setText(TextUtils.isEmpty(friendNickName) ? chatTable.mobile : friendNickName);
        msgChatListViewHolder.tvDesc.setText(chatTable.last_message);
        msgChatListViewHolder.tvDate.setText(getDateToString(Long.parseLong(chatTable.message_time)));

        boolean is_private = chatTable.is_private;
        if (is_private){
            msgChatListViewHolder.swipe.setSwipeEnable(false);
            msgChatListViewHolder.ivHeadImg.setImageDrawable(context.getResources().getDrawable(R.drawable.home_customerservice_user));
        }else {
            ImageUtils.setNormalImage(context, chatTable.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, msgChatListViewHolder.ivHeadImg);
            msgChatListViewHolder.swipe.setSwipeEnable(true);
        }

        msgChatListViewHolder.clItem.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));


        boolean top = chatTable.top;
        msgChatListViewHolder.clItem.setBackgroundColor(top ? context.getResources().getColor(R.color.color_F5F5F5) : context.getResources().getColor(R.color.colorWhite));

        boolean shield = chatTable.shield;
        msgChatListViewHolder.cbShied.setChecked(true);
        if (shield) {
            msgChatListViewHolder.cbShied.setVisibility(View.VISIBLE);
            int number = chatTable.number;
            msgChatListViewHolder.tvNumber.setVisibility(View.GONE);
            if (number == 0) {
                msgChatListViewHolder.btnUnRead.setText("标记未读");
                msgChatListViewHolder.cbShied.setChecked(false);
            }else {
                msgChatListViewHolder.btnUnRead.setText("标记已读");
                msgChatListViewHolder.tvDesc.setText(String.format("(%d条信息)%s", number, chatTable.last_message));
            }
        } else {
            msgChatListViewHolder.cbShied.setVisibility(View.GONE);
            int number = chatTable.number;
            if (number == 0) {
                msgChatListViewHolder.btnUnRead.setText("标记未读");
                msgChatListViewHolder.tvNumber.setVisibility(View.GONE);
            } else if (number >= 99) {
                msgChatListViewHolder.btnUnRead.setText("标记已读");
                msgChatListViewHolder.tvNumber.setVisibility(View.VISIBLE);
                msgChatListViewHolder.tvNumber.setText("99+");
            } else {
                msgChatListViewHolder.btnUnRead.setText("标记已读");
                msgChatListViewHolder.tvNumber.setVisibility(View.VISIBLE);
                msgChatListViewHolder.tvNumber.setText(String.valueOf(number));
            }
        }

        msgChatListViewHolder.clItem.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClickMSGListener(i, chatTable);
        });

        msgChatListViewHolder.btnDelete.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onSlideMenuClickListener("delete", i, chatTable);
        });

        msgChatListViewHolder.btnUnRead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onSlideMenuClickListener("unRead", i, chatTable);
        });
    }

    @Override
    public int getItemCount() {
        return chatTables == null ? 0 : chatTables.size();
    }

    static class MsgChatListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_headImg)
        CircleImageView ivHeadImg;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_desc)
        TextView tvDesc;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.btnUnRead)
        Button btnUnRead;
        @BindView(R.id.btnDelete)
        Button btnDelete;
        @BindView(R.id.cl_item)
        ConstraintLayout clItem;
        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.swipe)
        SwipeMenuLayout swipe;
        @BindView(R.id.cb_shied)
        CheckBox cbShied;

        public MsgChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnMsgChatListClickListener {
        void onSlideMenuClickListener(String menu, int position, ChatTable chatTable);

        void onItemClickMSGListener(int position, ChatTable chatTable);
    }


    public static String getDateToString(long milSecond) {
        Date date = new Date(milSecond);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(date);
        String now = sdf.format(new Date());
        if (param.equals(now)) {
            //今天
            return new SimpleDateFormat("HH:mm").format(milSecond);
        } else {
            return new SimpleDateFormat("MM月dd日").format(milSecond);
        }

    }


}
