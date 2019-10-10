package com.daoyu.chat.event;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateFRemarkEvent implements Parcelable {
    public String frmarks;
    public String nickName;

    public UpdateFRemarkEvent(String frmarks, String nickName) {
        this.frmarks = frmarks;
        this.nickName = nickName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.frmarks);
        dest.writeString(this.nickName);
    }

    protected UpdateFRemarkEvent(Parcel in) {
        this.frmarks = in.readString();
        this.nickName = in.readString();
    }

    public static final Creator<UpdateFRemarkEvent> CREATOR = new Creator<UpdateFRemarkEvent>() {
        @Override
        public UpdateFRemarkEvent createFromParcel(Parcel source) {
            return new UpdateFRemarkEvent(source);
        }

        @Override
        public UpdateFRemarkEvent[] newArray(int size) {
            return new UpdateFRemarkEvent[size];
        }
    };
}
