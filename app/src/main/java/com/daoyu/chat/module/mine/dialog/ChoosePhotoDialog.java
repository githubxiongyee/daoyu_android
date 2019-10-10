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
 * 选择图片对对话框
 */
public class ChoosePhotoDialog extends DialogFragment implements View.OnClickListener {
    @BindView(R.id.tv_camera)
    TextView tvCamera;
    @BindView(R.id.tv_album)
    TextView tvAlbum;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    private Unbinder mUnbind;
    private IChoosePhotoWays mListener;

    public static ChoosePhotoDialog getInstance() {
        ChoosePhotoDialog dialog = new ChoosePhotoDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IChoosePhotoWays) {
            mListener = (IChoosePhotoWays) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_choose_photo, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        tvCamera.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
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
            case R.id.tv_camera:
                if (mListener == null) {
                    return;
                }
                mListener.onChoosePhoto("camera");
                dismissAllowingStateLoss();
                break;
            case R.id.tv_album:
                if (mListener == null) {
                    return;
                }
                mListener.onChoosePhoto("album");
                dismissAllowingStateLoss();
                break;
            case R.id.tv_cancel:
                dismissAllowingStateLoss();
                break;
        }
    }

    public interface IChoosePhotoWays {
        void onChoosePhoto(String way);
    }
}
