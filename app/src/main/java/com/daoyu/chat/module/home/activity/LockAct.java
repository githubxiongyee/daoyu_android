package com.daoyu.chat.module.home.activity;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;

public class LockAct extends BaseTitleActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.act_lock;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("智能锁管理");
        setToolBarColor(R.color.blue_bg);
    }
}
