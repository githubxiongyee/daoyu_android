package com.daoyu.chat.module.login.view;

import android.content.Context;
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
 * 开启定位dialog
 */
public class LocationServiceDialog extends DialogFragment implements View.OnClickListener {
    @BindView(R.id.tv_font)
    TextView tvFont;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    private Unbinder mUnbind;
    private ICheckSequenceListener mListener;

    public void setmListener(ICheckSequenceListener mListener) {
        this.mListener = mListener;
    }

    public static LocationServiceDialog getInstance() {
        LocationServiceDialog dialog = new LocationServiceDialog();
        return dialog;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ICheckSequenceListener) {
            mListener = (ICheckSequenceListener) context;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_location_service_bg));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_location_service, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = DensityUtil.getScreenWidth() - getResources().getDimensionPixelOffset(R.dimen.dp_20);
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
            case R.id.tv_confirm://确认按钮
                if (mListener == null) {
                    return;
                }
                mListener.onConfirm();
                dismissAllowingStateLoss();
                break;
            case R.id.tv_cancel://取消按钮
                if (mListener == null) {
                    return;
                }
                mListener.onCancel();
                dismissAllowingStateLoss();
                break;
        }
    }


    public interface ICheckSequenceListener {
        void onConfirm();

        void onCancel();
    }

}
