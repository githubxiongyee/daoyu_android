package com.daoyu.chat.module.mine.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daoyu.chat.R;
import com.daoyu.chat.module.mine.adapter.ChooseAddressAdapter;
import com.daoyu.chat.module.mine.bean.AddressBean;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.utils.ToolsUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 选择省市区
 */
public class ChooseProvinceCityAreaDialog extends DialogFragment implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.tv_deliver)
    TextView tvDeliver;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.tv_province)
    TextView tvProvince;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.rv_address)
    RecyclerView rvAddress;

    private Unbinder mUnbind;
    private IChooseProvinceCityAreaListener mListener;
    private ChooseAddressAdapter adapter;
    private String choseType = null;
    private ArrayList<AddressBean> address;
    private String provinceId;
    private String cityId;

    public static ChooseProvinceCityAreaDialog getInstance() {
        ChooseProvinceCityAreaDialog dialog = new ChooseProvinceCityAreaDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IChooseProvinceCityAreaListener) {
            mListener = (IChooseProvinceCityAreaListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(true);
        View inflate = inflater.inflate(R.layout.dialog_province_city_area, container, false);
        mUnbind = ButterKnife.bind(this, inflate);
        initEvent();
        return inflate;
    }

    private void initEvent() {
        ivClose.setOnClickListener(this);
        tvProvince.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvArea.setOnClickListener(this);

        tvProvince.setText("请选择");
        tvProvince.setTextColor(getResources().getColor(R.color.colorAccent));
        choseType = "province";

        tvCity.setVisibility(View.INVISIBLE);
        tvArea.setVisibility(View.INVISIBLE);

        rvAddress.setLayoutManager(new LinearLayoutManager(getContext()));
        address = new ArrayList<>();
        adapter = new ChooseAddressAdapter(R.layout.item_address, address);
        adapter.setListener(this);
        rvAddress.setAdapter(adapter);
        initProvince();

    }

    private void initProvince() {
        address.clear();
        String province = ToolsUtil.getJson(getContext(), "province.json");
        ArrayList<AddressBean> addressLists = new Gson().fromJson(province, new TypeToken<ArrayList<AddressBean>>() {
        }.getType());
        if (addressLists != null && addressLists.size() > 0) {
            address.addAll(addressLists);
        }
        adapter.notifyDataSetChanged();
    }

    private void initCity(String provinceId) {
        address.clear();
        String city = ToolsUtil.getJson(getContext(), "city.json");
        try {
            JSONObject object = new JSONObject(city);
            JSONArray cityArray = object.getJSONArray(provinceId);
            for (int i = 0; i < cityArray.length(); i++) {
                JSONObject cityObject = cityArray.getJSONObject(i);
                String name = cityObject.optString("name");
                String id = cityObject.optString("id");
                address.add(new AddressBean(name, id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

    }

    private void initArea(String cityId) {
        address.clear();
        String country = ToolsUtil.getJson(getContext(), "country.json");
        try {
            JSONObject object = new JSONObject(country);
            JSONArray countryArray = object.optJSONArray(cityId);
            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject countryObject = countryArray.getJSONObject(i);
                String name = countryObject.optString("name");
                String id = countryObject.optString("id");
                address.add(new AddressBean(name, id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = DensityUtil.getScreenWidth();
    }

    @Override
    public void onDetach() {
        if (mUnbind != null) {
            mUnbind.unbind();
        }
        if (mListener != null) {
            mListener = null;
        }
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_province:
                choseType = "province";
                tvProvince.setText("请选择");
                initProvince();
                tvProvince.setTextColor(getResources().getColor(R.color.colorAccent));
                tvCity.setVisibility(View.INVISIBLE);
                tvArea.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_city:
                choseType = "city";
                tvCity.setVisibility(View.VISIBLE);
                tvCity.setText("请选择");
                if (TextUtils.isEmpty(provinceId)) return;
                initCity(provinceId);
                tvCity.setTextColor(getResources().getColor(R.color.colorAccent));
                tvArea.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_area:
                choseType = "area";
                tvArea.setVisibility(View.VISIBLE);
                tvArea.setText("请选择");
                if (TextUtils.isEmpty(cityId)) return;
                initArea(cityId);
                tvArea.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
            case R.id.iv_close:
                dismissAllowingStateLoss();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        AddressBean addressBean = address.get(position);
        switch (choseType) {
            case "province":
                provinceId = addressBean.id;
                tvProvince.setText(addressBean.name);
                tvProvince.setTextColor(getResources().getColor(R.color.color_1A1A1A));
                tvCity.setVisibility(View.VISIBLE);
                choseType = "city";
                initCity(provinceId);
                break;
            case "city":
                cityId = addressBean.id;
                tvCity.setText(addressBean.name);
                tvCity.setTextColor(getResources().getColor(R.color.color_1A1A1A));
                tvArea.setVisibility(View.VISIBLE);
                choseType = "area";
                initArea(cityId);
                break;
            case "area":
                tvArea.setText(addressBean.name);
                tvCity.setTextColor(getResources().getColor(R.color.color_1A1A1A));
                if (mListener == null) return;
                mListener.onChooseAddressResult(tvProvince.getText().toString() + tvCity.getText().toString() + addressBean.name);
                dismissAllowingStateLoss();
                break;
        }
    }

    public interface IChooseProvinceCityAreaListener {
        void onChooseAddressResult(String address);
    }
}
