package com.daoyu.chat.module.home.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class ContactFriendBean extends BaseBean<ArrayList<ContactFriendBean.ContactFriendData>> {
    public static class ContactFriendData implements Parcelable, Comparable<ContactFriendData> {
        public int id;
        public String friendMobile;
        public String friendNickName;
        public int userFriendId;
        public String firstLetter;
        public String isAI;
        public String blacklist;
        public String sex;
        public String head_img;
        public String headImg;
        public String agent_no;
        public String address_x;
        public String address_y;
        public String remarks;
        public String address_d;
        public String ftype;
        public boolean checked;
        public String fremarks;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ContactFriendData that = (ContactFriendData) o;

            if (id != that.id) return false;
            if (userFriendId != that.userFriendId) return false;
            if (checked != that.checked) return false;
            if (!friendMobile.equals(that.friendMobile)) return false;
            if (!friendNickName.equals(that.friendNickName)) return false;
            if (!firstLetter.equals(that.firstLetter)) return false;
            if (!isAI.equals(that.isAI)) return false;
            if (!blacklist.equals(that.blacklist)) return false;
            if (!sex.equals(that.sex)) return false;
            if (!head_img.equals(that.head_img)) return false;
            if (!headImg.equals(that.headImg)) return false;
            if (!agent_no.equals(that.agent_no)) return false;
            if (!address_x.equals(that.address_x)) return false;
            if (!address_y.equals(that.address_y)) return false;
            if (!remarks.equals(that.remarks)) return false;
            if (!address_d.equals(that.address_d)) return false;
            if (!ftype.equals(that.ftype)) return false;
            return fremarks.equals(that.fremarks);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + friendMobile.hashCode();
            result = 31 * result + friendNickName.hashCode();
            result = 31 * result + userFriendId;
            result = 31 * result + firstLetter.hashCode();
            result = 31 * result + isAI.hashCode();
            result = 31 * result + blacklist.hashCode();
            result = 31 * result + sex.hashCode();
            result = 31 * result + head_img.hashCode();
            result = 31 * result + headImg.hashCode();
            result = 31 * result + agent_no.hashCode();
            result = 31 * result + address_x.hashCode();
            result = 31 * result + address_y.hashCode();
            result = 31 * result + remarks.hashCode();
            result = 31 * result + address_d.hashCode();
            result = 31 * result + ftype.hashCode();
            result = 31 * result + (checked ? 1 : 0);
            result = 31 * result + fremarks.hashCode();
            return result;
        }

        public ContactFriendData() {
        }

        @Override
        public int compareTo(ContactFriendData o) {
            return this.firstLetter.charAt(0) - o.firstLetter.charAt(0);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.friendMobile);
            dest.writeString(this.friendNickName);
            dest.writeInt(this.userFriendId);
            dest.writeString(this.firstLetter);
            dest.writeString(this.isAI);
            dest.writeString(this.blacklist);
            dest.writeString(this.sex);
            dest.writeString(this.head_img);
            dest.writeString(this.headImg);
            dest.writeString(this.agent_no);
            dest.writeString(this.address_x);
            dest.writeString(this.address_y);
            dest.writeString(this.remarks);
            dest.writeString(this.address_d);
            dest.writeString(this.ftype);
            dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
            dest.writeString(this.fremarks);
        }

        protected ContactFriendData(Parcel in) {
            this.id = in.readInt();
            this.friendMobile = in.readString();
            this.friendNickName = in.readString();
            this.userFriendId = in.readInt();
            this.firstLetter = in.readString();
            this.isAI = in.readString();
            this.blacklist = in.readString();
            this.sex = in.readString();
            this.head_img = in.readString();
            this.headImg = in.readString();
            this.agent_no = in.readString();
            this.address_x = in.readString();
            this.address_y = in.readString();
            this.remarks = in.readString();
            this.address_d = in.readString();
            this.ftype = in.readString();
            this.checked = in.readByte() != 0;
            this.fremarks = in.readString();
        }

        public static final Creator<ContactFriendData> CREATOR = new Creator<ContactFriendData>() {
            @Override
            public ContactFriendData createFromParcel(Parcel source) {
                return new ContactFriendData(source);
            }

            @Override
            public ContactFriendData[] newArray(int size) {
                return new ContactFriendData[size];
            }
        };
    }
}
