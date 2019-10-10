package com.daoyu.chat.module.group.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.group.adapter.GroupMemberAdapter;
import com.daoyu.chat.module.group.bean.UsersHeaderBean;
import com.daoyu.chat.module.home.activity.ContactDetailsActivity;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 群成员列表 所有
 */
public class GroupMemberAllActivity extends BaseTitleActivity implements OnItemClickListener {

    @BindView(R.id.rv_group_member)
    RecyclerView rvGroupMember;
    private ArrayList<UsersHeaderBean.UsersHeaderData> userAllHeaders;
    private GroupMemberAdapter memberAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_group_memberall;
    }

    @Override
    protected void initEvent() {
        userAllHeaders = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        rvGroupMember.setLayoutManager(gridLayoutManager);
        memberAdapter = new GroupMemberAdapter(this, userAllHeaders);
        memberAdapter.setListener(this);
        rvGroupMember.setAdapter(memberAdapter);
        Intent intent = getIntent();
        ArrayList<UsersHeaderBean.UsersHeaderData> userHeaders = intent.getParcelableArrayListExtra(Constant.GROUP_MEMBER_ALL);
        if (userHeaders == null || userHeaders.size() <= 0) {
            return;
        }
        userAllHeaders.clear();
        userAllHeaders.addAll(userHeaders);
        memberAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Object data, int position) {
        if (data == null) return;
        if (data instanceof UsersHeaderBean.UsersHeaderData) {
            UsersHeaderBean.UsersHeaderData usersHeaderData = (UsersHeaderBean.UsersHeaderData) data;
            int type = usersHeaderData.type;
            if (type == 0) {
                Intent intent = new Intent(this, ContactDetailsActivity.class);
                intent.putExtra(Constant.CONTACT_ID, usersHeaderData.userId);
                startActivity(intent);
            } else if (type == 1) {//邀请

            } else if (type == 2) {//移除
                //TODO

            } else if (type == 3) {//更多
                if (userAllHeaders == null || userAllHeaders.size() <= 0) return;
                Intent intent = new Intent(this, GroupMemberAllActivity.class);
                intent.putParcelableArrayListExtra(Constant.GROUP_MEMBER_ALL, userAllHeaders);
                startActivity(intent);
            }

        }
    }
}
