package com.daoyu.chat.module.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseFragment;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.event.RedBagUpdateStatusEvent;
import com.daoyu.chat.module.envelope.activity.RedBagBrandDetailActivity;
import com.daoyu.chat.module.envelope.activity.RedBagNewDetailActivity;
import com.daoyu.chat.module.envelope.activity.RewardRulesAct;
import com.daoyu.chat.module.home.activity.SearchCommonActivity;
import com.daoyu.chat.module.home.adapter.RedBadAdapter;
import com.daoyu.chat.module.home.bean.RedBagAdsBean;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 红包
 */
public class RedBagFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener {
    @BindView(R.id.iv_search)
    ImageView ivSearch;

    @BindView(R.id.iv_add)
    ImageView ivAdd;

    @BindView(R.id.rv_red_bag)
    RecyclerView rvRedBag;

    @BindView(R.id.iv_gift_rules)
    ImageView ivRules;

    private Context context;
    private RedBadAdapter adapter;

    private ArrayList<RedBagAdsBean.RedBagData> redBagLists;
    private ArrayList<RedBagAdsBean.RedBagData> explosionLists;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_red_bag;
    }

    @Override
    public void onResume() {
        super.onResume();
        redBagLists.clear();
        explosionLists.clear();
        requestRedBag();
        requestExplosion();
    }

    @Override
    protected void initEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        context = getContext();
        redBagLists = new ArrayList<>();
        explosionLists = new ArrayList<>();
        ivSearch.setOnClickListener(this);
        ivAdd.setOnClickListener(this);

        ivRules.setOnClickListener(this);

        rvRedBag.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RedBadAdapter(getContext());
        adapter.setListener(this);
        rvRedBag.setAdapter(adapter);

        redBagLists.clear();
        explosionLists.clear();
        requestRedBag();
        requestExplosion();

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
            case R.id.iv_gift_rules:
                startActivity(new Intent(this.getContext(), RewardRulesAct.class));
                break;
        }
    }


    @Override
    public void onItemClick(Object data, int position) {
        if (data instanceof Integer) {
            int pos = (int) data;
            if (pos == 1) {
                if (redBagLists == null || redBagLists.size() <= 0) return;
                RedBagAdsBean.RedBagData redBagData = redBagLists.get(position);
                Intent intent = new Intent(getActivity(), RedBagBrandDetailActivity.class);
                intent.putExtra(Constant.RED_BAG_ADS, redBagData);
                startActivity(intent);
            } else {
                if (explosionLists == null || explosionLists.size() <= 0) return;
                RedBagAdsBean.RedBagData redBagData = explosionLists.get(position);
                Intent intent = new Intent(getActivity(), RedBagNewDetailActivity.class);
                intent.putExtra(Constant.RED_BAG_ADS, redBagData);
                startActivity(intent);
            }
        }
    }


    private void requestRedBag() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        CHttpUtils.getInstance().requestGetFormServer(UrlConfig.URL_REB_BAG_ADS_LIST + "?channal=A00000&oneClass=" + 1 + "&uid=" + userInfoData.uid, this, new JsonCallback<RedBagAdsBean>(RedBagAdsBean.class) {
            @Override
            public void onSuccess(Response<RedBagAdsBean> response) {
                if (isFragmentFinish) return;
                if (response == null || response.body() == null) return;
                RedBagAdsBean body = response.body();
                if (body.success) {
                    redBagLists.clear();
                    ArrayList<RedBagAdsBean.RedBagData> redBagDataLists = body.data;
                    if (redBagDataLists != null && redBagDataLists.size() > 0) {
                        redBagLists.addAll(redBagDataLists);
                        adapter.setRedBagLists(redBagLists);
                        adapter.notifyItemChanged(0);
                    } else {
                        adapter.setRedBagLists(new ArrayList<RedBagAdsBean.RedBagData>());
                    }
                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<RedBagAdsBean> response) {
                super.onError(response);
                if (isFragmentFinish) return;
                redBagLists.clear();
                adapter.setRedBagLists(new ArrayList<RedBagAdsBean.RedBagData>());
            }
        });
    }


    private void requestExplosion() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        CHttpUtils.getInstance().requestGetFormServer(UrlConfig.URL_EXPLOSION_LIST + "?channal=A00000&oneClass=" + 2 + "&uid=" + userInfoData.uid, this, new JsonCallback<RedBagAdsBean>(RedBagAdsBean.class) {
            @Override
            public void onSuccess(Response<RedBagAdsBean> response) {
                if (isFragmentFinish) return;
                if (response == null || response.body() == null) return;
                RedBagAdsBean body = response.body();
                if (body.success) {
                    explosionLists.clear();
                    ArrayList<RedBagAdsBean.RedBagData> explosionDataLists = body.data;
                    if (explosionDataLists != null && explosionDataLists.size() > 0) {
                        explosionLists.addAll(explosionDataLists);
                        adapter.setExplosionLists(explosionLists);
                        adapter.notifyItemChanged(1);
                    } else {
                        adapter.setExplosionLists(new ArrayList<RedBagAdsBean.RedBagData>());
                    }
                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<RedBagAdsBean> response) {
                super.onError(response);
                if (isFragmentFinish) return;
                adapter.setExplosionLists(new ArrayList<RedBagAdsBean.RedBagData>());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRedBagUpdateStatusEvent(RedBagUpdateStatusEvent event) {
        if (event == null) return;
        int position = event.position;
        RedBagAdsBean.RedBagData redBagData = event.redBagData;
        int pId = redBagData.pId;
        if (position == 0) {
            if (redBagLists != null && redBagLists.size() > 0) {
                for (int i = 0; i < redBagLists.size(); i++) {
                    RedBagAdsBean.RedBagData redBagDataCurrent = redBagLists.get(i);
                    int pIdCurrent = redBagDataCurrent.pId;
                    if (pIdCurrent == pId) {
                        redBagDataCurrent.hongbaoSta = "Y";
                        break;
                    }
                }
            }
        } else if (position == 1) {
            if (explosionLists != null && explosionLists.size() > 0) {
                for (int i = 0; i < explosionLists.size(); i++) {
                    RedBagAdsBean.RedBagData redBagDataCurrent = explosionLists.get(i);
                    int pIdCurrent = redBagDataCurrent.pId;
                    if (pIdCurrent == pId) {
                        redBagDataCurrent.hongbaoSta = "Y";
                        break;
                    }
                }
            }
        }
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
