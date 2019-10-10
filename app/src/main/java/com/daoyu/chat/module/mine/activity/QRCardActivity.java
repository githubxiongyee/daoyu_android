package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.activity.ScanQRCodeActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.utils.SwitchCodeUtils;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.daoyu.chat.utils.ToolsUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * 二维码名片
 */
public class QRCardActivity extends BaseTitleActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_id)
    TextView tvId;

    @BindView(R.id.iv_header)
    ImageView ivHeader;

    @BindView(R.id.tv_change_style)
    TextView tvChangeStyle;

    @BindView(R.id.tv_default_style)
    TextView tvDefaultStyle;

    @BindView(R.id.tv_save)
    TextView tvSave;

    @BindView(R.id.tv_scan)
    TextView tvScan;

    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    private boolean permissType; //true : 文件读写权限  false : 相机权限
    private Bitmap qrImage;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_qrcard;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("二维码名片");
        setRightMenu(new int[]{R.string.text_reset_qr}, false);
        tvSave.setOnClickListener(this);
        tvScan.setOnClickListener(this);
        tvChangeStyle.setOnClickListener(this);
        tvDefaultStyle.setOnClickListener(this);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String qrval = userInfoData.qrval;
        if (TextUtils.isEmpty(qrval)) return;
        qrImage = ToolsUtil.createQRImage(qrval, DensityUtil.dip2px(360));
        //ImageUtils.setNormalImage(this, qrImage, ivQrCode);

        ImageUtils.setRoundImageView(this, userInfoData.headImg, R.drawable.my_user_default, ivHeader);
        tvName.setText(userInfoData.nickName);
        tvId.setText("ID号：" + userInfoData.userPhone);

        //二维码中间的图片
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_app);
        //生成需要的二维码，将二维码颜色设置成透明，背景设置成白色
        originalQrBitmap = QRCodeEncoder.syncEncodeQRCode(qrval, DensityUtil.dip2px(360), ContextCompat.getColor(this, R.color.transparent), ContextCompat.getColor(this, R.color.white), logoBitmap);
        startShowCode();
    }

    private void startShowCode() {
        int codeStyle = SharedPreferenceUtil.getInstance().getInt(Constant.CODE_STYLE);
        if (codeStyle == 0) {
            createDef();
        } else if (codeStyle == 1) {
            creatQRcode1();
        } else if (codeStyle == 2) {
            creatQRcode2();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_scan:
                permissType = false;
                requestPermission(Permission.Group.CAMERA);
                break;
            case R.id.tv_save:
                permissType = true;
                requestPermission(Permission.Group.STORAGE);
                break;
            case R.id.tv_change_style:
                if (indexTag == 0) {
                    creatQRcode1();
                } else if (indexTag == 1) {
                    creatQRcode2();
                } else if (indexTag == 2) {
                    createDef();
                }
                break;
            case R.id.tv_default_style:
                createDef();
                break;
        }
    }

    private Random mRandom = new Random();
    private Bitmap lastBitmap; //最终的花式二维码
    private Bitmap originalQrBitmap;
    //二维码样式背景图
    private int[] qrBgs = {R.drawable.my_code_picture_t, R.drawable.my_code_picture_bg_t};
    //二维码背景图
    private static int[] qrBgIds = {R.drawable.my_code_bg_white, R.drawable.my_code_bg_white};

    //二维码在背景图的位置信息 （和上面二维码的背景图要一一对应）
    private static String[] assertJsons = {"ee.json", "no_bg.json"};

    /**
     * 生成最终的花式二维码
     */
    int indexTag = 0;

    private void creatQRcode1() {
        //int result1 = mRandom.nextInt(qrBgs.length);
        Bitmap qrbgBitmap = BitmapFactory.decodeResource(getResources(), qrBgs[0]);
        //int result2 = mRandom.nextInt(qrBgIds.length);
        Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), qrBgIds[0]);
        String assertFileName = assertJsons[1];

        lastBitmap = SwitchCodeUtils.drawStyleQRcode(this, bgBitmap, qrbgBitmap, originalQrBitmap, assertFileName, 1);
        ivQrCode.setImageBitmap(lastBitmap);
        indexTag = 1;
        SharedPreferenceUtil.getInstance().putInt(Constant.CODE_STYLE, 1);
    }

    private void creatQRcode2() {
        //int result1 = mRandom.nextInt(qrBgs.length);
        Bitmap qrbgBitmap = BitmapFactory.decodeResource(getResources(), qrBgs[1]);
        //int result2 = mRandom.nextInt(qrBgIds.length);
        Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), qrBgIds[1]);
        String assertFileName = assertJsons[1];

        lastBitmap = SwitchCodeUtils.drawStyleQRcode(this, bgBitmap, qrbgBitmap, originalQrBitmap, assertFileName, 1);
        ivQrCode.setImageBitmap(lastBitmap);
        indexTag = 2;
        SharedPreferenceUtil.getInstance().putInt(Constant.CODE_STYLE, 2);
    }

    private void createDef() {
        ImageUtils.setNormalImage(this, qrImage, ivQrCode);
        indexTag = 0;
        SharedPreferenceUtil.getInstance().putInt(Constant.CODE_STYLE, 0);
    }

    @Override
    public void onPermissionGrant(List<String> permissions) {
        if (permissType) {
            boolean hasStorage = AndPermission.hasAlwaysDeniedPermission(this, Permission.Group.STORAGE);
            if (hasStorage) {
                if (qrImage == null) return;
                ImageUtils.saveImageToGallery(this, qrImage);
                toast.toastShow("保存成功,请在相册中查看");
            }
        } else {
            startActivity(new Intent(this, ScanQRCodeActivity.class));
        }
    }
}
