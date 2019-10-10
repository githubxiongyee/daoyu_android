package com.daoyu.chat.module.group.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.view.CircleImageView;

import butterknife.BindView;

public class GroupQRCodeActivity extends BaseTitleActivity {

    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    private GroupInfoBean.GroupInfoData groupInfoData;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_group_qrcode;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("群二维码");
        Intent intent = getIntent();
        groupInfoData = intent.getParcelableExtra(Constant.CONTACT_GROUP_INFO);
        if (groupInfoData == null) {
            finish();
            return;
        }

        ImageUtils.setNormalImage(this, groupInfoData.groupurl + "?imageMogr2/thumbnail/200/quality/40", R.drawable.ic_placeholder, R.drawable.ic_placeholder, ivHeader);
        tvName.setText(groupInfoData.groupname);
        Bitmap qrImage = ToolsUtil.createQRImage(groupInfoData.group_id, getResources().getDimensionPixelSize(R.dimen.dp_189));
        ivQrCode.setImageBitmap(qrImage);
    }
}
