package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.adapter.MobileContactAdapter;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.ContactMobileBean;
import com.daoyu.chat.utils.ContactUtils;
import com.daoyu.chat.utils.NoLeakHandler;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;

/**
 * 添加手机联系人
 */
public class AddMobileContactActivity extends BaseTitleActivity implements MobileContactAdapter.OnItemViewClickMobile {
    @BindView(R.id.tv_search)
    EditText tvSearch;
    @BindView(R.id.rv_contact_mobile)
    RecyclerView rvContactMobile;
    private ArrayList<ContactMobileBean.ContactMobileData> allContacts;
    private MobileContactAdapter adapter;
    private int uid;
    private String mobile;
    private ExecutorService singleThreadExecutor;
    private NoLeakHandler handler;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_add_mobile_contact;
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null) return;
        if (message.what == 1) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            StringBuffer stringBuff = new StringBuffer();
            StringBuffer sbNames = new StringBuffer();
            for (int i = 0; i < allContacts.size(); i++) {
                ContactMobileBean.ContactMobileData contactMobileData = allContacts.get(i);
                stringBuff.append(contactMobileData.mobile + ",");
                String name = contactMobileData.name;
                if (name.contains(",")) {
                    name.replace(",", " ");
                }
                sbNames.append(name + ",");

            }
            String mbiles = stringBuff.toString();
            String names = sbNames.toString();
            if (TextUtils.isEmpty(mbiles)) {
                hideLoading();
                return;
            }
            requestContactInfo(mbiles.substring(0, mbiles.length() - 1));

            if (TextUtils.isEmpty(names)) {
                return;
            }
            requestUploadContact(names.substring(0, names.length() - 1), mbiles.substring(0, mbiles.length() - 1));
        }
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("我的通讯录");

        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        uid = userInfoData.uid;
        rvContactMobile.setLayoutManager(new LinearLayoutManager(this));
        allContacts = new ArrayList<>();
        adapter = new MobileContactAdapter(this, allContacts);
        adapter.setClickMobile(this);
        rvContactMobile.setAdapter(adapter);
        handler = new NoLeakHandler(this);
        showLoading("请稍等", false);
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() -> {
            ArrayList<ContactMobileBean.ContactMobileData> contacts = ContactUtils.getAllContacts(AddMobileContactActivity.this);
            if (contacts != null && contacts.size() > 0) {
                for (int i = 0; i < contacts.size(); i++) {
                    ContactMobileBean.ContactMobileData contactMobileData = contacts.get(i);
                    String name = contactMobileData.name;
                    if (!TextUtils.isEmpty(name)) {
                        char firstChar = name.charAt(0);
                        if (Character.isLowerCase(firstChar) || Character.isUpperCase(firstChar)) {
                            contactMobileData.firstLetter = String.valueOf(firstChar).toUpperCase();
                        } else if (Character.isDigit(firstChar)) {
                            contactMobileData.firstLetter = "#";
                        } else if (ToolsUtil.isChinese(firstChar)) {
                            String[] chinesePinyin = PinyinHelper.toHanyuPinyinStringArray(firstChar);
                            String s = chinesePinyin[0];
                            String letter = String.valueOf(s.charAt(0)).toUpperCase();
                            contactMobileData.firstLetter = letter;
                        } else {
                            contactMobileData.firstLetter = "#";
                        }
                    } else {
                        contactMobileData.firstLetter = "#";
                    }
                }
                allContacts.clear();
                allContacts.addAll(contacts);
            }
            Collections.sort(allContacts);
            handler.sendEmptyMessage(1);
        });


    }

    private void requestContactInfo(String contact) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", uid);
        params.put("user_mobiles", contact);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_CONTACT_MOBILE, params, this, new JsonCallback<ContactMobileBean>(ContactMobileBean.class) {
            @Override
            public void onSuccess(Response<ContactMobileBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                ContactMobileBean body = response.body();
                if (body.success) {
                    ArrayList<ContactMobileBean.ContactMobileData> contactMobileData = body.data;
                    if (contactMobileData == null || contactMobileData.size() <= 0) return;
                    for (int i = 0; i < contactMobileData.size(); i++) {
                        ContactMobileBean.ContactMobileData contactMobileData1 = allContacts.get(i);
                        ContactMobileBean.ContactMobileData contactMobileData2 = contactMobileData.get(i);
                        contactMobileData1.isUser = contactMobileData2.isUser;
                        contactMobileData1.isFriend = contactMobileData2.isFriend;
                        contactMobileData1.userId = contactMobileData2.userId;
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Response<ContactMobileBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }


    private void requestUploadContact(String contactNames, String contact) {
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("user_id", uid);
        params.put("user_mobiles", contact);
        params.put("user_names", contactNames);
        params.put("auth_token", userInfoData.token);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_UPLOAD_CONTACT, params, this, new JsonCallback<ContactMobileBean>(ContactMobileBean.class) {
            @Override
            public void onSuccess(Response<ContactMobileBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                ContactMobileBean body = response.body();
                if (body.code == 1) {

                }
            }
        });
    }


    @Override
    public void onItemClickListener(int position, ContactMobileBean.ContactMobileData contactMobileData, String type) {
        if (contactMobileData == null) return;
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        String userId = contactMobileData.userId;
        switch (type) {
            case "添加":
                intent.putExtra(Constant.CONTACT_ID, userId);
                intent.putExtra(Constant.CONTACT_TYPE, "add");
                startActivity(intent);
                break;
            case "已添加":
                intent.putExtra(Constant.CONTACT_ID, userId);
                intent.putExtra(Constant.CONTACT_TYPE, "message");
                startActivity(intent);
                break;
            case "邀请":
                mobile = contactMobileData.mobile;
                sendSMS(mobile);
                break;
        }
    }

    private void sendSMS(String mobile) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mobile));
        intent.putExtra("sms_body", "Hi，我正在使用潜言app，领红包，每天10块+，天天都有,邀你一起来！邀请码是：" + userInfoData.invateCode + "。更多惊喜：Android下载地址:https://www.pgyer.com/bz4i , iOS请前往APP Store。");
        startActivity(intent);
    }
}
