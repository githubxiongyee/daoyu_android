package com.daoyu.chat.module.group.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class GroupRedpackageBean extends BaseBean<ArrayList<GroupRedpackageBean.GroupRedpackageData>> {

    public static class GroupRedpackageData implements android.os.Parcelable {
        public int id;
        public int uid;
        public int vipHongbao;
        public int vipStatus;
        public double vipCredit;
        public long createTime;
        public long updateTime;
        public int fid;
        public String remarks;
        public double namount;
        public int hbcount;
        public int ncount;
        public String pvid;
        public String source;
        public String balance;
        public String headeImag;
        public String name;


        public GroupRedpackageData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.uid);
            dest.writeInt(this.vipHongbao);
            dest.writeInt(this.vipStatus);
            dest.writeDouble(this.vipCredit);
            dest.writeLong(this.createTime);
            dest.writeLong(this.updateTime);
            dest.writeInt(this.fid);
            dest.writeString(this.remarks);
            dest.writeDouble(this.namount);
            dest.writeInt(this.hbcount);
            dest.writeInt(this.ncount);
            dest.writeString(this.pvid);
            dest.writeString(this.source);
            dest.writeString(this.balance);
            dest.writeString(this.headeImag);
            dest.writeString(this.name);
        }

        protected GroupRedpackageData(Parcel in) {
            this.id = in.readInt();
            this.uid = in.readInt();
            this.vipHongbao = in.readInt();
            this.vipStatus = in.readInt();
            this.vipCredit = in.readDouble();
            this.createTime = in.readLong();
            this.updateTime = in.readLong();
            this.fid = in.readInt();
            this.remarks = in.readString();
            this.namount = in.readDouble();
            this.hbcount = in.readInt();
            this.ncount = in.readInt();
            this.pvid = in.readString();
            this.source = in.readString();
            this.balance = in.readString();
            this.headeImag = in.readString();
            this.name = in.readString();
        }

        public static final Creator<GroupRedpackageData> CREATOR = new Creator<GroupRedpackageData>() {
            @Override
            public GroupRedpackageData createFromParcel(Parcel source) {
                return new GroupRedpackageData(source);
            }

            @Override
            public GroupRedpackageData[] newArray(int size) {
                return new GroupRedpackageData[size];
            }
        };
    }
}
