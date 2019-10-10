package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.chat.adapter.ImageViewPagerAdapter;
import com.daoyu.chat.module.im.module.MessageDetailTable;

import java.util.ArrayList;

import butterknife.BindView;

public class ChatBigImageActivity extends BaseTitleActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<MessageDetailTable> arrayList;
    private ImageViewPagerAdapter adapter;
    private String imageUrl;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_chat_big_image;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("预览");
        Intent intent = getIntent();
        arrayList = intent.getParcelableArrayListExtra(Constant.CHAT_IMAGE_LIST);
        imageUrl = intent.getStringExtra(Constant.IMAGE_LARGE);
        adapter = new ImageViewPagerAdapter(arrayList, this);

        viewPager.setAdapter(adapter);
        if (TextUtils.isEmpty(imageUrl)) return;
        int isPosition = -1;
        for (int i = 0; i < arrayList.size(); i++) {
            MessageDetailTable table = arrayList.get(i);
            if (table.message.equals(imageUrl)) {
                isPosition = i;
                break;
            }

        }
        if (isPosition == -1) return;
        viewPager.setCurrentItem(isPosition);
    }
}
