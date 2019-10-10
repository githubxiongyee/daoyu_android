package com.daoyu.chat.event;

import android.os.Parcel;
import android.os.Parcelable;

public class EmojiEvent implements Parcelable {
    public String emoji;

    public EmojiEvent(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.emoji);
    }

    protected EmojiEvent(Parcel in) {
        this.emoji = in.readString();
    }

    public static final Parcelable.Creator<EmojiEvent> CREATOR = new Parcelable.Creator<EmojiEvent>() {
        @Override
        public EmojiEvent createFromParcel(Parcel source) {
            return new EmojiEvent(source);
        }

        @Override
        public EmojiEvent[] newArray(int size) {
            return new EmojiEvent[size];
        }
    };
}
