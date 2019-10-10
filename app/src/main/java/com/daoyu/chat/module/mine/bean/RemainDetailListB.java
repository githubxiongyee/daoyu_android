package com.daoyu.chat.module.mine.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RemainDetailListB {

    /**
     * msg : 请求成功
     * code : 1
     * data : [{"id":281,"orderNo":"67693609289584642","payTime":1567234867000,"money":0.1,"payWay":3},{"id":282,"orderNo":"67694400909938690","payTime":1567235041000,"money":0.1,"payWay":1},{"id":283,"orderNo":"67694635455418369","payTime":1567235076000,"money":0.1,"payWay":2},{"id":286,"orderNo":"67696453904306178","payTime":1567235553000,"money":0.34,"payWay":3},{"id":292,"orderNo":"67698570744369153","payTime":1567236036000,"money":0.1,"payWay":3},{"id":293,"orderNo":"67698855709577217","payTime":1567236083000,"money":0.8,"payWay":3},{"id":296,"orderNo":"67699420824932353","payTime":1567236220000,"money":0.36,"payWay":3},{"id":317,"orderNo":"67730972086669314","payTime":1567243746000,"money":0.1,"payWay":2},{"id":323,"orderNo":"67732083187159041","payTime":1567244011000,"money":0.1,"payWay":1},{"id":324,"orderNo":"67732408887447553","payTime":1567244085000,"money":0.1,"payWay":1},{"id":328,"orderNo":"68350356790480897","payTime":1567391425000,"money":0.8,"payWay":3},{"id":332,"orderNo":"68356855520595970","payTime":1567392968000,"money":0.1,"payWay":1},{"id":336,"orderNo":"68401703539740674","payTime":1567403654000,"money":0.48,"payWay":3},{"id":337,"orderNo":"68402041441259521","payTime":1567403732000,"money":0.1,"payWay":2},{"id":343,"orderNo":"68453520504078337","payTime":1567416048000,"money":0.1,"payWay":1},{"id":344,"orderNo":"68454493695852545","payTime":1567416272000,"money":0.1,"payWay":1}]
     * success : true
     */

    private String msg;
    private int code;
    private boolean success;
    private List<DataBean> data;

    public static RemainDetailListB objectFromData(String str) {

        return new Gson().fromJson(str, RemainDetailListB.class);
    }

    public static RemainDetailListB objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), RemainDetailListB.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<RemainDetailListB> arrayRemainDetailListBFromData(String str) {

        Type listType = new TypeToken<ArrayList<RemainDetailListB>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<RemainDetailListB> arrayRemainDetailListBFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<RemainDetailListB>>() {
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 281
         * orderNo : 67693609289584642
         * payTime : 1567234867000
         * money : 0.1
         * payWay : 3
         */

        private int id;
        private String orderNo;
        private long payTime;
        private double money;
        private int payWay;

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

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public long getPayTime() {
            return payTime;
        }

        public void setPayTime(long payTime) {
            this.payTime = payTime;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public int getPayWay() {
            return payWay;
        }

        public void setPayWay(int payWay) {
            this.payWay = payWay;
        }
    }
}
