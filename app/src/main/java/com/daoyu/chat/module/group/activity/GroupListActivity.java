package com.daoyu.chat.module.group.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.group.adapter.GroupChatListAdapter;
import com.daoyu.chat.module.group.bean.GroupChatListBean;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.module.login.bean.UserBean;
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

import butterknife.BindView;

/**
 * 群聊列表
 */
public class GroupListActivity extends BaseTitleActivity implements GroupChatListAdapter.OnItemClickListener {

    @BindView(R.id.rv_group_list)
    RecyclerView rvGroupList;
    private UserBean.UserData userInfoData;
    private ArrayList<GroupInfoBean.GroupInfoData> groupInfos;
    private GroupChatListAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_group_list;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("群聊");
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        rvGroupList.setLayoutManager(new LinearLayoutManager(this));
        groupInfos = new ArrayList<>();
        adapter = new GroupChatListAdapter(this, groupInfos);
        adapter.setListener(this);
        rvGroupList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestGroupList();
    }

    private void requestGroupList() {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SEARCH_USER_ALL_GROUP, params, this, new JsonCallback<GroupChatListBean>(GroupChatListBean.class) {
            @Override
            public void onSuccess(Response<GroupChatListBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                GroupChatListBean body = response.body();
                if (body.code == 1) {
                    ArrayList<GroupInfoBean.GroupInfoData> groupInfoDatas = body.data;
                    groupInfos.clear();
                    if (groupInfoDatas == null || groupInfoDatas.size() <= 0) {
                        return;
                    }
                    for (int i = 0; i < groupInfoDatas.size(); i++) {
                        GroupInfoBean.GroupInfoData groupInfoData = groupInfoDatas.get(i);
                        String groupname = groupInfoData.groupname;
                        if (!TextUtils.isEmpty(groupname)) {
                            char firstChar = groupname.charAt(0);
                            if (Character.isLowerCase(firstChar) || Character.isUpperCase(firstChar)) {
                                groupInfoData.firstLetter = String.valueOf(firstChar).toUpperCase();
                            } else if (Character.isDigit(firstChar)) {
                                groupInfoData.firstLetter = "#";
                            } else if (ToolsUtil.isChinese(firstChar)) {
                                String[] chinesePinyin = PinyinHelper.toHanyuPinyinStringArray(firstChar);
                                String s = chinesePinyin[0];
                                String letter = String.valueOf(s.charAt(0)).toUpperCase();
                                groupInfoData.firstLetter = letter;
                            } else {
                                groupInfoData.firstLetter = "#";
                            }
                        } else {
                            groupInfoData.firstLetter = "#";
                        }
                    }
                    if (groupInfoDatas.size() <= 0) {
                        return;
                    }
                    Collections.sort(groupInfoDatas);
                    groupInfos.addAll(groupInfoDatas);
                    adapter.notifyDataSetChanged();
                }

            }
        },UrlConfig.BASE_GROUP_URL);
    }

    @Override
    public void onItemViewClick(GroupInfoBean.GroupInfoData groupInfoData, int position) {
        if (groupInfoData == null) return;
        Intent intent = new Intent(this, GroupChatActivity.class);
        intent.putExtra(Constant.FRIEND_NAME, groupInfoData.groupname);
        intent.putExtra(Constant.FRIEND_UID, groupInfoData.group_id);
        intent.putExtra(Constant.FRIEND_HEADER, groupInfoData.groupurl);
        startActivity(intent);
    }
}
