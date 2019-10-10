package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.module.home.adapter.GuidePagerAdapter;
import com.daoyu.chat.module.login.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 引导页
 */
public class GuidePageAct extends BaseActivity {
    @BindView(R.id.vp)
    ViewPager pager;
    @BindView(R.id.tv_guide_btn)
    TextView tvGuideBtn;

    GuidePagerAdapter adapter;


    private List<View> guides = new ArrayList<View>();
    private int curPos = 0;          // 记录当前的位置

    private int[] ids = {R.drawable.guide_page1,
            R.drawable.guide_page2,
            R.drawable.guide_page4,
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.act_guide_page;
    }

    @Override
    public void windowSetting() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
    }

    @Override
    protected void initEvent() {
        initPager();
    }

    private void initPager() {
        this.getView();
        ImageView iv = null;
        guides.clear();
        for (int i = 0; i < ids.length; i++) {
            iv = buildImageView(ids[i]);
            guides.add(iv);
        }
        adapter = new GuidePagerAdapter(guides);
        pager.setAdapter(adapter);
        pager.clearAnimation();
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                int pos = position % ids.length;

                if (pos == ids.length - 1) {// 到最后一张了
                    tvGuideBtn.setVisibility(View.VISIBLE);
                } else {
                    tvGuideBtn.setVisibility(View.GONE);
                }
                curPos = pos;
                super.onPageSelected(position);
            }
        });
    }

    private ImageView buildImageView(int id) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(id);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(params);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return iv;
    }

    /**
     * 在layout中实例化一些View
     */
    private void getView() {
        tvGuideBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 点击体验
                startActivity(new Intent(GuidePageAct.this, LoginActivity.class));
                finish();
            }
        });
    }
}
