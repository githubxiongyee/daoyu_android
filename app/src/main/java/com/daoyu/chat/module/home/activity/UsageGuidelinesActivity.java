package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;

import butterknife.BindView;

public class UsageGuidelinesActivity extends BaseActivity {
    @BindView(R.id.cl_root)
    ConstraintLayout clRoot;
    @BindView(R.id.cl_1)
    ConstraintLayout cl1;
    @BindView(R.id.cl_2)
    ConstraintLayout cl2;
    @BindView(R.id.cl_3)
    ConstraintLayout cl3;
    @BindView(R.id.tv_next1)
    TextView tvNext1;
    @BindView(R.id.tv_next2)
    TextView tvNext2;
    @BindView(R.id.tv_know)
    TextView tvKnow;

    @Override
    public void windowSetting() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_usage_guidelines;
    }

    @Override
    protected void initEvent() {
        cl1.setVisibility(View.VISIBLE);
        cl2.setVisibility(View.GONE);
        cl3.setVisibility(View.GONE);

        cl1.setOnClickListener(this);
        cl2.setOnClickListener(this);
        cl3.setOnClickListener(this);
        tvNext1.setOnClickListener(this);
        tvNext2.setOnClickListener(this);
        tvKnow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.cl_1:
                cl1.setVisibility(View.GONE);
                cl2.setVisibility(View.VISIBLE);
                cl3.setVisibility(View.GONE);
                break;
            case R.id.cl_2:
                cl1.setVisibility(View.GONE);
                cl2.setVisibility(View.GONE);
                cl3.setVisibility(View.VISIBLE);
                break;
            case R.id.cl_3:
                break;
            case R.id.tv_next1:
                cl1.setVisibility(View.GONE);
                cl2.setVisibility(View.VISIBLE);
                cl3.setVisibility(View.GONE);
                break;
            case R.id.tv_next2:
                cl1.setVisibility(View.GONE);
                cl2.setVisibility(View.GONE);
                cl3.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_know:
                cl1.setVisibility(View.GONE);
                cl2.setVisibility(View.GONE);
                cl3.setVisibility(View.VISIBLE);
                startActivity(new Intent(this,MainActivity.class));
                finish();
                break;
        }
    }

}
