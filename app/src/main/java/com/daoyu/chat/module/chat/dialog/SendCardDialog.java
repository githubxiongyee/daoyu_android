package com.daoyu.chat.module.chat.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 发送给
 */
public class SendCardDialog extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_send)
    TextView tvSend;

    private Unbinder mUnbind;

    private OnSendCardToFriendListener lintener;
    private String userId;
    private String userHeader;
    private String name;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSendCardToFriendListener) {
            lintener = (OnSendCardToFriendListener) context;
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public static SendCardDialog getInstance(String userId, String header, String name) {
        SendCardDialog dialog = new SendCardDialog();
        Bundle args = new Bundle();
        args.putString(Constant.CONTACT_ID, userId);
        args.putString(Constant.CONTACT_HEADER, header);
        args.putString(Constant.CONTACT_NAME, name);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Bundle arguments = getArguments();
            userId = arguments.getString(Constant.CONTACT_ID);
            userHeader = arguments.getString(Constant.CONTACT_HEADER);
            name = arguments.getString(Constant.CONTACT_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_send_card, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        ImageUtils.setNormalImage(getContext(), userHeader + "?imageMogr2/thumbnail/200/quality/40", ivHeader);
        tvName.setText(name);
        tvCancel.setOnClickListener(this);
        tvSend.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = DensityUtil.getScreenWidth() - getResources().getDimensionPixelOffset(R.dimen.dp_40);
    }

    @Override
    public void onDetach() {
        if (mUnbind != null) {
            mUnbind.unbind();
        }
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismissAllowingStateLoss();
                break;
            case R.id.tv_send:
                if (lintener == null) return;
                lintener.onSendCard(userId, name, userHeader);
                dismissAllowingStateLoss();
                break;
        }
    }


    public interface OnSendCardToFriendListener {
        void onSendCard(String userId, String userName, String header);
    }
}
