package com.daoyu.chat.event;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuClickEvent implements Parcelable {
    public String menu;

    public MenuClickEvent(String menu) {
        this.menu = menu;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.menu);
    }

    public MenuClickEvent() {
    }

    protected MenuClickEvent(Parcel in) {
        this.menu = in.readString();
    }

    public static final Parcelable.Creator<MenuClickEvent> CREATOR = new Parcelable.Creator<MenuClickEvent>() {
        @Override
        public MenuClickEvent createFromParcel(Parcel source) {
            return new MenuClickEvent(source);
        }

        @Override
        public MenuClickEvent[] newArray(int size) {
            return new MenuClickEvent[size];
        }
    };
}
