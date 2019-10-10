package com.daoyu.chat.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class ContactMobileBean extends BaseBean<ArrayList<ContactMobileBean.ContactMobileData>> {

    public static class ContactMobileData implements Parcelable, Comparable<ContactMobileData> {
        public String name;
        public String mobile;
        public String firstLetter;
        public String isFriend;
        public String isUser;
        public String userId;

        @Override
        public int compareTo(ContactMobileData o) {
            if (firstLetter.equals("#") && !o.firstLetter.equals("#")) {
                return 1;
            } else if (!firstLetter.equals("#") && o.firstLetter.equals("#")) {
                return -1;
            } else {
                return firstLetter.compareTo(o.firstLetter);
            }
        }

        public ContactMobileData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.mobile);
            dest.writeString(this.firstLetter);
            dest.writeString(this.isFriend);
            dest.writeString(this.isUser);
            dest.writeString(this.userId);
        }

        protected ContactMobileData(Parcel in) {
            this.name = in.readString();
            this.mobile = in.readString();
            this.firstLetter = in.readString();
            this.isFriend = in.readString();
            this.isUser = in.readString();
            this.userId = in.readString();
        }

        public static final Creator<ContactMobileData> CREATOR = new Creator<ContactMobileData>() {
            @Override
            public ContactMobileData createFromParcel(Parcel source) {
                return new ContactMobileData(source);
            }

            @Override
            public ContactMobileData[] newArray(int size) {
                return new ContactMobileData[size];
            }
        };
    }

}
