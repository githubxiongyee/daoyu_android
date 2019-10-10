package com.daoyu.chat.module.group.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class UsersHeaderBean extends BaseBean<ArrayList<UsersHeaderBean.UsersHeaderData>> {
    public static class UsersHeaderData implements android.os.Parcelable, Comparable<UsersHeaderData> {
        public String userId;
        public String headImg;
        public String nickName;
        public int localHeader;
        public int type = 0;
        public String firstLetter;
        public boolean checked;

        public UsersHeaderData(String nickName, int localHeader, int type) {
            this.nickName = nickName;
            this.localHeader = localHeader;
            this.type = type;
        }

        public UsersHeaderData() {
        }

        @Override
        public int compareTo(UsersHeaderData o) {
            return this.firstLetter.charAt(0)-o.firstLetter.charAt(0);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.userId);
            dest.writeString(this.headImg);
            dest.writeString(this.nickName);
            dest.writeInt(this.localHeader);
            dest.writeInt(this.type);
            dest.writeString(this.firstLetter);
            dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        }

        protected UsersHeaderData(Parcel in) {
            this.userId = in.readString();
            this.headImg = in.readString();
            this.nickName = in.readString();
            this.localHeader = in.readInt();
            this.type = in.readInt();
            this.firstLetter = in.readString();
            this.checked = in.readByte() != 0;
        }

        public static final Creator<UsersHeaderData> CREATOR = new Creator<UsersHeaderData>() {
            @Override
            public UsersHeaderData createFromParcel(Parcel source) {
                return new UsersHeaderData(source);
            }

            @Override
            public UsersHeaderData[] newArray(int size) {
                return new UsersHeaderData[size];
            }
        };
    }
}
