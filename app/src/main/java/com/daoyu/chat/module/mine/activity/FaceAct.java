package com.daoyu.chat.module.mine.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.mine.adapter.FaceBgAdapter;
import com.daoyu.chat.module.mine.adapter.FacePopAdapter;
import com.daoyu.chat.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FaceAct extends BaseTitleActivity {
    @BindView(R.id.rg_top)
    RadioGroup rgTop;
    @BindView(R.id.rb_pop)
    RadioButton rbPop;
    @BindView(R.id.rb_bg)
    RadioButton rbBg;
    @BindView(R.id.rv_face)
    RecyclerView rvFace;

    FacePopAdapter popAdapter;
    FaceBgAdapter bgAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.act_face;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("皮肤");

        rbPop.setChecked(true);

        popAdapter = new FacePopAdapter(R.layout.item_face_pop_rv, test());
        bgAdapter = new FaceBgAdapter(R.layout.item_face_bg_rv, test2());
        rvFace.setLayoutManager(new GridLayoutManager(FaceAct.this, 3));
        rvFace.addItemDecoration(new GridSpacingItemDecoration(3, this.getResources().getDimensionPixelOffset(R.dimen.dp_3), true));
        rvFace.setAdapter(popAdapter);

        initRgListener();
    }


    private void initRgListener() {
        rgTop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_pop:
                        rvFace.setAdapter(popAdapter);
                        break;
                    case R.id.rb_bg:
                        rvFace.setAdapter(bgAdapter);
                        break;
                }
            }
        });
    }

    private List<String> test() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            data.add("" + i);
        }
        return data;
    }

    private List<String> test2() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            data.add("" + i);
        }
        return data;
    }
}
