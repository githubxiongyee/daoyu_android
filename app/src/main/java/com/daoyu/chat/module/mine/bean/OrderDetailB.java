package com.daoyu.chat.module.mine.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailB  {

    /**
     * msg : 请求成功
     * code : 1
     * data : {"id":153,"orderNumber":"63392568291729410","orderStatus":"1","expCompany":null,"expCode":null,"expNo":null,"signName":"来看","signPhone":"13454657676","signAddress":"天津市市辖区和平区=+=u已经太阳镜同样","orderTime":1566209378000,"payWay":"1","payTime":1566209380000,"amountPayable":188,"amountRealpay":187.98,"remarks":null,"appid":"wxcd8c9aa2163b6d9c","partnerid":"1516774851","packagePay":"Sign=WXPay","noncestr":"9ABjBqYeGsdUtC84","timestamp":"1566209380","prepayid":"wx19180940470413723b94dee31006583300","sign":"77DD00F442A53807D10E41A7C00F5B91"}
     * success : true
     */

    private String msg;
    private int code;
    private DataBean data;
    private boolean success;

    public static OrderDetailB objectFromData(String str) {

        return new Gson().fromJson(str, OrderDetailB.class);
    }

    public static OrderDetailB objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), OrderDetailB.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<OrderDetailB> arrayOrderDetailBFromData(String str) {

        Type listType = new TypeToken<ArrayList<OrderDetailB>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<OrderDetailB> arrayOrderDetailBFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<OrderDetailB>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBean {
        /**
         * id : 153
         * orderNumber : 63392568291729410
         * orderStatus : 1
         * expCompany : null
         * expCode : null
         * expNo : null
         * signName : 来看
         * signPhone : 13454657676
         * signAddress : 天津市市辖区和平区=+=u已经太阳镜同样
         * orderTime : 1566209378000
         * payWay : 1
         * payTime : 1566209380000
         * amountPayable : 188
         * amountRealpay : 187.98
         * remarks : null
         * appid : wxcd8c9aa2163b6d9c
         * partnerid : 1516774851
         * packagePay : Sign=WXPay
         * noncestr : 9ABjBqYeGsdUtC84
         * timestamp : 1566209380
         * prepayid : wx19180940470413723b94dee31006583300
         * sign : 77DD00F442A53807D10E41A7C00F5B91
         */

        private int id;
        private String orderNumber;
        private String orderStatus;
        private Object expCompany;
        private Object expCode;
        private String expNo;
        private String signName;
        private String signPhone;
        private String signAddress;
        private long orderTime;
        private String payWay;
        private long payTime;
        private double amountPayable;
        private double amountRealpay;
        private Object remarks;
        private String appid;
        private String partnerid;
        private String packagePay;
        private String noncestr;
        private String timestamp;
        private String prepayid;
        private String sign;

        public static DataBean objectFromData(String str) {

            return new Gson().fromJson(str, DataBean.class);
        }

        public static DataBean objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DataBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<DataBean> arrayDataBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<DataBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<DataBean> arrayDataBeanFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DataBean>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public Object getExpCompany() {
            return expCompany;
        }

        public void setExpCompany(Object expCompany) {
            this.expCompany = expCompany;
        }

        public Object getExpCode() {
            return expCode;
        }

        public void setExpCode(Object expCode) {
            this.expCode = expCode;
        }

        public String getExpNo() {
            return expNo;
        }

        public void setExpNo(String expNo) {
            this.expNo = expNo;
        }

        public String getSignName() {
            return signName;
        }

        public void setSignName(String signName) {
            this.signName = signName;
        }

        public String getSignPhone() {
            return signPhone;
        }

        public void setSignPhone(String signPhone) {
            this.signPhone = signPhone;
        }

        public String getSignAddress() {
            return signAddress;
        }

        public void setSignAddress(String signAddress) {
            this.signAddress = signAddress;
        }

        public long getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(long orderTime) {
            this.orderTime = orderTime;
        }

        public String getPayWay() {
            return payWay;
        }

        public void setPayWay(String payWay) {
            this.payWay = payWay;
        }

        public long getPayTime() {
            return payTime;
        }

        public void setPayTime(long payTime) {
            this.payTime = payTime;
        }

        public double getAmountPayable() {
            return amountPayable;
        }

        public void setAmountPayable(double amountPayable) {
            this.amountPayable = amountPayable;
        }

        public double getAmountRealpay() {
            return amountRealpay;
        }

        public void setAmountRealpay(double amountRealpay) {
            this.amountRealpay = amountRealpay;
        }

        public Object getRemarks() {
            return remarks;
        }

        public void setRemarks(Object remarks) {
            this.remarks = remarks;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPackagePay() {
            return packagePay;
        }

        public void setPackagePay(String packagePay) {
            this.packagePay = packagePay;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}
