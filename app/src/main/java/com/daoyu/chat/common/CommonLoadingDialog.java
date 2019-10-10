package com.daoyu.chat.common;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.daoyu.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CommonLoadingDialog extends DialogFragment implements DialogInterface.OnKeyListener {

    public static final String LOADING_TEXT = "loading_text";
    private static boolean mIsBack = true;

    @BindView(R.id.loading_text)
    TextView loadingTv;

    private Unbinder mUnBinder;
    private String mLoadingText;
    private OnKeyDownListener mListener;

    public static CommonLoadingDialog newInstance(String loadingText, boolean isBack) {
        mIsBack = isBack;
        CommonLoadingDialog dialog = new CommonLoadingDialog();
        Bundle bundle = new Bundle();
        bundle.putString(LOADING_TEXT, loadingText);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLoadingText = bundle.getString(LOADING_TEXT, getString(R.string.text_loading));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(this);
        View view = inflater.inflate(R.layout.dialog_common_loading, container, false);
        mUnBinder = ButterKnife.bind(this, view);
        initEvent();
        return view;
    }

    private void initEvent() {
        if (!TextUtils.isEmpty(mLoadingText)) {
            loadingTv.setText(mLoadingText);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnKeyDownListener) {
            mListener = (OnKeyDownListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (mListener != null) {
            mListener.onDialogKeyDown(dialog, keyCode, event);
        }
        return mIsBack;
    }
    boolean mDismissed;
    boolean mShownByMe;
    @Override
    public void show(FragmentManager manager, String tag) {
        //super.show(manager, tag);
        this.mDismissed = false;
        this.mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        // 这里把原来的commit()方法换成了commitAllowingStateLoss()
        ft.commitAllowingStateLoss();
    }

    public interface OnKeyDownListener {
        void onDialogKeyDown(DialogInterface dialog, int keyCode, KeyEvent event);
    }

}
