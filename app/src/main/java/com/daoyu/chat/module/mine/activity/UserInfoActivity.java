package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.dialog.ChoosePhotoDialog;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.NoLeakHandler;
import com.daoyu.chat.utils.QiNiuUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.CircleImageView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.model.Response;

import org.litepal.crud.callback.UpdateOrDeleteCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class UserInfoActivity extends BaseTitleActivity implements ChoosePhotoDialog.IChoosePhotoWays {

    @BindView(R.id.iv_header)
    CircleImageView ivHeader;

    @BindView(R.id.cl_header)
    ConstraintLayout clHeader;

    @BindView(R.id.tv_nick)
    TextView tvNick;

    @BindView(R.id.cl_nick)
    ConstraintLayout clNick;

    @BindView(R.id.tv_id)
    TextView tvId;

    @BindView(R.id.cl_qr_code)
    ConstraintLayout clQrCode;

    @BindView(R.id.tv_sex)
    TextView tvSex;

    @BindView(R.id.cl_sex)
    ConstraintLayout clSex;

    @BindView(R.id.cl_area)
    ConstraintLayout clArea;

    @BindView(R.id.tv_signature)
    TextView tvSignature;

    @BindView(R.id.tv_area_btn)
    TextView tvArea;

    @BindView(R.id.tv_invite)
    EditText tvInvite;


    @BindView(R.id.cl_signature)
    ConstraintLayout clSignature;
    @BindView(R.id.cl_id)
    ConstraintLayout clId;
    private ChoosePhotoDialog choosePhotoDialog;
    private UserBean.UserData userInfoData;
    private NoLeakHandler handler;

    @Override
    public void handleMessage(Message message) {
        if (message == null) return;
        int what = message.what;
        if (what == 1) {
            String url = (String) message.obj;
            ImageUtils.setNormalImage(this, url, R.drawable.my_user_default, R.drawable.my_user_default, ivHeader);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("个人信息");
        handler = new NoLeakHandler(this);
        clHeader.setOnClickListener(this);
        clNick.setOnClickListener(this);
        clQrCode.setOnClickListener(this);
        clSex.setOnClickListener(this);
        clArea.setOnClickListener(this);
        clSignature.setOnClickListener(this);
        clId.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        ImageUtils.setNormalImage(this, userInfoData.headImg, R.drawable.my_user_default, R.drawable.my_user_default, ivHeader);
        tvNick.setText(userInfoData.nickName);
        tvId.setText(String.valueOf(userInfoData.userPhone));
        tvSex.setText("F".equals(userInfoData.sex) ? "女" : "男");
        tvSignature.setText(userInfoData.remarks);
        String area = userInfoData.addressD;
        String invite = userInfoData.invateCode;
        if (TextUtils.isEmpty(area)) {
            tvArea.setText("请选择");
        } else {
            tvArea.setText(userInfoData.addressD);
        }
        if (TextUtils.isEmpty(invite) || invite == null){

        }else {
            tvInvite.setText(invite);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.cl_header:
                showChoosePhoto();
                break;
            case R.id.cl_nick:
                startActivity(new Intent(this, NickSettingActivity.class));
                break;
            case R.id.cl_qr_code:
                startActivity(new Intent(this, QRCardActivity.class));
                break;
            case R.id.cl_sex:
                startActivity(new Intent(this, SexSettingActivity.class));
                break;
            case R.id.cl_signature:
                startActivity(new Intent(this, SignatureSettingActivity.class));
                break;
            case R.id.cl_id:
                toast.toastShow(String.valueOf(userInfoData.uid));
                Log.e("token", userInfoData.token);
                break;
            case R.id.cl_area:
                //地区
                startActivity(new Intent(this, AreaSettingActivity.class));
                break;
        }
    }

    /**
     * 显示选择照片对话框
     */
    private void showChoosePhoto() {
        if (choosePhotoDialog != null) {
            choosePhotoDialog.dismissAllowingStateLoss();
        }
        choosePhotoDialog = ChoosePhotoDialog.getInstance();
        if (!choosePhotoDialog.isAdded()) {
            choosePhotoDialog.show(getSupportFragmentManager(), "choosePhotoDialog");
        } else {
            choosePhotoDialog.dismissAllowingStateLoss();
        }

    }

    /**
     * 选择图片打开方式
     *
     * @param way camera 相机 album 相册
     */
    @Override
    public void onChoosePhoto(String way) {
        if (TextUtils.isEmpty(way)) return;
        switch (way) {
            case "camera":
                onCameraUser();
                break;
            case "album":
                onAlbumUser();
                break;
        }
    }


    public void onAlbumUser() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())//全部..ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(false)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.3f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .compress(true)
                .enableCrop(true)
                .freeStyleCropEnabled(true)
                .withAspectRatio(1, 1)
                .minimumCompressSize(50)// 小于100kb的图片不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public void onCameraUser() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .compress(true)
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .enableCrop(true)
                .withAspectRatio(1, 1)
                .minimumCompressSize(50)// 小于100kb的图片不压缩
                .compress(true)
                .freeStyleCropEnabled(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = selectList.get(0);
                    String compressPath = localMedia.getCompressPath();
                    if (TextUtils.isEmpty(compressPath)) {
                        return;
                    }
                    QiNiuUtil.request(new File(compressPath), String.valueOf(System.currentTimeMillis()), (url, status) -> {
                        if (status) {
                            requestSettingUserHeader(url);
                        }
                    }, ".jpg");
                    break;
            }
        }
    }

    /**
     * 请求设置头像
     *
     * @param url
     */
    private void requestSettingUserHeader(String url) {
        showLoading("头像修改中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("headImg", url);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("uid", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_UPDATE_USER_INFO, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                    userInfoData.headImg = url;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    updateHeaderImage(url);
                    Message obtain = Message.obtain();
                    obtain.obj = url;
                    obtain.what = 1;
                    handler.sendMessage(obtain);

                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<BaseBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

    private void updateHeaderImage(String headerImage) {
        MessageDetailTable table = new MessageDetailTable();
        table.avatar = headerImage;
        table.updateAllAsync("user_id = ?", String.valueOf(userInfoData.uid)).listen(new UpdateOrDeleteCallback() {
            @Override
            public void onFinish(int rowsAffected) {
                Log.d("TAG", "受影响的记录数:" + rowsAffected);
            }
        });
    }

}
