package com.daoyu.chat.module.home.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.chat.bean.LocalFriendData;
import com.daoyu.chat.module.home.adapter.LifeAdapter;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class BlackListActivity extends BaseTitleActivity implements LifeAdapter.OnItemClickListener {

    @BindView(R.id.rv_life)
    RecyclerView rvLife;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private UserBean.UserData userInfoData;
    private ArrayList<ContactFriendBean.ContactFriendData> contactClassLists;
    private ArrayList<ContactFriendBean.ContactFriendData> allContactClassLists;
    private LifeAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_life;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("黑名单");
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        rvLife.setLayoutManager(new LinearLayoutManager(this));
        contactClassLists = new ArrayList<>();
        allContactClassLists = new ArrayList<>();
        adapter = new LifeAdapter(this, contactClassLists);
        rvLife.setAdapter(adapter);
        adapter.setIsbalck(true);
        adapter.setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        swipeRefresh.setRefreshing(true);
        requestLifeClass();
    }

    private void requestLifeClass() {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_BLACK_LIST_CONTACT, params, this, new JsonCallback<ContactFriendBean>(ContactFriendBean.class) {
            @Override
            public void onSuccess(Response<ContactFriendBean> response) {
                if (isActivityFinish) return;
                swipeRefresh.setRefreshing(false);
                if (response == null || response.body() == null) return;
                ContactFriendBean body = response.body();
                if (body.success) {
                    ArrayList<ContactFriendBean.ContactFriendData> data = body.data;
                    contactClassLists.clear();
                    if (data != null && data.size() > 0) {
                        for (int i = 0; i < data.size(); i++) {
                            ContactFriendBean.ContactFriendData contactFriendData = data.get(i);
                            String fremarks = contactFriendData.fremarks;
                            String friendNickName = TextUtils.isEmpty(fremarks) ? contactFriendData.friendNickName : fremarks;
                            if (!TextUtils.isEmpty(friendNickName)) {
                                char firstChar = friendNickName.charAt(0);
                                if (Character.isLowerCase(firstChar) || Character.isUpperCase(firstChar)) {
                                    contactFriendData.firstLetter = String.valueOf(firstChar).toUpperCase();
                                } else if (Character.isDigit(firstChar)) {
                                    contactFriendData.firstLetter = "#";
                                } else if (ToolsUtil.isChinese(firstChar)) {
                                    String[] chinesePinyin = PinyinHelper.toHanyuPinyinStringArray(firstChar);
                                    String s = chinesePinyin[0];
                                    String letter = String.valueOf(s.charAt(0)).toUpperCase();
                                    contactFriendData.firstLetter = letter;
                                } else {
                                    contactFriendData.firstLetter = "#";
                                }
                            } else {
                                contactFriendData.firstLetter = "#";
                            }

                        }
                        if (data != null && data.size() > 0) {
                            Collections.sort(data);
                            contactClassLists.addAll(data);
                            allContactClassLists.addAll(data);
                        }
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onError(Response<ContactFriendBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                swipeRefresh.setRefreshing(false);
            }
        });
    }


    @Override
    public void onItemViewClick(ContactFriendBean.ContactFriendData contactClassData, int position) {
        if (contactClassData == null) return;
    }

    @Override
    public void onMoveViewClick(ContactFriendBean.ContactFriendData contactClassData, int position) {
        requestWhite(String.valueOf(contactClassData.userFriendId), contactClassData.remarks,position);

    }

    @Override
    public void onEditViewAfterTextChanged(Editable s) {
        String input = s.toString().trim();
        contactClassLists.clear();
        if (TextUtils.isEmpty(input)) {
            contactClassLists.addAll(allContactClassLists);
        } else {
            for (int i = 0; i < allContactClassLists.size(); i++) {
                ContactFriendBean.ContactFriendData contactFriendData = allContactClassLists.get(i);
                String friendNickName = contactFriendData.friendNickName;
                if (!TextUtils.isEmpty(friendNickName)) {
                    if (friendNickName.contains(input)) {
                        if (!contactClassLists.contains(contactFriendData)) {
                            contactClassLists.add(contactFriendData);
                        }
                    }
                }
                String fremarks = contactFriendData.fremarks;
                if (!TextUtils.isEmpty(fremarks)) {
                    if (fremarks.contains(input)) {
                        if (!contactClassLists.contains(contactFriendData)) {
                            contactClassLists.add(contactFriendData);
                        }
                    }
                }

            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 拉白
     */
    private void requestWhite(String friendId, String remarks,int position) {
        showLoading("请稍后...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", friendId);
        params.put("user_id", String.valueOf(userInfoData.uid));
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_FRIEND_WHITE, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.code==1) {
                    toast.toastShow(body.msg);
                    Map<String, LocalFriendData> normalFriendMap = IMConstant.normalFriendMap;
                    if (normalFriendMap.containsKey(friendId)) {
                        normalFriendMap.put(friendId, new LocalFriendData(remarks, 0));
                    }
                    if (contactClassLists != null || contactClassLists.size() > 0) {
                        int pos = position - 1;
                        contactClassLists.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
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
}
