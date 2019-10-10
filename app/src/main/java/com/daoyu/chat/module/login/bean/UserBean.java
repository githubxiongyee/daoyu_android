package com.daoyu.chat.module.login.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class UserBean extends BaseBean<UserBean.UserData> {
    public static class UserData implements android.os.Parcelable {
        public int uid;
        public String token;
        public String nickName;
        public String headImg;
        public String sex;
        public String userPhone;
        public String remarks;
        public String agentNo;
        public String addressD;
        public int type;
        public double creditTotal;
        public String qrval;
        public String paySign;
        public String area = "";
        public String payQRVal;//收款码
        public String invateCode;//邀请码
        public String private_id;


        public UserData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.uid);
            dest.writeString(this.token);
            dest.writeString(this.nickName);
            dest.writeString(this.headImg);
            dest.writeString(this.sex);
            dest.writeString(this.userPhone);
            dest.writeString(this.remarks);
            dest.writeString(this.agentNo);
            dest.writeString(this.addressD);
            dest.writeInt(this.type);
            dest.writeDouble(this.creditTotal);
            dest.writeString(this.qrval);
            dest.writeString(this.paySign);
            dest.writeString(this.area);
            dest.writeString(this.payQRVal);
            dest.writeString(this.invateCode);
            dest.writeString(this.private_id);
        }

        protected UserData(Parcel in) {
            this.uid = in.readInt();
            this.token = in.readString();
            this.nickName = in.readString();
            this.headImg = in.readString();
            this.sex = in.readString();
            this.userPhone = in.readString();
            this.remarks = in.readString();
            this.agentNo = in.readString();
            this.addressD = in.readString();
            this.type = in.readInt();
            this.creditTotal = in.readDouble();
            this.qrval = in.readString();
            this.paySign = in.readString();
            this.area = in.readString();
            this.payQRVal = in.readString();
            this.invateCode = in.readString();
            this.private_id = in.readString();
        }

        public static final Creator<UserData> CREATOR = new Creator<UserData>() {
            @Override
            public UserData createFromParcel(Parcel source) {
                return new UserData(source);
            }

            @Override
            public UserData[] newArray(int size) {
                return new UserData[size];
            }
        };
    }
}
