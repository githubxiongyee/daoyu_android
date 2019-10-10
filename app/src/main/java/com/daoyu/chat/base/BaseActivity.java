package com.daoyu.chat.base;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.daoyu.chat.R;
import com.daoyu.chat.common.CommonLoadingDialog;
import com.daoyu.chat.common.CommonTouchListener;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.utils.RuntimeRationale;
import com.daoyu.chat.utils.ToastUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.bakumon.statuslayoutmanager.library.DefaultOnStatusChildClickListener;
import me.bakumon.statuslayoutmanager.library.StatusLayoutManager;

import static com.daoyu.chat.utils.DensityUtil.getStatusBarHeight;

/**
 * author: sunny
 * desc:BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public ToastUtil toast;
    private Unbinder mUnBinder;
    protected boolean isActivityFinish;
    public StatusLayoutManager mStatusLayoutManager;

    private View decorView;

    private List<CommonTouchListener> touchListeners = new ArrayList<>();

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     */
    public void registerCommonTouchListener(CommonTouchListener listener) {
        touchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     */
    public void unRegisterMyTouchListener(CommonTouchListener listener) {
        touchListeners.remove(listener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationBarColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            disableAutoFill();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
            }
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        windowSetting();
        setContentView(getLayoutResId());
        toast = new ToastUtil(this);
        BaseApplication.getInstance().addActivity(this);
        mUnBinder = ButterKnife.bind(this);


        setListener();
        initEvent();
        savedInstanceState(savedInstanceState);
    }

    public void windowSetting() {

    }

    public void savedInstanceState(Bundle savedInstanceState) {

    }

    /**
     * <P>shang</P>
     * <P>解决虚拟按键问题</P>
     *
     * @param window
     */
    public void solveNavigationBar(Window window) {

        //保持布局状态
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions |= 0x00001000;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }

    /**
     * <P>shang</P>
     * <P>判断是否有虚拟按键</P>
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    private void init() {
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                //  | View.SYSTEM_UI_FLAG_FULLSCREEN // 不隐藏状态栏，因为隐藏了比如时间电量等信息也会隐藏
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        //判断当前版本在4.0以上并且存在虚拟按键，否则不做操作
        if (Build.VERSION.SDK_INT < 19 || !checkDeviceHasNavigationBar()) {
            //一定要判断是否存在按键，否则在没有按键的手机调用会影响别的功能。如之前没有考虑到，导致图传全屏变成小屏显示。
            return;
        } else {
            //自定义工具，设置状态栏颜色是透明
            //ViewUtil.setWindowStatusBarColor(this, R.color.transparent);
            //setStatusBarColor(R.color.ip_color_primary_trans);
            // 获取属性
            decorView.setSystemUiVisibility(flag);
        }
    }

    private void setStatusBarColor(int color) {

        ViewGroup decorViewGroup =
                (ViewGroup) getWindow().getDecorView();

        View statusBarView = new View(getWindow().getContext());

        int statusBarHeight = getStatusBarHeight(getWindow().getContext());//获取状态栏高度（自定义函数 见下文）

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);

        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        decorViewGroup.addView(statusBarView);
    }


    /**
     * 判断是否存在虚拟按键
     *
     * @return
     */
    public boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    protected abstract int getLayoutResId();

    public void setListener() {

    }

    protected abstract void initEvent();


    @Override
    public void onClick(View view) {
    }

    protected void bindingRecyclerView(RecyclerView recyclerView) {
        mStatusLayoutManager = new StatusLayoutManager.Builder(recyclerView)
                /* .setLoadingLayout(R.layout.layout_status_loading)
                 .setEmptyLayout(R.layout.layout_status_empty)
                 .setErrorLayout(R.layout.layout_status_error)*/
                //.setErrorClickViewID(R.id.tv_refresh)
                .setOnStatusChildClickListener(new DefaultOnStatusChildClickListener() {
                    @Override
                    public void onEmptyChildClick(View view) {
                        BaseActivity.this.onEmptyChildClick(view);
                    }

                    @Override
                    public void onErrorChildClick(View view) {
                        BaseActivity.this.onErrorChildClick(view);
                    }

                    @Override
                    public void onCustomerChildClick(View view) {
                        BaseActivity.this.onCustomerChildClick(view);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (CommonTouchListener listener : touchListeners) {
            listener.onTouchEvent(ev);
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setNavigationBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.BLACK);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void disableAutoFill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
    }

    public void handleMessage(Message message) {

    }

    protected void requestPermission(String... permissions) {
        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(granted)
                .onDenied(denied)
                .start();
    }

    protected void requestPermission(String[]... permissions) {
        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(granted)
                .onDenied(denied)
                .start();
    }

    private Action<List<String>> granted = permissions -> {
        List<String> permissionText = Permission.transformText(BaseActivity.this, permissions);
        onPermissionGrant(permissionText);
    };
    private Action<List<String>> denied = permissions -> {
        if (AndPermission.hasAlwaysDeniedPermission(BaseActivity.this, permissions)) {
            showSettingDialog(BaseActivity.this, permissions);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    public void onPermissionGrant(List<String> permissions) {

    }

    public void showLoading(String loadingText, boolean canBack) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constant.TAG_LOADING_DIALOG);
        if (fragment == null) {
            fragment = CommonLoadingDialog.newInstance(loadingText, canBack);
        }
        CommonLoadingDialog dialog = (CommonLoadingDialog) fragment;
        if (!dialog.isAdded()) {
            dialog.show(getSupportFragmentManager(), Constant.TAG_LOADING_DIALOG);
        } else {
            dialog.dismissAllowingStateLoss();
        }
    }

    public void hideLoading() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constant.TAG_LOADING_DIALOG);
        if (fragment == null || !(fragment instanceof CommonLoadingDialog)) return;
        CommonLoadingDialog dialog = (CommonLoadingDialog) fragment;
        dialog.dismissAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        CHttpUtils.getInstance().cancelRequest(this);
        isActivityFinish = true;
        BaseApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

    /**
     * 获取String资源文件
     *
     * @param ids String id
     * @return
     */
    public String $$(int ids) {
        return getResources().getString(ids);
    }


    public void onAlbum() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部..ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(false)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .compress(true)
                .enableCrop(false)
                .cropCompressQuality(60)
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .isDragFrame(true)// 是否可拖动裁剪框(固定)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public void onCamera() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .compress(true)
                .enableCrop(false)
                .cropCompressQuality(60)
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
