package com.daoyu.chat.module.mine.adapter;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.utils.SharedPreferenceUtil;

import java.util.List;

/**
 * 皮肤-泡泡
 */
public class FacePopAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public FacePopAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ConstraintLayout clParent = helper.getView(R.id.cl_parent);
        DisplayMetrics display = helper.itemView.getContext().getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = display.widthPixels - 6;
        int w = screenWidth / 3;
        ViewGroup.LayoutParams params = clParent.getLayoutParams();
        params.height = w;
        clParent.setLayoutParams(params);
        clParent.setBackground(helper.itemView.getContext().getResources().getDrawable(loadBgData(helper.getPosition())));
        TextView textView = helper.getView(R.id.tv);
        textView.setBackground(helper.itemView.getContext().getResources().getDrawable(loadPopData(helper.getPosition())));
        ImageView ivSelect = helper.getView(R.id.iv_select);
        int selectIndex = SharedPreferenceUtil.getInstance().getInt(Constant.POP,0);
        selectBgData(selectIndex);
        if (bgIds[helper.getPosition()]) {
            ivSelect.setVisibility(View.VISIBLE);
        } else {
            ivSelect.setVisibility(View.GONE);
        }
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBgData(helper.getPosition());
                SharedPreferenceUtil.getInstance().putInt(Constant.POP,helper.getPosition());
                notifyDataSetChanged();
            }
        });

    }

    private int loadPopData(int position) {
        int[] popBgIds = new int[]{R.drawable.my_skin_bubble_default,
                R.drawable.my_skin_bubble_chatbox_one,
                R.drawable.my_skin_bubble_chatbox_two};
        return popBgIds[position];
    }

    private int loadBgData(int position) {
        int[] bgIds = new int[]{R.drawable.my_skin_bubble_bg_default,
                R.drawable.my_skin_bubble_bg_one,
                R.drawable.my_skin_bubble_bg_two};
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
