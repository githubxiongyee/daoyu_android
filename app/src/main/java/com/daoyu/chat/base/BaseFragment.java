package com.daoyu.chat.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daoyu.chat.R;
import com.daoyu.chat.common.CommonLoadingDialog;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.group.activity.StartGroupChatActivity;
import com.daoyu.chat.module.home.activity.ScanQRCodeActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.dialog.TopPopWindow;
import com.daoyu.chat.utils.RuntimeRationale;
import com.daoyu.chat.utils.ToastUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.bakumon.statuslayoutmanager.library.DefaultOnStatusChildClickListener;
import me.bakumon.statuslayoutmanager.library.StatusLayoutManager;


/**
 * Fragment基类
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    private Unbinder mUnBinder;
    protected boolean isFragmentFinish;
    protected boolean mIsVisible = false;
    protected boolean mIsInitiated = false;
    protected boolean mIsPrepared = false;
    public ToastUtil toast;
    public StatusLayoutManager mStatusLayoutManager;

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;
    private TopPopWindow topPopWindow;
    private String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE};


    protected abstract int getLayoutResId();

    protected abstract void initEvent();

    protected void loadInitData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(getLayoutResId(), container, false);
        mUnBinder = ButterKnife.bind(this, rootLayout);
        toast = new ToastUtil(getContext());
        initEvent();
        return rootLayout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this.getContext()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mIsPrepared = true;
        loadData(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.mIsVisible = isVisibleToUser;
        loadData(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(true);
    }

    private void loadData(boolean forceUpdate) {
        if (mIsVisible && mIsPrepared && (!mIsInitiated || forceUpdate)) {
            loadInitData();
            mIsInitiated = true;
        }
    }

    public void handleMessage(Message message) {
    }

    @Override
    public void onDestroyView() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        CHttpUtils.getInstance().cancelRequest(this);
        super.onDestroyView();
        isFragmentFinish = true;
    }

    public void onMultiClick(View view) {

    }

    @Override
    public void onClick(View view) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            onMultiClick(view);
        }
    }

    protected void bindingRecyclerView(RecyclerView recyclerView) {
        mStatusLayoutManager = new StatusLayoutManager.Builder(recyclerView)
                .setOnStatusChildClickListener(new DefaultOnStatusChildClickListener() {
                    @Override
                    public void onEmptyChildClick(View view) {
                        BaseFragment.this.onEmptyChildClick(view);
                    }

                    @Override
                    public void onErrorChildClick(View view) {
                        BaseFragment.this.onErrorChildClick(view);
                    }

                    @Override
                    public void onCustomerChildClick(View view) {
                        BaseFragment.this.onCustomerChildClick(view);
                    }
                })
                .build();
    }

    protected void onEmptyChildClick(View view) {

    }

    protected void onErrorChildClick(View view) {

    }

    protected void onCustomerChildClick(View view) {

    }

    public void showLoading(String loadingText, boolean canBack) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(Constant.TAG_LOADING_DIALOG);
        if (fragment == null) {
            fragment = CommonLoadingDialog.newInstance(loadingText, canBack);
        }
        CommonLoadingDialog dialog = (CommonLoadingDialog) fragment;
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), Constant.TAG_LOADING_DIALOG);
        } else {
            dialog.dismissAllowingStateLoss();
        }
    }

    public void hideLoading() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(Constant.TAG_LOADING_DIALOG);
        if (fragment == null || !(fragment instanceof CommonLoadingDialog)) return;
        CommonLoadingDialog dialog = (CommonLoadingDialog) fragment;
        dialog.dismissAllowingStateLoss();
    }

    /**
     * 显示右上角popup菜单
     */
    public void showTopRightPopMenu(View view) {
        if (topPopWindow != null && topPopWindow.isShowing()) {
            topPopWindow.dismiss();
        }
        topPopWindow = new TopPopWindow(getActivity(), v -> {
            switch (v.getId()) {
                case R.id.tv_02:
                    Intent intent = new Intent(getActivity(), StartGroupChatActivity.class);
                    intent.putExtra(Constant.TITLE, "选择联系人");
                    startActivity(intent);
                    break;
                case R.id.tv_03:
                    AndPermission.with(this)
                            .runtime()
                            .permission(Permission.Group.CAMERA)
                            .rationale(new RuntimeRationale())
                            .onGranted(granted)
                            .onDenied(denied)
                            .start();
                    break;
                case R.id.tv_share:
                    requestPermissions(mPermissionList, 123);
                    break;
            }
        });
        //在某一个页面的下方
        topPopWindow.showAsDropDownView(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            boolean b = AndPermission.hasPermissions(getActivity(), mPermissionList);
            if (b) {
                UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                showShareDialog(String.format(getResources().getString(R.string.str_share_txt),userInfoData.invateCode));
            }
        }
    }

    private Action<List<String>> granted = permissions -> {
        if (AndPermission.hasPermissions(getContext(), Permission.Group.CAMERA)) {
            startActivity(new Intent(getActivity(), ScanQRCodeActivity.class));
        }
    };
    private Action<List<String>> denied = permissions -> {
        if (AndPermission.hasAlwaysDeniedPermission(this, permissions)) {
            showSettingDialog(getContext(), permissions);
        }
    };

    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = null;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < permissionNames.size(); i++) {
            String permission = permissionNames.get(i);
            if (i == permissionNames.size() - 1) {
                buffer.append(permission);
            } else {
                buffer.append(permission + ",");
            }
        }
        message = context.getString(R.string.message_permission_always_failed, buffer.toString().trim());
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle(R.string.title_dialog)
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(R.string.setting, (dialog, which) -> setPermission())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                })
                .show();
    }

    private void setPermission() {
        AndPermission.with(this).runtime().setting().start(Constant.REQUEST_CODE_SETTING);
    }

    /**
     * 分享
     *
     * @param description 描述
     */
    public void showShareDialog(String description) {
        UMImage umImage = new UMImage(getContext(),R.drawable.login_logo);
        new ShareAction(getActivity()).withText(description).setDisplayList(SHARE_MEDIA.SINA/*, SHARE_MEDIA.QQ*/, SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        toast.toastShow(share_media.getName());
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        toast.toastShow("分享失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                }).open();
    }

}
