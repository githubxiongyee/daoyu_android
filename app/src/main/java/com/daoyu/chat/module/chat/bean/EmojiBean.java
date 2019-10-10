package com.daoyu.chat.module.chat.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class EmojiBean implements Parcelable {
    public String code;
    public String png;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.png);
    }

    public EmojiBean() {
    }

    protected EmojiBean(Parcel in) {
        this.code = in.readString();
        this.png = in.readString();
    }

    public static final Parcelable.Creator<EmojiBean> CREATOR = new Parcelable.Creator<EmojiBean>() {
        @Override
        public EmojiBean createFromParcel(Parcel source) {
            return new EmojiBean(source);
        }

        @Override
        public EmojiBean[] newArray(int size) {
            return new EmojiBean[size];
        }
    };
}
