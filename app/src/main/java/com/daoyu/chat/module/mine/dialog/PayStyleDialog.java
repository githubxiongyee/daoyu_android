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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 选择支付方式
 */
public class PayStyleDialog extends DialogFragment implements View.OnClickListener {
    @BindView(R.id.pay_style_back)
    ImageView ivCancel;

    @BindView(R.id.rl_remain)
    RelativeLayout rlRemain;
    @BindView(R.id.rl_bank)
    RelativeLayout rlBank;
    @BindView(R.id.rl_aliPay)
    RelativeLayout rlAliPay;
    @BindView(R.id.rl_weChat)
    RelativeLayout rlWeChat;

    @BindView(R.id.iv_icon_remain_get)
    ImageView ivRemainGet;
    @BindView(R.id.iv_icon_bank_get)
    ImageView ivBankGet;
    @BindView(R.id.iv_icon_aliPay_get)
    ImageView ivAliGet;
    @BindView(R.id.iv_icon_weChat_get)
    ImageView ivWeChatGet;

    private Unbinder mUnbind;
    private IPayWays mListener;

    public static PayStyleDialog getInstance() {
        PayStyleDialog dialog = new PayStyleDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IPayWays) {
            mListener = (IPayWays) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.pay_style_dialog, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        rlRemain.setOnClickListener(this);
        rlBank.setOnClickListener(this);
        rlAliPay.setOnClickListener(this);
        rlWeChat.setOnClickListener(this);

        ivCancel.setOnClickListener(this);
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
            case R.id.rl_remain:
                if (mListener == null) {
                    return;
                }
                mListener.onChooseRemain("Remain");
                //dismissAllowingStateLoss();
                ivRemainGet.setVisibility(View.VISIBLE);
                ivBankGet.setVisibility(View.GONE);
                ivAliGet.setVisibility(View.GONE);
                ivWeChatGet.setVisibility(View.GONE);
                break;
            case R.id.rl_bank:
                if (mListener == null) {
                    return;
                }
                mListener.onChooseBank("Bank");
                //dismissAllowingStateLoss();
                ivRemainGet.setVisibility(View.GONE);
                ivBankGet.setVisibility(View.VISIBLE);
                ivAliGet.setVisibility(View.GONE);
                ivWeChatGet.setVisibility(View.GONE);
                break;
            case R.id.rl_aliPay:
                if (mListener == null) {
                    return;
                }
                mListener.onChooseAli("Ali");
                //dismissAllowingStateLoss();
                ivRemainGet.setVisibility(View.GONE);
                ivBankGet.setVisibility(View.GONE);
                ivAliGet.setVisibility(View.VISIBLE);
                ivWeChatGet.setVisibility(View.GONE);
                break;
            case R.id.rl_weChat:
                if (mListener == null) {
                    return;
                }
                mListener.onChooseWeChat("WeChat");
                //dismissAllowingStateLoss();
                ivRemainGet.setVisibility(View.GONE);
                ivBankGet.setVisibility(View.GONE);
                ivAliGet.setVisibility(View.GONE);
                ivWeChatGet.setVisibility(View.VISIBLE);
                break;
            case R.id.pay_style_back:
                dismissAllowingStateLoss();
                break;
        }
    }

    public interface IPayWays {
        void onChooseRemain(String way);

        void onChooseBank(String way);

        void onChooseAli(String way);

        void onChooseWeChat(String way);
    }
}
