package com.daoyu.chat.module.mine.adapter;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.utils.SharedPreferenceUtil;

import java.util.List;

/**
 * 皮肤-背景
 */
public class FaceBgAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public FaceBgAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ImageView ivBg = helper.getView(R.id.iv_bg);
        if (helper.getPosition()!=0){
            ivBg.setImageDrawable(helper.itemView.getContext().getResources().getDrawable(loadBgData(helper.getPosition()-1)));
        }
        ImageView ivSelect = helper.getView(R.id.iv_select);
        int anInt = SharedPreferenceUtil.getInstance().getInt(Constant.FACE,0);
        selectBgData(anInt);
        if (bgIds[helper.getPosition()]) {
            ivSelect.setVisibility(View.VISIBLE);
        } else {
            ivSelect.setVisibility(View.GONE);
        }
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBgData(helper.getPosition());
                SharedPreferenceUtil.getInstance().putInt(Constant.FACE, helper.getPosition());
                notifyDataSetChanged();
            }
        });

    }

    private int loadBgData(int position) {
        int[] bgIds = new int[]{R.drawable.my_skin_bubble_background_bg_one,
                R.drawable.my_skin_bubble_background_bg_two};
        return bgIds[position];
    }

    boolean[] bgIds = new boolean[]{true,
            false,
            false};

    private boolean selectBgData(int position) {

        for (int i = 0; i < bgIds.length; i++) {
            if (i == position) {
                bgIds[i] = true;
            } else {
                bgIds[i] = false;
            }
        }
        return bgIds[position];
    }

}
