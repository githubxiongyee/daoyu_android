package com.daoyu.chat.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daoyu.chat.R;

import butterknife.BindView;


public abstract class BaseTitleActivity extends BaseActivity {
    @BindView(R.id.iv_menu)
    public ImageView ivMenu;

    @BindView(R.id.iv_back)
    public ImageView titleBackIv;

    @BindView(R.id.tv_title)
    public TextView tvTitle;

    @BindView(R.id.save_tv)
    public TextView saveTv;

    @BindView(R.id.add_tv)
    public TextView addTv;

    @BindView(R.id.ll_root)
    public LinearLayout llRoot;

    @BindView(R.id.iv_right)
    ImageView ivRight;

    @BindView(R.id.tv_right_title)
    TextView tvRightTitle;

    @Override
    public void setContentView(int layoutResID) {
        View rootLayout = getLayoutInflater().inflate(R.layout.activity_base_title, null, false);
        View contentLayout = getLayoutInflater().inflate(layoutResID, null, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentLayout.setLayoutParams(params);
        FrameLayout container = rootLayout.findViewById(R.id.content_fl);
        container.addView(contentLayout);
        getWindow().setContentView(rootLayout);
    }

    @Override
    public void setListener() {
        super.setListener();
        titleBackIv.setOnClickListener(this);
        saveTv.setOnClickListener(this);
        addTv.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
        tvTitle.setOnClickListener(this);

        ivRight.setOnClickListener(this);
        tvRightTitle.setOnClickListener(this);
    }

    public void showRightTxtTitle(String tvStr) {
        tvRightTitle.setVisibility(View.VISIBLE);
        tvRightTitle.setText(tvStr);
    }

    public void hideRightTxtTitle() {
        tvRightTitle.setVisibility(View.GONE);
    }

    public void hideTitleLayout() {
        llRoot.setVisibility(View.GONE);
    }

    public void setBackBtn(int resId) {
        titleBackIv.setImageResource(resId);
    }

    public void setRightMenu(int[] ResId, boolean doubleMenu) {
        saveTv.setVisibility(View.VISIBLE);
        saveTv.setText(getString(ResId[0]));
        if (doubleMenu && ResId.length > 1) {
            addTv.setVisibility(View.VISIBLE);
            addTv.setText(getString(ResId[1]));
        }
    }


    public void setToolBarColor(int colorId) {
        llRoot.setBackgroundColor(getResources().getColor(colorId));
    }

    public void showMenu() {
        ivMenu.setVisibility(View.VISIBLE);
    }

    public void hideMenu() {
        ivMenu.setVisibility(View.GONE);
    }

    public void showRightAdd() {
        ivRight.setVisibility(View.VISIBLE);
    }

    public void showRightAdd(int resId) {
        ivRight.setImageResource(resId);
        ivRight.setVisibility(View.VISIBLE);
    }

    public void hideRightAdd() {
        ivRight.setVisibility(View.GONE);
    }

    public void showRightAdd(int resId, int w, int h) {
        ivRight.setImageResource(resId);
        ivRight.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = ivRight.getLayoutParams();
        params.width = w;
        params.height = h;
        ivRight.setLayoutParams(params);
        ivRight.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    public void setCurrentTitle(int ResId) {
        tvTitle.setText(getString(ResId));
    }

    public void setCurrentTitle(String title) {
        tvTitle.setText(title);
    }

    public void setCurrentTitle(String title, int color) {
        tvTitle.setText(title);
        tvTitle.setTextColor(color);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

}
