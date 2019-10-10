package com.daoyu.chat.module.home.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QrAddFriendB {

    /**
     * msg : 请求成功
     * code : 1
     * data : {"id":76,"mobile":"18028767791","nickName":"电信号发货","headImg":"http://image.futruedao.com/1565761350730.jpg","sex":"F","remarks":"废后将军剪短发解决方法回家","addressD":null,"fType":"N","isBlack":null}
     * success : true
     */

    private String msg;
    private int code;
    private DataBean data;
    private boolean success;

    public static QrAddFriendB objectFromData(String str) {

        return new Gson().fromJson(str, QrAddFriendB.class);
    }

    public static QrAddFriendB objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), QrAddFriendB.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<QrAddFriendB> arrayQrAddFriendBFromData(String str) {

        Type listType = new TypeToken<ArrayList<QrAddFriendB>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<QrAddFriendB> arrayQrAddFriendBFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<QrAddFriendB>>() {
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
         * id : 76
         * mobile : 18028767791
         * nickName : 电信号发货
         * headImg : http://image.futruedao.com/1565761350730.jpg
         * sex : F
         * remarks : 废后将军剪短发解决方法回家
         * addressD : null
         * fType : N
         * isBlack : null
         */

        private int id;
        private String mobile;
        private String nickName;
        private String headImg;
        private String sex;
        private String remarks;
        private String addressD;
        private String fType;
        private Object isBlack;

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

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getAddressD() {
            return addressD;
        }

        public void setAddressD(String addressD) {
            this.addressD = addressD;
        }

        public String getFType() {
            return fType;
        }

        public void setFType(String fType) {
            this.fType = fType;
        }

        public Object getIsBlack() {
            return isBlack;
        }

        public void setIsBlack(Object isBlack) {
            this.isBlack = isBlack;
        }
    }
}
