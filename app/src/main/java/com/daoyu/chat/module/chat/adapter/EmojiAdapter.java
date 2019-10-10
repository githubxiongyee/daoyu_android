package com.daoyu.chat.module.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daoyu.chat.R;
import com.daoyu.chat.module.chat.bean.EmojiBean;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {

    private Context context;
    private List<EmojiBean> moreMenus;

    public EmojiAdapter(Context context, List<EmojiBean> moreMenus) {
        this.context = context;
        this.moreMenus = moreMenus;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EmojiViewHolder(LayoutInflater.from(context).inflate(R.layout.item_emoji, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int i) {
        EmojiBean emojiBean = moreMenus.get(i);
        String drawableName = emojiBean.png.split("\\.")[0];
        int drableId = getDrableId(context, drawableName);
        holder.ivEmoji.setImageResource(getResourceByReflect(drawableName));
        holder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onEmojiClickListener(emojiBean, i);
        });
    }

    @Override
    public int getItemCount() {
        return moreMenus == null ? 0 : moreMenus.size();
    }

    static class EmojiViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_emoji)
        ImageView ivEmoji;

        public EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnEmojiClickListener listener;

    public void setListener(OnEmojiClickListener listener) {
        this.listener = listener;
    }

    public interface OnEmojiClickListener {
        void onEmojiClickListener(EmojiBean emojiBean, int position);
    }

    public static int getDrableId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "drable", context.getPackageName());
    }

    public int getResourceByReflect(String imageName) {
        Class drawable = R.drawable.class;
        Field field = null;
        int r_id;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id = 0;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }
}
