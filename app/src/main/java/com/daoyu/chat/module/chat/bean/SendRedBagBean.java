package com.daoyu.chat.module.chat.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class SendRedBagBean extends BaseBean<SendRedBagBean.SendRedBagData> {
    public static class SendRedBagData implements android.os.Parcelable {
        public int id;
        public int uid;
        public int vipHongbao;
        public int vipStatus;
        public double vipCredit;
        public long createTime;
        public long updateTime;
        public int fid;
        public String remarks;
        public String balance;

        public SendRedBagData() {
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
            dest.writeString(this.balance);
        }

        protected SendRedBagData(Parcel in) {
            this.id = in.readInt();
            this.uid = in.readInt();
            this.vipHongbao = in.readInt();
            this.vipStatus = in.readInt();
            this.vipCredit = in.readDouble();
            this.createTime = in.readLong();
            this.updateTime = in.readLong();
            this.fid = in.readInt();
            this.remarks = in.readString();
            this.balance = in.readString();
        }

        public static final Creator<SendRedBagData> CREATOR = new Creator<SendRedBagData>() {
            @Override
            public SendRedBagData createFromParcel(Parcel source) {
                return new SendRedBagData(source);
            }

            @Override
            public SendRedBagData[] newArray(int size) {
                return new SendRedBagData[size];
            }
        };
    }
}
