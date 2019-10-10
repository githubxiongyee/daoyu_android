package com.daoyu.chat.module.chat.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MoreMenuBean implements Parcelable {
    public String menu;
    public int imgIds;

    public MoreMenuBean(String menu, int imgIds) {
        this.menu = menu;
        this.imgIds = imgIds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.menu);
        dest.writeInt(this.imgIds);
    }

    public MoreMenuBean() {
    }

    protected MoreMenuBean(Parcel in) {
        this.menu = in.readString();
        this.imgIds = in.readInt();
    }

    public static final Parcelable.Creator<MoreMenuBean> CREATOR = new Parcelable.Creator<MoreMenuBean>() {
        @Override
        public MoreMenuBean createFromParcel(Parcel source) {
            return new MoreMenuBean(source);
        }

        @Override
        public MoreMenuBean[] newArray(int size) {
            return new MoreMenuBean[size];
        }
    };
}
