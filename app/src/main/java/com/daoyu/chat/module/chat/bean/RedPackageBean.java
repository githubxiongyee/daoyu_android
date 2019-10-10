package com.daoyu.chat.module.chat.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class RedPackageBean extends BaseBean<RedPackageBean.RedPackageData> {

    public static class RedPackageData implements android.os.Parcelable {
        public int id;
        public int uid;
        public int vipHongbao;
        public int vipStatus;
        public double vipCredit;
        public long createTime;
        public long updateTime;
        public String fid;
        public String remarks;

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
            dest.writeString(this.fid);
            dest.writeString(this.remarks);
        }

        public RedPackageData() {
        }

        protected RedPackageData(Parcel in) {
            this.id = in.readInt();
            this.uid = in.readInt();
            this.vipHongbao = in.readInt();
            this.vipStatus = in.readInt();
            this.vipCredit = in.readDouble();
            this.createTime = in.readLong();
            this.updateTime = in.readLong();
            this.fid = in.readString();
            this.remarks = in.readString();
        }

        public static final Creator<RedPackageData> CREATOR = new Creator<RedPackageData>() {
            @Override
            public RedPackageData createFromParcel(Parcel source) {
                return new RedPackageData(source);
            }

            @Override
            public RedPackageData[] newArray(int size) {
                return new RedPackageData[size];
            }
        };
    }
}
