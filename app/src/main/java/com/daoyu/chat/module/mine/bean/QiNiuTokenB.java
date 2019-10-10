package com.daoyu.chat.module.mine.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QiNiuTokenB {

    /**
     * msg : 请求成功
     * code : 1
     * data : fP6KfIAAIJG0ZpEQQGxwbfu6TZyF_r2mCt5GHRUb:dHhKLcp83zrXBAYhm87PFnN-jQ0=:eyJpbnNlcnRPbmx5IjoxLCJzY29wZSI6InByb2R1Y3RzLWltYWdlIiwiZGVhZGxpbmUiOjE1NjQ4NTUwMjd9
     * success : true
     */

    private String msg;
    private int code;
    private String data;
    private boolean success;

    public static QiNiuTokenB objectFromData(String str) {

        return new Gson().fromJson(str, QiNiuTokenB.class);
    }

    public static QiNiuTokenB objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), QiNiuTokenB.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<QiNiuTokenB> arrayQiNiuTokenBFromData(String str) {

        Type listType = new TypeToken<ArrayList<QiNiuTokenB>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<QiNiuTokenB> arrayQiNiuTokenBFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<QiNiuTokenB>>() {
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
