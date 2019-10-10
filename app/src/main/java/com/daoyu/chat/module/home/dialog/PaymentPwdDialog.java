package com.daoyu.chat.module.home.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.view.PayPsdInputView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 支付密码选择框
 */
public class PaymentPwdDialog extends DialogFragment implements View.OnClickListener, PayPsdInputView.onPasswordListener {
    @BindView(R.id.tv_pay_money)
    TextView tvPayMoney;
    @BindView(R.id.tv_payment_way)
    TextView tvPaymentWay;
    @BindView(R.id.tv_payment_info)
    TextView tvPaymentInfo;
    @BindView(R.id.psd_view)
    PayPsdInputView psdView;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    private Unbinder mUnbind;
    private String paymentAmount;
    private final int BOND = 1;
    @SuppressLint("HandlerLeak")
   /* private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BOND:
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
            }
        }
    };*/
    private OnPaymentResult result;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPaymentResult) {
            result = (OnPaymentResult) context;
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public static PaymentPwdDialog getInstance(String paymentAmount) {
        PaymentPwdDialog dialog = new PaymentPwdDialog();
        Bundle args = new Bundle();
        args.putString(Constant.PAYMENT_AMOUNT, paymentAmount);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Bundle arguments = getArguments();
            paymentAmount = arguments.getString(Constant.PAYMENT_AMOUNT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_choose_photo_bg));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_payment, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        tvPayMoney.setText(paymentAmount);
        ivClose.setOnClickListener(this);
        psdView.setComparePassword(this);
        psdView.setFocusable(true);
        psdView.setFocusableInTouchMode(true);
        tvPaymentInfo.setText(String.format("金额(剩余:%.2f元)", userInfoData.creditTotal / 100));
        // handler.sendEmptyMessageDelayed(BOND, 100);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = DensityUtil.getScreenWidth() - getResources().getDimensionPixelOffset(R.dimen.dp_60);

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
        }
    }

    @Override
    public void onDifference(String oldPsd, String newPsd) {

    }

    @Override
    public void onEqual(String psd) {

    }

    @Override
    public void inputFinished(String inputPsd) {
        dismissAllowingStateLoss();
        if (result == null) return;
        result.onPayResult(inputPsd);
    }

    public interface OnPaymentResult {
        void onPayResult(String result);
    }
}
