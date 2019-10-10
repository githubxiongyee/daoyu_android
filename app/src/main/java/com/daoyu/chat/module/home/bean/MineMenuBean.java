package com.daoyu.chat.module.home.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MineMenuBean implements Parcelable {
    public int icons;
    public String menu;

    public MineMenuBean(int icons, String menu) {
        this.icons = icons;
        this.menu = menu;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.icons);
        dest.writeString(this.menu);
    }

    public MineMenuBean() {
    }

    protected MineMenuBean(Parcel in) {
        this.icons = in.readInt();
        this.menu = in.readString();
    }

    public static final Parcelable.Creator<MineMenuBean> CREATOR = new Parcelable.Creator<MineMenuBean>() {
        @Override
        public MineMenuBean createFromParcel(Parcel source) {
            return new MineMenuBean(source);
        }

        @Override
        public MineMenuBean[] newArray(int size) {
            return new MineMenuBean[size];
        }
    };
}
