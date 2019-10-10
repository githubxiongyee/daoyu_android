package com.daoyu.chat.module.system.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.ShippingAddressData;
import com.daoyu.chat.module.mine.dialog.ChooseProvinceCityAreaDialog;
import com.daoyu.chat.module.system.bean.AddAddressBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 新建收货地址
 */
public class AddAddressActivity extends BaseTitleActivity implements ChooseProvinceCityAreaDialog.IChooseProvinceCityAreaListener {
    @BindView(R.id.edit_shopping_name)
    EditText editShoppingName;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.edit_phone_number)
    EditText editPhoneNumber;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.edit_address_detail)
    EditText editAddressDetail;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.switch_set_default)
    Switch switchBtn;
    boolean isDef = false;

    private ChooseProvinceCityAreaDialog chooseProvinceCityAreaDialog;
    private boolean isUpdate = false;
    private int adId;

    private boolean shoppingStatus = false;
    private boolean phoneNumberStatus = false;
    private boolean areaStatus = false;
    private boolean addressStatus = false;
    private ShippingAddressData address;
    private boolean paymentInto;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_add_address;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("新建收货地址");
        ivClear.setOnClickListener(this);
        tvArea.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        editAddressDetail.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editAddressDetail.setSingleLine(false);
        editAddressDetail.setMaxLines(2);
        editAddressDetail.setHorizontallyScrolling(false);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isDef = true;
                }else{
                    isDef = false;
                }
            }
        });
        editShoppingName.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (TextUtils.isEmpty(input)) {
                    ivClear.setVisibility(View.GONE);
                    shoppingStatus = false;
                    tvSave.setEnabled(false);
                } else {
                    ivClear.setVisibility(View.VISIBLE);
                    shoppingStatus = true;
                    if (shoppingStatus && phoneNumberStatus && addressStatus && areaStatus) {
                        tvSave.setEnabled(true);
                    } else {
                        tvSave.setEnabled(false);
                    }
                }
            }
        });
        editPhoneNumber.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (TextUtils.isEmpty(input)) {
                    phoneNumberStatus = false;
                    tvSave.setEnabled(false);
                } else {
                    phoneNumberStatus = true;
                    if (shoppingStatus && phoneNumberStatus && addressStatus && areaStatus) {
                        tvSave.setEnabled(true);
                    } else {
                        tvSave.setEnabled(false);
                    }
                }
            }
        });
        editAddressDetail.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (TextUtils.isEmpty(input)) {
                    addressStatus = false;
                    tvSave.setEnabled(false);
                } else {
                    addressStatus = true;
                    if (shoppingStatus && phoneNumberStatus && addressStatus && areaStatus) {
                        tvSave.setEnabled(true);
                    } else {
                        tvSave.setEnabled(false);
                    }
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            address = intent.getParcelableExtra(Constant.ADDRESS_BEAN);
            paymentInto = intent.getBooleanExtra(Constant.PAYMENT_INTO, false);
        }
        if (address != null) {
            isUpdate = true;
            String aAddress = address.aAddress;
            String[] split = aAddress.split("=\\+=");
            tvArea.setText(split[0]);
            tvArea.setTextColor(getResources().getColor(R.color.color_1A1A1A));
            editAddressDetail.setText(split[1]);
            editPhoneNumber.setText(address.aPhone);
            editShoppingName.setText(address.aNick);
            adId = address.id;
            tvSave.setEnabled(true);
            areaStatus = true;
            if (address.isDef.equals("Y")){
                isDef = true;
            }else {
                isDef = false;
            }
            switchBtn.setChecked(isDef);

        } else {
            isUpdate = false;
            tvSave.setEnabled(false);
        }

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_save:
                String shoppingName = editShoppingName.getText().toString();
                if (TextUtils.isEmpty(shoppingName)) {
                    toast.toastShow("请填写收货人");
                    return;
                }
                String phoneNumber = editPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    toast.toastShow("请填写收货人手机号");
                    return;
                }
                if (!phoneNumber.startsWith("1")) {
                    toast.toastShow("请填写正确的收货人手机号");
                    return;
                }
                if (phoneNumber.length() != 11) {
                    toast.toastShow("请填写正确的收货人手机号");
                    return;
                }
                String area = tvArea.getText().toString();
                if (TextUtils.isEmpty(area) || "省、市、区选择".equals(area)) {
                    toast.toastShow("请选择省、市、区");
                    return;
                }
                String address = editAddressDetail.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    toast.toastShow("请填写详细地址");
                    return;
                }
                String longAddress = area + "=+=" + address;
                requestSaveAddress(shoppingName, phoneNumber, longAddress, isUpdate);
                break;
            case R.id.iv_clear:
                editShoppingName.setText("");
                editShoppingName.clearComposingText();
                break;
            case R.id.tv_area:
                showChooseAddressDialog();
                break;
        }
    }

    private void requestSaveAddress(String name, String phoneNumber, String address, boolean isUpdate) {
        showLoading("请稍后...", false);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        params.put("nickName", name);
        params.put("aPhone", phoneNumber);
        params.put("aAddress", address);
        if (isUpdate) {
            params.put("adid", adId);
        }
        params.put("sta", isUpdate ? 2 : 1);
        if (isDef){
            params.put("isDef","Y");
        }else {
            params.put("isDef","N");
        }

        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_UPDATA_ADD_ADDRESS, params, this, new JsonCallback<AddAddressBean>(AddAddressBean.class) {
            @Override
            public void onSuccess(Response<AddAddressBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    if (paymentInto) {
                        int addressId = (int) body.data;
                        ShippingAddressData shippingAddressData;
                        if (isDef) {
                            shippingAddressData = new ShippingAddressData(addressId, name, phoneNumber, address,"Y");
                        } else {
                            shippingAddressData = new ShippingAddressData(addressId, name, phoneNumber, address,"N");
                        }
                        Intent data = new Intent();
                        data.putExtra(Constant.ADDRESS_BEAN, shippingAddressData);
                        setResult(RESULT_OK, data);
                    }
                    toast.toastShow(body.msg);
                    finish();
                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<AddAddressBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }


    private void showChooseAddressDialog() {
        if (chooseProvinceCityAreaDialog != null) {
            chooseProvinceCityAreaDialog.dismissAllowingStateLoss();
        }
        chooseProvinceCityAreaDialog = ChooseProvinceCityAreaDialog.getInstance();
        if (!chooseProvinceCityAreaDialog.isAdded()) {
            chooseProvinceCityAreaDialog.show(getSupportFragmentManager(), "ChooseProvinceCityAreaDialog");
        } else {
            chooseProvinceCityAreaDialog.dismissAllowingStateLoss();
        }
    }


    @Override
    public void onChooseAddressResult(String address) {
        tvArea.setText(address);
        tvArea.setTextColor(getResources().getColor(R.color.color_1A1A1A));
        areaStatus = true;
        if (shoppingStatus && phoneNumberStatus && addressStatus && areaStatus) {
            tvSave.setEnabled(true);
        } else {
            tvSave.setEnabled(false);
        }
    }
}
