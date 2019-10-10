package com.daoyu.chat.module.home.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseFragment;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.ApplyFriendEvent;
import com.daoyu.chat.module.chat.activity.CommonContactImageActivity;
import com.daoyu.chat.module.chat.activity.NearbyPeopleActivity;
import com.daoyu.chat.module.group.activity.GroupListActivity;
import com.daoyu.chat.module.home.activity.AddContactActivity;
import com.daoyu.chat.module.home.activity.BlackListActivity;
import com.daoyu.chat.module.home.activity.ContactDetailsActivity;
import com.daoyu.chat.module.home.activity.LifeActivity;
import com.daoyu.chat.module.home.activity.MainActivity;
import com.daoyu.chat.module.home.activity.SearchCommonActivity;
import com.daoyu.chat.module.home.activity.WorkActivity;
import com.daoyu.chat.module.home.adapter.ContactAdapter;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.module.im.module.ApplyFriendTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.SideBar;
import com.lzy.okgo.model.Response;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 通讯录
 */
public class ContactFragment extends BaseFragment implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener, ContactAdapter.OnContactItemViewClickListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.iv_search)
    ImageView ivSearch;

    @BindView(R.id.iv_add)
    ImageView ivAdd;

    @BindView(R.id.tv_address_book)
    TextView tvAddressBook;

    @BindView(R.id.relative)
    RelativeLayout relative;

    @BindView(R.id.rv_contact)
    RecyclerView rvContact;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private ArrayList<ContactFriendBean.ContactFriendData> contactFriends;
    private ContactAdapter adapter;
    private UserBean.UserData userInfoData;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_contact;
    }

    double latitude;
    double longitude;

    @Override
    protected void initEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        initListener();
        contactFriends = new ArrayList<>();
        rvContact.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactAdapter(getContext(), contactFriends);
        adapter.setListener(this);
        rvContact.setAdapter(adapter);
    }

    private void initListener() {
        ivSearch.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefresh.setRefreshing(true);
        requestContactFriend();

        LitePal.where("self_uid = ? and status = ?", String.valueOf(userInfoData.uid), "1").findAsync(ApplyFriendTable.class).listen(list -> {
            if (list == null || list.size() <= 0) {
                adapter.setNumber(0);
            } else {
                adapter.setNumber(list.size());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_search:
                startActivity(new Intent(getActivity(), SearchCommonActivity.class));
                break;
            case R.id.iv_add:
                showTopRightPopMenu(ivAdd);
                break;
        }
    }

    private void requestContactFriend() {
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_WHITE_LABEL_LIST, params, this, new JsonCallback<ContactFriendBean>(ContactFriendBean.class) {
            @Override
            public void onSuccess(Response<ContactFriendBean> response) {
                if (isFragmentFinish) return;
                swipeRefresh.setRefreshing(false);
                if (response == null || response.body() == null) return;
                ContactFriendBean body = response.body();
                ArrayList<ContactFriendBean.ContactFriendData> contactFriendList = body.data;
                contactFriends.clear();
                if (contactFriendList != null && contactFriendList.size() > 0) {
                    for (int i = 0; i < contactFriendList.size(); i++) {
                        ContactFriendBean.ContactFriendData contactFriendData = contactFriendList.get(i);
                        String remarks = contactFriendData.fremarks;
                        String friendNickName = TextUtils.isEmpty(remarks) ? contactFriendData.friendNickName : remarks;
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
                }
                if (contactFriendList == null || contactFriendList.size() <= 0) return;
                Collections.sort(contactFriendList);
                contactFriends.addAll(contactFriendList);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(Response<ContactFriendBean> response) {
                super.onError(response);
                if (isFragmentFinish) return;
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        toast.toastShow(s);
    }

    @Override
    public void onContactMenu(int menu) {
        switch (menu) {
            case Constant.InnerContact.ADD_CONTACT:
                adapter.setNumber(0);
                startActivity(new Intent(getActivity(), AddContactActivity.class));
                break;
            case Constant.InnerContact.LIFT:
                startActivity(new Intent(getActivity(), LifeActivity.class));
                break;
            case Constant.InnerContact.WORK:
                startActivity(new Intent(getActivity(), WorkActivity.class));
                break;
            case Constant.InnerContact.GROUP:
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case Constant.InnerContact.NEARBY:
                //附近的人
                latitude = MainActivity.latitude;
                longitude = MainActivity.longitude;
                Intent nearbyIntent = new Intent(this.getActivity(), NearbyPeopleActivity.class);
                nearbyIntent.putExtra("latitude", latitude);
                nearbyIntent.putExtra("longitude", longitude);
                Log.e("info", "///////=========MainActivity==传值的时候：" + longitude + "---" + latitude);
                startActivity(nearbyIntent);
                break;
            case Constant.InnerContact.BLACKLIST:
                startActivity(new Intent(getActivity(), BlackListActivity.class));
                break;
        }
    }

    @Override
    public void onContactFriend(int position) {
        if (contactFriends == null || contactFriends.size() <= 0) return;
        ContactFriendBean.ContactFriendData contactFriendData = contactFriends.get(position - 1);
        Intent intent = new Intent(getActivity(), ContactDetailsActivity.class);
        intent.putExtra(Constant.CONTACT_ID, String.valueOf(contactFriendData.userFriendId));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        requestContactFriend();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyFriendEvent(ApplyFriendEvent event) {
        if (event != null) {
            boolean show = event.show;
            if (show) {
                LitePal.where("self_uid = ? and status = ?", String.valueOf(userInfoData.uid), "1").findAsync(ApplyFriendTable.class).listen(list -> {
                    if (list == null || list.size() <= 0) {
                        adapter.setNumber(0);
                    } else {
                        adapter.setNumber(list.size());
                    }
                });
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
