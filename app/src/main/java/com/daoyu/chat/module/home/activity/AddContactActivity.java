package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.event.ApplyFriendEvent;
import com.daoyu.chat.module.home.adapter.AddContactAdapter;
import com.daoyu.chat.module.im.module.ApplyFriendTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 添加联系人
 */
public class AddContactActivity extends BaseTitleActivity implements OnItemClickListener {

    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_add_mobile_contact)
    TextView tvAddMobileContact;
    @BindView(R.id.rv_apply_list)
    RecyclerView rvApplyList;
    private ArrayList<ApplyFriendTable> applyAsFriends;
    private AddContactAdapter adapter;
    private UserBean.UserData userInfoData;

    @Override
    protected void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {
            return;
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_add_contact;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("添加联系人");
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        EventBus.getDefault().post(new ApplyFriendEvent(false));
        tvSearch.setOnClickListener(this);
        tvAddMobileContact.setOnClickListener(this);
        applyAsFriends = new ArrayList<>();
        rvApplyList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddContactAdapter(this, applyAsFriends);
        adapter.setListener(this);
        rvApplyList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        LitePal.where("self_uid = ?", String.valueOf(userInfoData.uid)).findAsync(ApplyFriendTable.class).listen(new FindMultiCallback<ApplyFriendTable>() {
            @Override
            public void onFinish(List<ApplyFriendTable> list) {
                if (list != null && list.size() > 0) {
                    applyAsFriends.clear();
                    applyAsFriends.addAll(list);
                    adapter = new AddContactAdapter(AddContactActivity.this, applyAsFriends);
                    adapter.setListener(AddContactActivity.this);
                    rvApplyList.setAdapter(adapter);
                    if (applyAsFriends == null || applyAsFriends.size() <= 0) {
                        rvApplyList.setVisibility(View.GONE);
                    } else {
                        rvApplyList.setVisibility(View.VISIBLE);
                    }
                }else {
                    rvApplyList.setVisibility(View.GONE);
                }
            }

        });


    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_search://搜索
                startActivity(new Intent(AddContactActivity.this, SearchAddContactActivity.class));
                break;
            case R.id.tv_add_mobile_contact://添加手机联系人
                requestPermission(Permission.READ_CONTACTS);
                break;
        }
    }

    @Override
    public void onItemClick(Object data, int position) {
        ApplyFriendTable applyFriendData = applyAsFriends.get(position);
        String status = applyFriendData.status;
        if ("1".equals(status)) {
            Intent intent = new Intent(this, ContactDetailsActivity.class);
            ContactDetailsActivity.applyFriendData = applyFriendData;
            intent.putExtra(Constant.CONTACT_ID, applyFriendData.userid);
            intent.putExtra(Constant.CONTACT_APPLY, true);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ContactDetailsActivity.class);
            ContactDetailsActivity.applyFriendData = applyFriendData;
            intent.putExtra(Constant.CONTACT_ID, applyFriendData.userid);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyFriendEvent(ApplyFriendEvent event) {
        LitePal.where("self_uid = ?", String.valueOf(userInfoData.uid)).findAsync(ApplyFriendTable.class).listen(list -> {
            if (list != null && list.size() > 0) {
                applyAsFriends.clear();
                applyAsFriends.addAll(list);
                adapter = new AddContactAdapter(AddContactActivity.this, applyAsFriends);
                adapter.setListener(AddContactActivity.this);
                rvApplyList.setAdapter(adapter);
                if (applyAsFriends == null || applyAsFriends.size() <= 0) {
                    rvApplyList.setVisibility(View.GONE);
                } else {
                    rvApplyList.setVisibility(View.VISIBLE);
                }
            }else {
                rvApplyList.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPermissionGrant(List<String> permissions) {
        if (AndPermission.hasPermissions(this, Permission.READ_CONTACTS)) {
            startActivity(new Intent(this, AddMobileContactActivity.class));
        }
    }
}
