package com.daoyu.chat.module.envelope.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daoyu.chat.R;
import com.daoyu.chat.module.envelope.view.RandomLayout;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 验证码对话框
 */
public class VerificationCodeDialog extends DialogFragment implements View.OnClickListener,RandomLayout.OnItemViewClickListener {
    public static final String PRODUCT_NAME = "product_name";
    @BindView(R.id.tv_font)
    TextView tvFont;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.ll_text_area)
    RandomLayout llTextArea;

    private Unbinder mUnbind;
    private ICheckSequenceListener mListener;
    private String productName;
    private ArrayList<String> chinaList = new ArrayList<>();
    private int currentClick = 0;
    private int length;
    private ToastUtil toast;
    private List<TextView> textViews = new ArrayList<>();

    public static VerificationCodeDialog getInstance(String productName) {
        VerificationCodeDialog dialog = new VerificationCodeDialog();
        Bundle arguments = new Bundle();
        arguments.putString(PRODUCT_NAME, productName);
        dialog.setArguments(arguments);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        productName = arguments.getString(PRODUCT_NAME);



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
        getDialog().getWindow().setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_verification_code_bg));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_verification_code, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        toast = new ToastUtil(getContext());
        String chinaText = filterChinese(productName);

        doCheck(chinaText);
        llTextArea.setListener(this);

        tvConfirm.setBackgroundColor(getResources().getColor(R.color.color_F0F0F0));
        tvConfirm.setOnClickListener(this);
        tvConfirm.setEnabled(false);
    }

    private void doCheck(String text) {
        length = text.length();
        if (length > 4) {
            text = text.substring(0, 4);
            length = 4;
        }
        tvFont.setText(text);
        for (int i = 0; i < length; i++) {
            String subStr = text.substring(i, i + 1);
            chinaList.add(subStr);
        }
        llTextArea.setTextData(chinaList,getContext());
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
        if (mListener != null) {
            mListener = null;
        }
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_confirm:
                if (clickTag == 0){
                    if (mListener == null) {
                        return;
                    }
                    toast.toastShow("领取成功");
                    mListener.onConfirm();
                    dismissAllowingStateLoss();
                }else if (clickTag == 1){
                    if (mListener == null) {
                        return;
                    }
                    toast.toastShow("验证失败,请重试");
                    mListener.onConfirm();
                    dismissAllowingStateLoss();
                }

                break;
        }
    }

    int clickTag = 0;
    @Override
    public void onItemViewClickListenerView(int tag, TextView textView) {
        if (tag == currentClick) {
            currentClick++;
            textView.setTextColor(getContext().getResources().getColor(R.color.color_E94E46));
        } else {
            new ToastUtil(getContext()).toastShow("验证失败,请重试");
        }
        if (currentClick == getNameList(chinaList).length()){
            if (textView.getText().toString().equals(chinaList.get(chinaList.size()-1))){
                //new ToastUtil(getContext()).toastShow("===验证成功==");
                clickTag = 0;
            }else {
                new ToastUtil(getContext()).toastShow("验证失败,请重试");
                clickTag = 1;
            }
            tvConfirm.setEnabled(true);
        }
    }
    private String getNameList(List<String> list){
        String nameStr = "";
        if (list.size() != 0){
            for (int i = 0;i < list.size();i ++){
                nameStr = nameStr + list.get(i);
            }
        }
        return nameStr;
    }
    @Override
    public void onStop() {
        super.onStop();
        //llTextArea.clearLayout();
    }

    public interface ICheckSequenceListener {
        void onConfirm();
    }

    public static String filterChinese(String chin) {
        chin = chin.replaceAll("[^(\\u4e00-\\u9fa5)]", "");
        return chin;
    }

}
