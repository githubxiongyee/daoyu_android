package com.daoyu.chat.module.mine.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.mine.adapter.ExpressionListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ExpressionAct extends BaseTitleActivity {
    @BindView(R.id.rv_hot)
    RecyclerView rvHot;

    @Override
    protected int getLayoutResId() {
        return R.layout.act_expression;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("表情");
        showRightAdd(R.drawable.my_emoticon_setting);
        rvHot.setLayoutManager(new LinearLayoutManager(this));
        List<String> data= new ArrayList<>();
        for (int i = 0;i < 3;i++){
            data.add(i+"号表情");
        }
        rvHot.setAdapter(new ExpressionListAdapter(ExpressionAct.this));
    }
}
