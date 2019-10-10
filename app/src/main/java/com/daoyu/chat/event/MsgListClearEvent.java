package com.daoyu.chat.event;

import android.os.Parcel;
import android.os.Parcelable;

public class MsgListClearEvent implements Parcelable {
    public String groupId;


    public MsgListClearEvent(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupId);
    }

    protected MsgListClearEvent(Parcel in) {
        this.groupId = in.readString();
    }

    public static final Parcelable.Creator<MsgListClearEvent> CREATOR = new Parcelable.Creator<MsgListClearEvent>() {
        @Override
        public MsgListClearEvent createFromParcel(Parcel source) {
            return new MsgListClearEvent(source);
        }

        @Override
        public MsgListClearEvent[] newArray(int size) {
            return new MsgListClearEvent[size];
        }
    };
}
