package com.daoyu.chat.module.system.dialog;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
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
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.system.bean.SignB;
import com.daoyu.chat.module.system.bean.SignInfoB;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 签到
 */
public class SignInDialog extends DialogFragment implements View.OnClickListener {
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.tv_sign_in_btn)
    TextView tvSignInBtn;

    @BindView(R.id.cl_day1)
    ConstraintLayout clDay1;
    @BindView(R.id.cl_day2)
    ConstraintLayout clDay2;
    @BindView(R.id.cl_day3)
    ConstraintLayout clDay3;
    @BindView(R.id.cl_day4)
    ConstraintLayout clDay4;
    @BindView(R.id.cl_day5)
    ConstraintLayout clDay5;
    @BindView(R.id.cl_day6)
    ConstraintLayout clDay6;
    @BindView(R.id.cl_day7)
    ConstraintLayout clDay7;

    @BindView(R.id.cl_sign_in)
    ConstraintLayout clSignIn;
    @BindView(R.id.cl_sign_finish)
    ConstraintLayout clSignFinish;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_money_num)
    TextView tvMoneyNum;

    private Unbinder mUnbind;
    private SignInDialog.IChooseAddWays mListener;

    public static SignInDialog getInstance() {
        SignInDialog dialog = new SignInDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignInDialog.IChooseAddWays) {
            mListener = (SignInDialog.IChooseAddWays) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_sign_in, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        requestSignInfo();
        ivClose.setOnClickListener(this);
        tvSignInBtn.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    /**
     * 得到上一次签到信息
     */
    private void requestSignInfo() {
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_LAST_SIGN, params, this, new JsonCallback<SignInfoB>(SignInfoB.class) {
            @Override
            public void onSuccess(Response<SignInfoB> response) {
                //if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                SignInfoB body = response.body();
                if (body.code == 1) {
                    if (body.data == null) return;
                    SignInfoB.SignInfoData data = body.data;
                    String giftCount = data.giftCount;
                    show7package(giftCount, data.lastCheckInDate);
                }
            }
        });
    }

    ConstraintLayout[] clDays = new ConstraintLayout[]{clDay1, clDay2, clDay3, clDay4, clDay5, clDay6, clDay7};

    private boolean isLastAndToDay(String lastCheckInDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);//往上推一天  30推三十天  365推一年
        String yesterday = sdf.format(calendar.getTime());

        SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String today = todayFormat.format(new Date());
        if (lastCheckInDate.equals(yesterday) || lastCheckInDate.equals(today)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isLastDay(String lastCheckInDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);//往上推一天  30推三十天  365推一年
        String yesterday = sdf.format(calendar.getTime());
        if (lastCheckInDate.equals(yesterday)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isToday(String time) {
        SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String today = todayFormat.format(new Date());
        if (time.equals(today)) {
            return true;
        } else {
            return false;
        }
    }

    private void show7package(String giftCount, String lastCheckInDate) {
        switch (giftCount) {
            case "1":
                if (lastCheckInDate == null || !isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                } else if (lastCheckInDate != null && !isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                } else if (lastCheckInDate != null && isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                }
                break;
            case "2":
                if (lastCheckInDate != null && !isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                } else if (lastCheckInDate != null && isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                }
                break;
            case "3":
                if (lastCheckInDate != null && !isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                } else if (lastCheckInDate != null && isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                }
                break;
            case "4":
                if (lastCheckInDate != null && !isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                } else if (lastCheckInDate != null && isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                }
                break;
            case "5":
                if (lastCheckInDate != null && !isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                } else if (lastCheckInDate != null && isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                }
                break;
            case "6":
                if (lastCheckInDate != null && !isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                } else if (lastCheckInDate != null && isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                }
                break;
            case "7":
                if (lastCheckInDate != null && !isLastAndToDay(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                } else if (lastCheckInDate != null && isToday(lastCheckInDate)) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_received_bg));
                } else if ((lastCheckInDate != null && isLastDay(lastCheckInDate))) {
                    clDay1.setBackground(getResources().getDrawable(R.drawable.signin_receiv_bg));
                    clDay2.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay3.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay4.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay5.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay6.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                    clDay7.setBackground(getResources().getDrawable(R.drawable.signin_unclaimed_bg));
                }
                break;
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
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
            /*case R.id.tv_add_all:
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
                break;*/
            case R.id.tv_sign_in_btn:
                /*if (mListener == null) {
                    return;
                }*/
                requestSignIn();
                //mListener.onChooseAdd("add_notAllowed");
                //dismissAllowingStateLoss();
                break;
            case R.id.iv_close:
                dismissAllowingStateLoss();
                break;
            case R.id.tv_confirm:
                dismissAllowingStateLoss();
                break;
        }
    }

    /**
     * 签到
     */
    private void requestSignIn() {
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SIGN_IN, params, this, new JsonCallback<SignB>(SignB.class) {
            @Override
            public void onSuccess(Response<SignB> response) {
                //if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                SignB body = response.body();
                if (body.code == 1) {
                    if (body.data == null) return;
                    SignB.SignData data = body.data;
                    /*SignB.SignData data = body.data;
                    String giftCount = data.giftCount;
                    show7package(giftCount, data.lastCheckInDate);*/
                    clSignIn.setVisibility(View.GONE);
                    clSignFinish.setVisibility(View.VISIBLE);
                    tvMoneyNum.setText(data.giftCount);
                }
            }
        });
    }

    public interface IChooseAddWays {
        void onChooseAdd(String way);
    }
}
