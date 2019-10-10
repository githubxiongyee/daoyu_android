package com.daoyu.chat.module.group.dialog;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.view.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 群聊未领取到红包对话框
 */
public class GroupReceiveRedPackageDialog extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    @BindView(R.id.tv_from_red_package)
    TextView tvFromRedPackage;
    @BindView(R.id.tv_greetings)
    TextView tvGreetings;
    @BindView(R.id.tv_receive_details)
    TextView tvReceiveDetails;

    private Unbinder mUnbind;
    private MessageDetailTable message;


    private OnNotReceivePackageListener lintener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNotReceivePackageListener) {
            lintener = (OnNotReceivePackageListener) context;
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public static GroupReceiveRedPackageDialog getInstance(MessageDetailTable messageDetail) {
        GroupReceiveRedPackageDialog dialog = new GroupReceiveRedPackageDialog();
        Bundle args = new Bundle();
        args.putParcelable(Constant.MESSAGE_RED_PACKAGE, messageDetail);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Bundle arguments = getArguments();
            message = arguments.getParcelable(Constant.MESSAGE_RED_PACKAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_receive_package_not_recevice, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        ivClose.setOnClickListener(this);
        tvReceiveDetails.setOnClickListener(this);
        if (message == null) return;
        tvFromRedPackage.setText(String.format("%s的红包", message.username));
        tvGreetings.setText(message.message);
        ImageUtils.setNormalImage(getContext(), message.avatar, R.drawable.my_user_default, R.drawable.my_user_default, ivHeader);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = DensityUtil.getScreenWidth() - getResources().getDimensionPixelOffset(R.dimen.dp_20);
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.dp_500);

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
            case R.id.iv_close:
                dismissAllowingStateLoss();
                break;
            case R.id.tv_receive_details:
                if (lintener==null)return;
                lintener.onNotReceivePackage(message);
                dismissAllowingStateLoss();
                break;

        }
    }



    public interface OnNotReceivePackageListener {
        void onNotReceivePackage(MessageDetailTable message);
    }
}
