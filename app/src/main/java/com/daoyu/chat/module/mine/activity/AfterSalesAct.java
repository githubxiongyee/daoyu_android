package com.daoyu.chat.module.mine.activity;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;

public class AfterSalesAct extends BaseTitleActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.act_after_sales;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("售后服务",R.color.colorBlack);
        setToolBarColor(R.color.color_F8F8F8);
        setBackBtn(R.drawable.btn_back);
    }
}
