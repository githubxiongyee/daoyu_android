package com.daoyu.chat.module.home.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class OffLineMessageBean extends BaseBean<ArrayList<OffLineMessageBean.OffLineMessageData>> {
    public static class OffLineMessageData implements android.os.Parcelable {
        public String user_id;
        public String user_friend_id;
        public String typeu;
        public String fingerPrint;
        public String dataContent;
        public int flag;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.user_id);
            dest.writeString(this.user_friend_id);
            dest.writeString(this.typeu);
            dest.writeString(this.fingerPrint);
            dest.writeString(this.dataContent);
            dest.writeInt(this.flag);
        }

        public OffLineMessageData() {
        }

        protected OffLineMessageData(Parcel in) {
            this.user_id = in.readString();
            this.user_friend_id = in.readString();
            this.typeu = in.readString();
            this.fingerPrint = in.readString();
            this.dataContent = in.readString();
            this.flag = in.readInt();
        }

        public static final Creator<OffLineMessageData> CREATOR = new Creator<OffLineMessageData>() {
            @Override
            public OffLineMessageData createFromParcel(Parcel source) {
                return new OffLineMessageData(source);
            }

            @Override
            public OffLineMessageData[] newArray(int size) {
                return new OffLineMessageData[size];
            }
        };
    }
}
