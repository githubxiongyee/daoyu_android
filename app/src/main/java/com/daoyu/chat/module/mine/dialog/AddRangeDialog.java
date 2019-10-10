package com.daoyu.chat.module.mine.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 好友添加弹窗
 */
public class AddRangeDialog extends DialogFragment implements View.OnClickListener {
    @BindView(R.id.tv_add_all)
    TextView tvAddAll;
    @BindView(R.id.tv_add_check)
    TextView tvAddCheck;
    @BindView(R.id.tv_add_notAllowed)
    TextView tvAddNotAllowed;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    private Unbinder mUnbind;
    private IChooseAddWays mListener;

    public static AddRangeDialog getInstance() {
        AddRangeDialog dialog = new AddRangeDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IChooseAddWays) {
            mListener = (IChooseAddWays) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_add_range, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        tvAddAll.setOnClickListener(this);
        tvAddCheck.setOnClickListener(this);
        tvAddNotAllowed.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = DensityUtil.getScreenWidth();
    }

    @Override
    public void onDetach() {
        if (mUnbind != null) {
            mUnbind.unbind();
        }
        if (mListener != null) {
            mListener = null;
        }
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_all:
                if (mListener == null) {
                    return;
                }
                mListener.onChooseAdd("add_all");
                dismissAllowingStateLoss();
                break;
            case R.id.tv_add_check:
                if (mListener == null) {
                    return;
                }
                mListener.onChooseAdd("add_check");
                dismissAllowingStateLoss();
                break;
            case R.id.tv_add_notAllowed:
                if (mListener == null) {
                    return;
                }
                mListener.onChooseAdd("add_notAllowed");
                dismissAllowingStateLoss();
                break;
            case R.id.tv_cancel:
                dismissAllowingStateLoss();
                break;
        }
    }

    public interface IChooseAddWays {
        void onChooseAdd(String way);
    }
}
