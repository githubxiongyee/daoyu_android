package com.daoyu.chat.module.login.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class PrivateHelpBean extends BaseBean<PrivateHelpBean.PrivateHelpData> {

    public static class PrivateHelpData implements android.os.Parcelable {
        public int id;
        public long realName;
        public String nickName;
        public String headImg;
        public String sex;
        public String mobile;
        public String credential;
        public String birthday;
        public String email;
        public String delStatus;
        public String remarks;
        public String agentNo;
        public String addressX;
        public String addressY;
        public String addressD;
        public long registerTime;
        public long updateTime;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeLong(this.realName);
            dest.writeString(this.nickName);
            dest.writeString(this.headImg);
            dest.writeString(this.sex);
            dest.writeString(this.mobile);
            dest.writeString(this.credential);
            dest.writeString(this.birthday);
            dest.writeString(this.email);
            dest.writeString(this.delStatus);
            dest.writeString(this.remarks);
            dest.writeString(this.agentNo);
            dest.writeString(this.addressX);
            dest.writeString(this.addressY);
            dest.writeString(this.addressD);
            dest.writeLong(this.registerTime);
            dest.writeLong(this.updateTime);
        }

        public PrivateHelpData() {
        }

        protected PrivateHelpData(Parcel in) {
            this.id = in.readInt();
            this.realName = in.readLong();
            this.nickName = in.readString();
            this.headImg = in.readString();
            this.sex = in.readString();
            this.mobile = in.readString();
            this.credential = in.readString();
            this.birthday = in.readString();
            this.email = in.readString();
            this.delStatus = in.readString();
            this.remarks = in.readString();
            this.agentNo = in.readString();
            this.addressX = in.readString();
            this.addressY = in.readString();
            this.addressD = in.readString();
            this.registerTime = in.readLong();
            this.updateTime = in.readLong();
        }

        public static final Creator<PrivateHelpData> CREATOR = new Creator<PrivateHelpData>() {
            @Override
            public PrivateHelpData createFromParcel(Parcel source) {
                return new PrivateHelpData(source);
            }

            @Override
            public PrivateHelpData[] newArray(int size) {
                return new PrivateHelpData[size];
            }
        };
    }
}
