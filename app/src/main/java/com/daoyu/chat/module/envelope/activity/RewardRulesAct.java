package com.daoyu.chat.module.envelope.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.envelope.adapter.RulesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RewardRulesAct extends BaseTitleActivity {
    @BindView(R.id.rv)
    RecyclerView rv;
    RulesAdapter adapter;
    @Override
    protected int getLayoutResId() {
        return R.layout.act_reward_rules;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("红包奖励",R.color.colorBlack);
        setToolBarColor(R.color.color_F8F8F8);
        setBackBtn(R.drawable.btn_back);

        List<String> strings = new ArrayList<>();
        strings.add("一条");
        adapter = new RulesAdapter(R.layout.adapter_rules,strings);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }
}
