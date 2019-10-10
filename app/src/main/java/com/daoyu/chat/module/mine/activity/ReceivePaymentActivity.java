package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.dialog.PayStyleDialog;
import com.daoyu.chat.utils.ToolsUtil;

import butterknife.BindView;

/**
 * 收付款 初始时显示付款二维码
 */
public class ReceivePaymentActivity extends BaseTitleActivity {

    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    @BindView(R.id.tv_collection_record)
    TextView tvCollectionRecord;

    @BindView(R.id.tv_bottom_menu)
    TextView tvBottomMenu;

    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.iv_remain_right)
    ImageView ivRemainRight;

    @BindView(R.id.cl_pay_way)
    ConstraintLayout clPayWay;

    @BindView(R.id.iv_change_icon)
    ImageView ivChangeIcon;

    @BindView(R.id.tv_receive_payment_title)
    TextView tvReceivePaymentTitle;

    @BindView(R.id.rl_remain)
    RelativeLayout rlRemain;

    PayStyleDialog payStyleDialog;
    private boolean type = false;//默认是付款

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_receive_payment;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle($$(R.string.text_receive_payment));
        tvCollectionRecord.setOnClickListener(this);
        clPayWay.setOnClickListener(this);
        tvBottomMenu.setOnClickListener(this);
        rlRemain.setOnClickListener(this);

        initPaymentView();//默认

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_bottom_menu:
                type = !type;
                if (type) {
                    initReceiveView();
                } else {
                    initPaymentView();
                }
                break;
            case R.id.rl_remain: //付款方式
                showPayStyleDialog();
                break;
            case R.id.tv_collection_record://收款记录
                if (type){
                    //收款
                    Intent intent = new Intent(this,GetOrPayActivity.class);
                    intent.putExtra("type",0);
                    startActivity(intent);
                }else {
                    //付款
                    Intent intent = new Intent(this,GetOrPayActivity.class);
                    intent.putExtra("type",1);
                    startActivity(intent);
                }
                break;
        }
    }

    private void showPayStyleDialog() {
        if (payStyleDialog != null) {
            payStyleDialog.dismissAllowingStateLoss();
        }
        payStyleDialog = payStyleDialog.getInstance();
        if (payStyleDialog.isAdded()) {
            payStyleDialog.dismissAllowingStateLoss();
        } else {
            payStyleDialog.show(getSupportFragmentManager(), "payStyleDialog");
        }
    }
    private void initPaymentView() {
        tvReceivePaymentTitle.setText($$(R.string.text_pay_someone_else));
        rlRemain.setVisibility(View.GONE);
        //clPayWay.setVisibility(View.VISIBLE);
        tvCollectionRecord.setVisibility(View.VISIBLE);
        tvCollectionRecord.setText("收付款记录");
        tvBottomMenu.setText($$(R.string.text_i_want_to_receive));
        Drawable drawable = getResources().getDrawable(R.drawable.my_icon_eceiving_receivables);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvBottomMenu.setCompoundDrawables(drawable, null, null, null);
    }

    private void initReceiveView() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String payQRVal = userInfoData.payQRVal;
        Bitmap qrImage = ToolsUtil.createQRImage(payQRVal, getResources().getDimensionPixelSize(R.dimen.dp_200));
        ivQrCode.setImageBitmap(qrImage);

        tvReceivePaymentTitle.setText($$(R.string.text_qr_code_collection));
        //clPayWay.setVisibility(View.GONE);
        rlRemain.setVisibility(View.GONE);

        tvCollectionRecord.setVisibility(View.VISIBLE);
        tvCollectionRecord.setText("收款记录");

        tvBottomMenu.setText($$(R.string.text_i_want_to_pay));
        Drawable drawable = getResources().getDrawable(R.drawable.my_icon_eceiving_pay);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvBottomMenu.setCompoundDrawables(drawable, null, null, null);
    }


}
