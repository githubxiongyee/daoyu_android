package com.daoyu.chat.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.daoyu.chat.module.home.bean.RedBagAdsBean;

public class RedBagUpdateStatusEvent implements Parcelable {
    public int position;
    public RedBagAdsBean.RedBagData redBagData;

    public RedBagUpdateStatusEvent() {
    }

    public RedBagUpdateStatusEvent(int position, RedBagAdsBean.RedBagData redBagData) {
        this.position = position;
        this.redBagData = redBagData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
    }

    protected RedBagUpdateStatusEvent(Parcel in) {
        this.position = in.readInt();
    }

    public static final Creator<RedBagUpdateStatusEvent> CREATOR = new Creator<RedBagUpdateStatusEvent>() {
        @Override
        public RedBagUpdateStatusEvent createFromParcel(Parcel source) {
            return new RedBagUpdateStatusEvent(source);
        }

        @Override
        public RedBagUpdateStatusEvent[] newArray(int size) {
            return new RedBagUpdateStatusEvent[size];
        }
    };
}
