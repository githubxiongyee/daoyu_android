package com.daoyu.chat.module.home.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.daoyu.chat.base.BaseBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 附近的人加好友
 */
public class BaseBean2 implements Parcelable {

    /**
     * msg : 请求成功
     * code : 1
     * success : true
     */

    public String msg;
    public int code;
    public boolean success;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
        dest.writeInt(this.code);
        dest.writeByte(this.success ? (byte) 1 : (byte) 0);
    }

    public BaseBean2() {
    }

    protected BaseBean2(Parcel in) {
        this.msg = in.readString();
        this.code = in.readInt();
        this.success = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BaseBean2> CREATOR = new Parcelable.Creator<BaseBean2>() {
        @Override
        public BaseBean2 createFromParcel(Parcel source) {
            return new BaseBean2(source);
        }

        @Override
        public BaseBean2[] newArray(int size) {
            return new BaseBean2[size];
        }
    };
}
