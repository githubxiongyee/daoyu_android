package com.daoyu.chat.module.mine.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UpgradeApkB {

    /**
     * msg : 1
     * code : 1
     * data : [{"id":1,"constantCode":"Android-APP","constantName":"Android-APP1.1","module":"IM","createId":0,"createTime":1566958827000,"updateId":0,"updateTime":1566958827000,"url":"http://....","remarks":"testtest"}]
     * success : true
     */

    private String msg;
    private int code;
    private boolean success;
    private List<DataBean> data;

    public static UpgradeApkB objectFromData(String str) {

        return new Gson().fromJson(str, UpgradeApkB.class);
    }

    public static UpgradeApkB objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), UpgradeApkB.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<UpgradeApkB> arrayUpgradeApkBFromData(String str) {

        Type listType = new TypeToken<ArrayList<UpgradeApkB>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<UpgradeApkB> arrayUpgradeApkBFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<UpgradeApkB>>() {
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
         * id : 1
         * constantCode : Android-APP
         * constantName : Android-APP1.1
         * module : IM
         * createId : 0
         * createTime : 1566958827000
         * updateId : 0
         * updateTime : 1566958827000
         * url : http://....
         * remarks : testtest
         */

        private int id;
        private String constantCode;
        private String constantName;
        private String module;
        private int createId;
        private long createTime;
        private int updateId;
        private long updateTime;
        private String url;
        private String remarks;

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

        public String getConstantCode() {
            return constantCode;
        }

        public void setConstantCode(String constantCode) {
            this.constantCode = constantCode;
        }

        public String getConstantName() {
            return constantName;
        }

        public void setConstantName(String constantName) {
            this.constantName = constantName;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public int getCreateId() {
            return createId;
        }

        public void setCreateId(int createId) {
            this.createId = createId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getUpdateId() {
            return updateId;
        }

        public void setUpdateId(int updateId) {
            this.updateId = updateId;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
