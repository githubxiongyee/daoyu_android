package com.daoyu.chat.module.im.module;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

public class ApplyFriendTable extends LitePalSupport implements Parcelable {
    public String username;//用户昵称
    public String avatar;//用户头像
    public String userid;//用户UID
    public String content;//手机号
    public int type;//UDP类型
    public String time;//时间戳(毫秒)
    public String status;//1:可添加 2:已添加 3:已过期
    public String self_uid;//当前用户信息
    public String isNearBy;


    public ApplyFriendTable() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.avatar);
        dest.writeString(this.userid);
        dest.writeString(this.content);
        dest.writeInt(this.type);
        dest.writeString(this.time);
        dest.writeString(this.status);
        dest.writeString(this.self_uid);
        dest.writeString(this.isNearBy);
    }

    protected ApplyFriendTable(Parcel in) {
        this.username = in.readString();
        this.avatar = in.readString();
        this.userid = in.readString();
        this.content = in.readString();
        this.type = in.readInt();
        this.time = in.readString();
        this.status = in.readString();
        this.self_uid = in.readString();
        this.isNearBy = in.readString();
    }

    public static final Creator<ApplyFriendTable> CREATOR = new Creator<ApplyFriendTable>() {
        @Override
        public ApplyFriendTable createFromParcel(Parcel source) {
            return new ApplyFriendTable(source);
        }

        @Override
        public ApplyFriendTable[] newArray(int size) {
            return new ApplyFriendTable[size];
        }
    };
}
