package com.daoyu.chat.module.home.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.LinearLayout;

import com.daoyu.chat.R;
import com.daoyu.chat.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 发送名片-搜索联系人弹窗
 */
public class SendToSearchDialog extends DialogFragment implements View.OnClickListener {
    @BindView(R.id.fillStatusBarView)
    View mStatusBar;

    private Unbinder mUnbind;
    private IChooseAddWays mListener;

    public static SendToSearchDialog getInstance() {
        SendToSearchDialog dialog = new SendToSearchDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IChooseAddWays) {
            mListener = (IChooseAddWays) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_send_to_search, container, false);
        mUnbind = ButterKnife.bind(this, inflate);

        //得到当前界面的装饰视图===========
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getDialog().getWindow().getDecorView();
            //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏颜色为透明
            getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        /**
         * 设置view高度为statusbar的高度，并填充statusbar
         */
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mStatusBar.getLayoutParams();
        lp.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        lp.height = getStatusBar(inflate);
        mStatusBar.setLayoutParams(lp);
        //==============================

        initEvent();
        return inflate;
    }
    /**
     * 获取状态栏高度
     * @return
     */
    public static int getStatusBar(View rootView){
        /**
         * 获取状态栏高度
         * */
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = rootView.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = rootView.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    private void initEvent() {
        /*tvAddAll.setOnClickListener(this);
        tvAddCheck.setOnClickListener(this);
        tvAddNotAllowed.setOnClickListener(this);
        tvCancel.setOnClickListener(this);*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP;
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
        /*switch (view.getId()) {
            case R.id.tv_add_all:
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
                break;
            case R.id.tv_add_notAllowed:
                if (mListener == null) {
                    return;
                }
                mListener.onChooseAdd("add_notAllowed");
                dismissAllowingStateLoss();
                break;
            case R.id.tv_cancel:
                dismissAllowingStateLoss();
                break;
        }*/
    }

    public interface IChooseAddWays {
        void onChooseAdd(String way);
    }
}
