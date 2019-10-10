package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.widget.ImageView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.utils.ImageUtils;

import butterknife.BindView;

/**
 * 好友头像查看
 */
public class FriendHeaderActivity extends BaseActivity {
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.iv_finnish)
    ImageView ivFinnish;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_friend_header;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        String header = intent.getStringExtra(Constant.CONTACT_HEADER);
        ImageUtils.setNormalImage(this, header, R.drawable.ic_placeholder, R.drawable.ic_placeholder, ivHeader);
        ivFinnish.setOnClickListener(v -> finish());

    }
}
