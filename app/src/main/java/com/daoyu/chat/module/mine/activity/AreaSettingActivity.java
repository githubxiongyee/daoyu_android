package com.daoyu.chat.module.mine.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.adapter.CountryOrRegionAdapter;
import com.daoyu.chat.module.mine.bean.CountryOrRegion;
import com.daoyu.chat.module.mine.strategy.ISortStrategy;
import com.daoyu.chat.module.mine.strategy.impl.EnglishSortStrategy;
import com.daoyu.chat.module.mine.strategy.impl.PinyinSortStrategy;
import com.daoyu.chat.module.mine.strategy.impl.StrokeSortStrategy;
import com.daoyu.chat.module.mine.utils.AreaCodeUtils;
import com.daoyu.chat.module.mine.utils.JSONUtil;
import com.daoyu.chat.module.mine.utils.SystemLanguageUtil;
import com.daoyu.chat.module.mine.view.SideBar;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.pinyinhelper.PinyinMapDict;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

//修改地区
public class AreaSettingActivity extends BaseActivity {
    private static final String TAG = AreaSettingActivity.class.getSimpleName();
    ListView sortListView;

    SideBar sideBar;

    TextView dialog;

    private CountryOrRegionAdapter adapter;

    private List<CountryOrRegion> mSourceDateList;

    private ISortStrategy sortStrategy;

    private ProgressDialog progressDialog = null;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.edit_nick)
    EditText editNick;

    //int index = 0;默认是第一个
    int index = 166;//默认变中国
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_area_setting;
    }

    @Override
    protected void initEvent() {
        tvTitle.setText("修改地区");

        tvCancel.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        tvComplete.setEnabled(true);
        editNick.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String nick = s.toString();
                if (TextUtils.isEmpty(nick)) {
                    tvComplete.setEnabled(true);
                } else {
                    tvComplete.setEnabled(true);
                }
            }
        });

        verifyStoragePermissions(this);
        // 添加自定义词典   https://github.com/promeG/TinyPinyin
        Pinyin.init(Pinyin.newConfig()
                .with(new PinyinMapDict() {
                    @Override
                    public Map<String, String[]> mapping() {
                        HashMap<String, String[]> map = new HashMap<String, String[]>();
                        map.put("秘鲁", new String[]{"BI", "LU"});
                        return map;
                    }
                }));

        initView();
        initData();
    }

    /*
     * android 动态权限申请
     * */
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        sortListView = findViewById(R.id.country_lvcountry);
        sideBar = findViewById(R.id.sidrbar);
        dialog = findViewById(R.id.dialog);

        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                dealSideBarItemSelected(s);
            }
        });

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryOrRegion countryOrRegion = (CountryOrRegion) adapter.getItem(position);
                dealItemClick(countryOrRegion);
                for (CountryOrRegion countryOrRegion1 : mSourceDateList){
                    countryOrRegion1.setChecked(false);
                }
                mSourceDateList.get(position).setChecked(true);
                index = position;
                adapter.notifyDataSetChanged();
            }
        });

    }
    private void dealSideBarItemSelected(String s) {
        // 该字母首次出现的位置
        int position = sortStrategy.getSideBarSortSectionFirstShowPosition(adapter, s);
        if (position != -1) {
            sortListView.setSelection(position);
        }
    }

    private void initData() {
        progressDialog = ProgressDialog.show(this, "Loading...", "正在加载测试数据...");
        sideBar.setVisibility(View.GONE);

        Observable.just("")
                .map(new Func1<String, List<CountryOrRegion>>() {
                    @Override
                    public List<CountryOrRegion> call(String s) {
                        // TODO: 2018/11/15 模拟从服务器拿到的数据
                        String areaCodeListJson = "[{\"areaCode\":\"86\",\"countryCode\":\"CN\"},{\"areaCode\":\"852\",\"countryCode\":\"HK\"},{\"areaCode\":\"66\",\"countryCode\":\"TH\"},{\"areaCode\":\"60\",\"countryCode\":\"MY\"},{\"areaCode\":\"84\",\"countryCode\":\"VN\"},{\"areaCode\":\"62\",\"countryCode\":\"ID\"},{\"areaCode\":\"91\",\"countryCode\":\"IN\"},{\"areaCode\":\"886\",\"countryCode\":\"TW\"},{\"areaCode\":\"1\",\"countryCode\":\"US\"},{\"areaCode\":\"63\",\"countryCode\":\"PH\"},{\"areaCode\":\"855\",\"countryCode\":\"KH\"},{\"areaCode\":\"856\",\"countryCode\":\"LA\"},{\"areaCode\":\"95\",\"countryCode\":\"MM\"},{\"areaCode\":\"65\",\"countryCode\":\"SG\"},{\"areaCode\":\"98\",\"countryCode\":\"IR\"},{\"areaCode\":\"853\",\"countryCode\":\"MO\"},{\"areaCode\":\"61\",\"countryCode\":\"AU\"},{\"areaCode\":\"44\",\"countryCode\":\"GB\"},{\"areaCode\":\"64\",\"countryCode\":\"NZ\"},{\"areaCode\":\"39\",\"countryCode\":\"IT\"},{\"areaCode\":\"81\",\"countryCode\":\"JP\"},{\"areaCode\":\"43\",\"countryCode\":\"AT\"},{\"areaCode\":\"34\",\"countryCode\":\"ES\"},{\"areaCode\":\"1\",\"countryCode\":\"CA\"},{\"areaCode\":\"7\",\"countryCode\":\"RU\"},{\"areaCode\":\"20\",\"countryCode\":\"EG\"},{\"areaCode\":\"27\",\"countryCode\":\"ZA\"},{\"areaCode\":\"30\",\"countryCode\":\"GR\"},{\"areaCode\":\"31\",\"countryCode\":\"NL\"},{\"areaCode\":\"32\",\"countryCode\":\"BE\"},{\"areaCode\":\"33\",\"countryCode\":\"FR\"},{\"areaCode\":\"36\",\"countryCode\":\"HU\"},{\"areaCode\":\"40\",\"countryCode\":\"RO\"},{\"areaCode\":\"41\",\"countryCode\":\"CH\"},{\"areaCode\":\"45\",\"countryCode\":\"DK\"},{\"areaCode\":\"46\",\"countryCode\":\"SE\"},{\"areaCode\":\"47\",\"countryCode\":\"NO\"},{\"areaCode\":\"48\",\"countryCode\":\"PL\"},{\"areaCode\":\"49\",\"countryCode\":\"DE\"},{\"areaCode\":\"51\",\"countryCode\":\"PE\"},{\"areaCode\":\"52\",\"countryCode\":\"MX\"},{\"areaCode\":\"53\",\"countryCode\":\"CU\"},{\"areaCode\":\"54\",\"countryCode\":\"AR\"},{\"areaCode\":\"55\",\"countryCode\":\"BR\"},{\"areaCode\":\"56\",\"countryCode\":\"CL\"},{\"areaCode\":\"57\",\"countryCode\":\"CO\"},{\"areaCode\":\"58\",\"countryCode\":\"VE\"},{\"areaCode\":\"82\",\"countryCode\":\"KR\"},{\"areaCode\":\"90\",\"countryCode\":\"TR\"},{\"areaCode\":\"92\",\"countryCode\":\"PK\"},{\"areaCode\":\"93\",\"countryCode\":\"AF\"},{\"areaCode\":\"94\",\"countryCode\":\"LK\"},{\"areaCode\":\"212\",\"countryCode\":\"MA\"},{\"areaCode\":\"213\",\"countryCode\":\"DZ\"},{\"areaCode\":\"216\",\"countryCode\":\"TN\"},{\"areaCode\":\"218\",\"countryCode\":\"LY\"},{\"areaCode\":\"220\",\"countryCode\":\"GM\"},{\"areaCode\":\"221\",\"countryCode\":\"SN\"},{\"areaCode\":\"223\",\"countryCode\":\"ML\"},{\"areaCode\":\"224\",\"countryCode\":\"GN\"},{\"areaCode\":\"226\",\"countryCode\":\"BF\"},{\"areaCode\":\"227\",\"countryCode\":\"NE\"},{\"areaCode\":\"228\",\"countryCode\":\"TG\"},{\"areaCode\":\"229\",\"countryCode\":\"BJ\"},{\"areaCode\":\"230\",\"countryCode\":\"MU\"},{\"areaCode\":\"231\",\"countryCode\":\"LR\"},{\"areaCode\":\"232\",\"countryCode\":\"SL\"},{\"areaCode\":\"233\",\"countryCode\":\"GH\"},{\"areaCode\":\"998\",\"countryCode\":\"UZ\"},{\"areaCode\":\"234\",\"countryCode\":\"NG\"},{\"areaCode\":\"235\",\"countryCode\":\"TD\"},{\"areaCode\":\"236\",\"countryCode\":\"CF\"},{\"areaCode\":\"237\",\"countryCode\":\"CM\"},{\"areaCode\":\"239\",\"countryCode\":\"ST\"},{\"areaCode\":\"241\",\"countryCode\":\"GA\"},{\"areaCode\":\"242\",\"countryCode\":\"CG\"},{\"areaCode\":\"244\",\"countryCode\":\"AO\"},{\"areaCode\":\"248\",\"countryCode\":\"SC\"},{\"areaCode\":\"249\",\"countryCode\":\"SD\"},{\"areaCode\":\"251\",\"countryCode\":\"ET\"},{\"areaCode\":\"252\",\"countryCode\":\"SO\"},{\"areaCode\":\"253\",\"countryCode\":\"DJ\"},{\"areaCode\":\"254\",\"countryCode\":\"KE\"},{\"areaCode\":\"255\",\"countryCode\":\"TZ\"},{\"areaCode\":\"256\",\"countryCode\":\"UG\"},{\"areaCode\":\"257\",\"countryCode\":\"BI\"},{\"areaCode\":\"258\",\"countryCode\":\"MZ\"},{\"areaCode\":\"260\",\"countryCode\":\"ZM\"},{\"areaCode\":\"261\",\"countryCode\":\"MG\"},{\"areaCode\":\"263\",\"countryCode\":\"ZW\"},{\"areaCode\":\"264\",\"countryCode\":\"NA\"},{\"areaCode\":\"265\",\"countryCode\":\"MW\"},{\"areaCode\":\"266\",\"countryCode\":\"LS\"},{\"areaCode\":\"267\",\"countryCode\":\"BW\"},{\"areaCode\":\"268\",\"countryCode\":\"SZ\"},{\"areaCode\":\"7\",\"countryCode\":\"KZ\"},{\"areaCode\":\"996\",\"countryCode\":\"KG\"},{\"areaCode\":\"350\",\"countryCode\":\"GI\"},{\"areaCode\":\"351\",\"countryCode\":\"PT\"},{\"areaCode\":\"352\",\"countryCode\":\"LU\"},{\"areaCode\":\"353\",\"countryCode\":\"IE\"},{\"areaCode\":\"354\",\"countryCode\":\"IS\"},{\"areaCode\":\"355\",\"countryCode\":\"AL\"},{\"areaCode\":\"356\",\"countryCode\":\"MT\"},{\"areaCode\":\"357\",\"countryCode\":\"CY\"},{\"areaCode\":\"358\",\"countryCode\":\"FI\"},{\"areaCode\":\"359\",\"countryCode\":\"BG\"},{\"areaCode\":\"370\",\"countryCode\":\"LT\"},{\"areaCode\":\"371\",\"countryCode\":\"LV\"},{\"areaCode\":\"372\",\"countryCode\":\"EE\"},{\"areaCode\":\"373\",\"countryCode\":\"MD\"},{\"areaCode\":\"374\",\"countryCode\":\"AM\"},{\"areaCode\":\"375\",\"countryCode\":\"BY\"},{\"areaCode\":\"376\",\"countryCode\":\"AD\"},{\"areaCode\":\"377\",\"countryCode\":\"MC\"},{\"areaCode\":\"378\",\"countryCode\":\"SM\"},{\"areaCode\":\"380\",\"countryCode\":\"UA\"},{\"areaCode\":\"386\",\"countryCode\":\"SI\"},{\"areaCode\":\"420\",\"countryCode\":\"CZ\"},{\"areaCode\":\"421\",\"countryCode\":\"SK\"},{\"areaCode\":\"423\",\"countryCode\":\"LI\"},{\"areaCode\":\"501\",\"countryCode\":\"BZ\"},{\"areaCode\":\"502\",\"countryCode\":\"GT\"},{\"areaCode\":\"503\",\"countryCode\":\"SV\"},{\"areaCode\":\"504\",\"countryCode\":\"HN\"},{\"areaCode\":\"505\",\"countryCode\":\"NI\"},{\"areaCode\":\"506\",\"countryCode\":\"CR\"},{\"areaCode\":\"507\",\"countryCode\":\"PA\"},{\"areaCode\":\"509\",\"countryCode\":\"HT\"},{\"areaCode\":\"591\",\"countryCode\":\"BO\"},{\"areaCode\":\"592\",\"countryCode\":\"GY\"},{\"areaCode\":\"593\",\"countryCode\":\"EC\"},{\"areaCode\":\"594\",\"countryCode\":\"GF\"},{\"areaCode\":\"595\",\"countryCode\":\"PY\"},{\"areaCode\":\"597\",\"countryCode\":\"SR\"},{\"areaCode\":\"598\",\"countryCode\":\"UY\"},{\"areaCode\":\"673\",\"countryCode\":\"BN\"},{\"areaCode\":\"674\",\"countryCode\":\"NR\"},{\"areaCode\":\"675\",\"countryCode\":\"PG\"},{\"areaCode\":\"676\",\"countryCode\":\"TO\"},{\"areaCode\":\"677\",\"countryCode\":\"SB\"},{\"areaCode\":\"679\",\"countryCode\":\"FJ\"},{\"areaCode\":\"682\",\"countryCode\":\"CK\"},{\"areaCode\":\"689\",\"countryCode\":\"PF\"},{\"areaCode\":\"850\",\"countryCode\":\"KP\"},{\"areaCode\":\"880\",\"countryCode\":\"BD\"},{\"areaCode\":\"960\",\"countryCode\":\"MV\"},{\"areaCode\":\"961\",\"countryCode\":\"LB\"},{\"areaCode\":\"962\",\"countryCode\":\"JO\"},{\"areaCode\":\"963\",\"countryCode\":\"SY\"},{\"areaCode\":\"964\",\"countryCode\":\"IQ\"},{\"areaCode\":\"965\",\"countryCode\":\"KW\"},{\"areaCode\":\"966\",\"countryCode\":\"SA\"},{\"areaCode\":\"967\",\"countryCode\":\"YE\"},{\"areaCode\":\"968\",\"countryCode\":\"OM\"},{\"areaCode\":\"971\",\"countryCode\":\"AE\"},{\"areaCode\":\"972\",\"countryCode\":\"IL\"},{\"areaCode\":\"973\",\"countryCode\":\"BH\"},{\"areaCode\":\"974\",\"countryCode\":\"QA\"},{\"areaCode\":\"976\",\"countryCode\":\"MN\"},{\"areaCode\":\"977\",\"countryCode\":\"NP\"},{\"areaCode\":\"992\",\"countryCode\":\"TJ\"},{\"areaCode\":\"993\",\"countryCode\":\"TM\"},{\"areaCode\":\"994\",\"countryCode\":\"AZ\"},{\"areaCode\":\"995\",\"countryCode\":\"GE\"},{\"areaCode\":\"1242\",\"countryCode\":\"BS\"},{\"areaCode\":\"1876\",\"countryCode\":\"JM\"}]";
                        List<CountryOrRegion> countryOrRegionList = JSONUtil.toCollection(areaCodeListJson, List.class, CountryOrRegion.class);
                        return countryOrRegionList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CountryOrRegion>>() {
                    @Override
                    public void call(List<CountryOrRegion> mCountryOrRegionList) {
                        if (mCountryOrRegionList != null && mCountryOrRegionList.size() > 0) {
                            initAndShowList(mCountryOrRegionList);
                        }
                    }
                });
    }
    /**
     * 初始化排序策略
     */
    private ISortStrategy initSortStrategy() {
        //获取语言地区代码，比如：zh-CN zh-HK en-US
        String localLanguageAndCountry = SystemLanguageUtil.getLocalLanguageAndCountry(this);
        Log.d(TAG, "localLanguageAndCountry = " + localLanguageAndCountry);
        //如果是使用繁体字的几个地区的话，则使用  笔画排序
        if (localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_ZH_TW_VALUE)
                || localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_ZH_HK_VALUE)
                || localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_ZH_MO_VALUE)
                || localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_HANT)) {
            Log.d(TAG, "使用笔画排序");
            return new StrokeSortStrategy();
        } else if (localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_ZH_CN_VALUE_DETAILS)) {
            //简体中文 使用 拼音排序
            Log.d(TAG, "使用拼音排序");
            return new PinyinSortStrategy();
        } else {
            //其他的都使用 英文排序
            Log.d(TAG, "使用英文排序");
            return new EnglishSortStrategy();
        }
    }
    /**
     * 初始化数据并显示
     */
    private void initAndShowList(List<CountryOrRegion> countryOrRegionList) {
        //初始化排序策略
        sortStrategy = initSortStrategy();
        Observable.just(countryOrRegionList)
                .map(new Func1<List<CountryOrRegion>, List<CountryOrRegion>>() {
                    @Override
                    public List<CountryOrRegion> call(List<CountryOrRegion> countryOrRegionList) {
                        //填充数据
                        mSourceDateList = filledData(countryOrRegionList);
                        //排序
                        if (mSourceDateList != null) {
                            mSourceDateList = sortStrategy.getSortedCountryOrRegionList(mSourceDateList);
                        }
                        return mSourceDateList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CountryOrRegion>>() {
                    @Override
                    public void call(List<CountryOrRegion> countryOrRegions) {
                        adapter = new CountryOrRegionAdapter(AreaSettingActivity.this, mSourceDateList, sortStrategy);
                        sortListView.setAdapter(adapter);

                        //设置 侧边栏需要展示的列表
                        sideBar.setB(sortStrategy.getSideBarSortShowItemArray(mSourceDateList, AreaSettingActivity.this));
                        sideBar.setVisibility(View.VISIBLE);

                        closeDialog();
                    }
                });
    }
    private void closeDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }
    /**
     * 为ListView填充数据
     */
    private List<CountryOrRegion> filledData(List<CountryOrRegion> mCountryOrRegionList) {
        List<CountryOrRegion> mSortList = new ArrayList<>();
        if (mCountryOrRegionList == null) {
            return mSortList;
        }
        for (int i = 0; i < mCountryOrRegionList.size(); i++) {
            CountryOrRegion sortModel = mCountryOrRegionList.get(i);
            //获取到要显示的国家或地区的名称
            String name = AreaCodeUtils.getCountryNameByCountryCode(this, sortModel.getCountryCode());
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            sortModel.setName(name);

            //根据不同的策略，返回不同语言下的中文汉字的笔画
            sortModel.setStrokeCount(sortStrategy.getStrokeCount(name, AreaSettingActivity.this));
            //根据不同的策略，返回不同的pinyin或者English
            String pinyin = sortStrategy.getPinyinOrEnglish(sortModel);
            sortModel.setPinyinName(pinyin);
            //根据不同的策略，返回拼音排序或者英文排序需要展示的 英文首字母
            String sortLetters = sortStrategy.getSortLetters(pinyin);
            sortModel.setSortLetters(sortLetters);

            mSortList.add(sortModel);
        }
        return mSortList;
    }
    private void dealItemClick(CountryOrRegion countryOrRegion) {
        // TODO: 2018/11/15 实现你自己的逻辑
//        if (null != countryOrRegion) {
//            if (null != countryOrRegion.getCountryCode()) {
//                ChooseCountryOrRegionActivity.this.finish();
//                String toastContent = "Name = " + countryOrRegion.getName()
//                        + "AreaCode = " + countryOrRegion.getAreaCode()
//                        + ",countryCode = " + countryOrRegion.getCountryCode();
//                Log.d(TAG,toastContent);
////                Toast.makeText(ChooseCountryOrRegionActivity.this, toastContent, Toast.LENGTH_LONG).show();
//            }
//        }
    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_complete:
                UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                //userInfoData.area = mSourceDateList.get(index).getName();
                userInfoData.addressD = mSourceDateList.get(index).getName();
                requestSaveArea(mSourceDateList.get(index).getName());

                break;
        }
    }

    /**
     * 请求修改地区接口
     *
     */
    private void requestSaveArea(String area) {
        showLoading("保存中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("addressD", area);
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
                    UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                    userInfoData.addressD = area;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    toast.toastShow(body.msg);
                    finish();
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
