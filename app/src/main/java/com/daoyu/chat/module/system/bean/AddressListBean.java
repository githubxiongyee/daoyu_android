package com.daoyu.chat.module.system.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.module.mine.bean.ShippingAddressData;

import java.util.ArrayList;

public class AddressListBean extends BaseBean<AddressListBean.AddressListData> {
    public static class AddressListData implements android.os.Parcelable {
        public int code;
        public String msg;
        public int page;
        public int limit;
        public int count;
        public ArrayList<ShippingAddressData> data;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.code);
            dest.writeString(this.msg);
            dest.writeInt(this.page);
            dest.writeInt(this.limit);
            dest.writeInt(this.count);
            dest.writeTypedList(this.data);
        }

        public AddressListData() {
        }

        protected AddressListData(Parcel in) {
            this.code = in.readInt();
            this.msg = in.readString();
            this.page = in.readInt();
            this.limit = in.readInt();
            this.count = in.readInt();
            this.data = in.createTypedArrayList(ShippingAddressData.CREATOR);
        }

        public static final Creator<AddressListData> CREATOR = new Creator<AddressListData>() {
            @Override
            public AddressListData createFromParcel(Parcel source) {
                return new AddressListData(source);
            }

            @Override
            public AddressListData[] newArray(int size) {
                return new AddressListData[size];
            }
        };
    }

}
