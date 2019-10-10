package com.daoyu.chat.module.group.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class GroupInfoBean extends BaseBean<GroupInfoBean.GroupInfoData> {

    public static class GroupInfoData implements android.os.Parcelable,Comparable<GroupInfoData> {
        public String group_id;
        public String adminid;
        public String groupname;
        public String groupnotice;
        public String groupurl;
        public int flag;
        public IdData _id;
        public String firstLetter;
        public ArrayList<String> users;
        public ArrayList<String> usersnickname;

        @Override
        public int compareTo(GroupInfoData o) {
            return this.firstLetter.charAt(0) - o.firstLetter.charAt(0);
        }


        public static class IdData implements android.os.Parcelable {
            public int timestamp;
            public int counter;
            public long time;
            public String date;
            public int processIdentifier;
            public int machineIdentifier;
            public int timeSecond;

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.timestamp);
                dest.writeInt(this.counter);
                dest.writeLong(this.time);
                dest.writeString(this.date);
                dest.writeInt(this.processIdentifier);
                dest.writeInt(this.machineIdentifier);
                dest.writeInt(this.timeSecond);
            }

            public IdData() {
            }

            protected IdData(Parcel in) {
                this.timestamp = in.readInt();
                this.counter = in.readInt();
                this.time = in.readLong();
                this.date = in.readString();
                this.processIdentifier = in.readInt();
                this.machineIdentifier = in.readInt();
                this.timeSecond = in.readInt();
            }

            public static final Creator<IdData> CREATOR = new Creator<IdData>() {
                @Override
                public IdData createFromParcel(Parcel source) {
                    return new IdData(source);
                }

                @Override
                public IdData[] newArray(int size) {
                    return new IdData[size];
                }
            };
        }

        public GroupInfoData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.group_id);
            dest.writeString(this.adminid);
            dest.writeString(this.groupname);
            dest.writeString(this.groupnotice);
            dest.writeString(this.groupurl);
            dest.writeInt(this.flag);
            dest.writeParcelable(this._id, flags);
            dest.writeString(this.firstLetter);
            dest.writeStringList(this.users);
            dest.writeStringList(this.usersnickname);
        }

        protected GroupInfoData(Parcel in) {
            this.group_id = in.readString();
            this.adminid = in.readString();
            this.groupname = in.readString();
            this.groupnotice = in.readString();
            this.groupurl = in.readString();
            this.flag = in.readInt();
            this._id = in.readParcelable(IdData.class.getClassLoader());
            this.firstLetter = in.readString();
            this.users = in.createStringArrayList();
            this.usersnickname = in.createStringArrayList();
        }

        public static final Creator<GroupInfoData> CREATOR = new Creator<GroupInfoData>() {
            @Override
            public GroupInfoData createFromParcel(Parcel source) {
                return new GroupInfoData(source);
            }

            @Override
            public GroupInfoData[] newArray(int size) {
                return new GroupInfoData[size];
            }
        };
    }
}
