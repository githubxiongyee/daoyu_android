package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
import android.widget.ImageView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.utils.ImageUtils;

import butterknife.BindView;

public class CommonContactImageActivity extends BaseTitleActivity {

    @BindView(R.id.iv_image)
    ImageView ivImage;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_common_contact_image;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String title = intent.getStringExtra(Constant.TITLE);
        setCurrentTitle(title);
        int ids = intent.getIntExtra(Constant.IMAGE_ID, -1);
        if (ids == -1) return;
        ImageUtils.setNormalImage(this, ids, ivImage);
    }
}
