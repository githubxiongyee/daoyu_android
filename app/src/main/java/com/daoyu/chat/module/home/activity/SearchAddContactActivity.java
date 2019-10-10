package com.daoyu.chat.module.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.adapter.SearchContactAdapter;
import com.daoyu.chat.module.home.bean.SearchContactBean;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 搜索添加联系人
 */
public class SearchAddContactActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.edit_search)
    EditText editSearch;

    @BindView(R.id.rv_search_result)
    RecyclerView rvSearchResult;
    private ArrayList<SearchContactBean.SearchContactData> searchContacts;
    private SearchContactAdapter adapter;
    private View viewEmpty;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search_add_contact;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("添加联系人");
        viewEmpty = View.inflate(SearchAddContactActivity.this, R.layout.layout_search_empty, null);

        editSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText etSearch = (EditText) v;
                String hintStr;
                if (hasFocus) {
                    hintStr = etSearch.getHint().toString();
                    etSearch.setTag(hintStr);
                    etSearch.setHint("");
                } else {
                    hintStr = etSearch.getTag().toString();
                    etSearch.setHint(hintStr);
                }
            }
        });
        //showKeyboard(editSearch);

        /*editSearch.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                String keyword = editSearch.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    toast.toastShow("请先输入昵称或手机号");
                    return false;
                }
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(SearchAddContactActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                doSearch(keyword);
            }

            return false;
        });*/
        /*editSearch.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    toast.toastShow("请先输入昵称或手机号");
                    return;
                }
                doSearch(keyword);
            }
        });*/
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                    if (event.getAction() == KeyEvent.ACTION_UP){
                        String keyword = editSearch.getText().toString();
                        if (TextUtils.isEmpty(keyword) ) {
                            toast.toastShow("请先输入昵称或手机号");
                            searchContacts.clear();
                            adapter.notifyDataSetChanged();
                            return false;
                        }else if (keyword.length() < 3){
                            toast.toastShow("请至少输入3个字符");
                            searchContacts.clear();
                            adapter.notifyDataSetChanged();
                            return false;
                        }
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(SearchAddContactActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        doSearch(keyword);
                    }

                    return true;
                }
                return false;
            }
        });

        bindingRecyclerView(rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResult.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = getResources().getDimensionPixelSize(R.dimen.dp_10);
                }
            }
        });

        searchContacts = new ArrayList<>();
        adapter = new SearchContactAdapter(R.layout.item_search_contact, searchContacts);
        adapter.setListener(this);
        rvSearchResult.setAdapter(adapter);
    }

    //弹出软键盘
    public void showKeyboard(EditText editText) {
        //其中editText为dialog中的输入框的 EditText
        if (editText != null) {
            //设置可获得焦点
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            //请求获得焦点
            editText.requestFocus();
            //调用系统输入法
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void doSearch(String keyword) {
        requestSearchContact(keyword);
    }

    private void requestSearchContact(String keyword) {
        searchContacts.clear();
        Map<String, Object> params = new HashMap<>();
        params.put("s_condition", keyword);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SEARCH_CONTACT, params, this, new JsonCallback<SearchContactBean>(SearchContactBean.class) {
            @Override
            public void onSuccess(Response<SearchContactBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                SearchContactBean body = response.body();
                if (body.success) {
                    ArrayList<SearchContactBean.SearchContactData> searchContactLists = body.data;
                    if (searchContactLists != null && searchContactLists.size() > 0) {
                        searchContacts.addAll(searchContactLists);
                        adapter.notifyDataSetChanged();
                        mStatusLayoutManager.showSuccessLayout();
                    } else {
                        mStatusLayoutManager.showCustomLayout(viewEmpty);
                    }
                } else {
                    mStatusLayoutManager.showCustomLayout(viewEmpty);
                }
            }

            @Override
            public void onError(Response<SearchContactBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                mStatusLayoutManager.showCustomLayout(viewEmpty);
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (searchContacts == null || searchContacts.size() <= 0) return;
        SearchContactBean.SearchContactData searchContactData = searchContacts.get(position);
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        intent.putExtra(Constant.CONTACT_ID, String.valueOf(searchContactData.id));
        intent.putExtra(Constant.CONTACT_TYPE, "add");
        startActivity(intent);
        finish();
    }

}
