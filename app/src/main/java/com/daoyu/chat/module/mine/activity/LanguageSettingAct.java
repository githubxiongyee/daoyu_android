package com.daoyu.chat.module.mine.activity;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;

public class LanguageSettingAct extends BaseTitleActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.act_language_setting;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("语言设置");
    }
}
