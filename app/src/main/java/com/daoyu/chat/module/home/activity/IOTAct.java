package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.home.adapter.IotAdapter;

import butterknife.BindView;

public class IOTAct extends BaseTitleActivity implements OnItemClickListener {
    @BindView(R.id.rv)
    RecyclerView rv;
    IotAdapter adapter;
    @Override
    protected int getLayoutResId() {
        return R.layout.act_iot;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("IOT", R.color.colorBlack);
        setToolBarColor(R.color.colorWhite);
        setBackBtn(R.drawable.btn_back);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IotAdapter(this);
        rv.setAdapter(adapter);
        adapter.setListener(this);
    }

    @Override
    public void onItemClick(Object data, int position) {
        /*if (data instanceof Integer) {
            int pos = (int) data;
            switch (pos) {
                case 0:
                    startActivity(new Intent(this,LockAct.class));
                    break;

            }
        }*/
        startActivity(new Intent(this,LockAct.class));
    }
}
