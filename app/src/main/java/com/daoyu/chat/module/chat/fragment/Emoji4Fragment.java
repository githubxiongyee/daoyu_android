package com.daoyu.chat.module.chat.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseFragment;
import com.daoyu.chat.event.EmojiEvent;
import com.daoyu.chat.module.chat.adapter.EmojiAdapter;
import com.daoyu.chat.module.chat.bean.EmojiBean;
import com.daoyu.chat.utils.GridSpacingItemDecoration;
import com.daoyu.chat.utils.ToolsUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 默认表情第一页
 */
public class Emoji4Fragment extends BaseFragment implements EmojiAdapter.OnEmojiClickListener {


    @BindView(R.id.rv_more)
    RecyclerView rvMore;
    private EmojiAdapter adapter;
    private ArrayList<EmojiBean> emojis1;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_default_more;
    }

    @Override
    protected void initEvent() {
        rvMore.setLayoutManager(new GridLayoutManager(getContext(), 8));
        rvMore.addItemDecoration(new GridSpacingItemDecoration(8, getResources().getDimensionPixelOffset(R.dimen.dp_4), true));
        emojis1 = new ArrayList<>();
        emojis1.clear();
        String emoji = ToolsUtil.getJson(getContext(), "emoji.json");
        ArrayList<EmojiBean> emojis = new Gson().fromJson(emoji, new TypeToken<ArrayList<EmojiBean>>() {
        }.getType());
        if (emojis != null && emojis.size() > 0) {
            for (int i = 72; i < 96; i++) {
                EmojiBean emojiBean = emojis.get(i);
                emojis1.add(emojiBean);
            }
        }
        adapter = new EmojiAdapter(getContext(), emojis1);
        adapter.setListener(this);
        rvMore.setAdapter(adapter);
    }

    @Override
    public void onEmojiClickListener(EmojiBean emojiBean, int position) {
        if (emojiBean == null) return;
        EventBus.getDefault().post(new EmojiEvent(emojiBean.code));
    }
}
