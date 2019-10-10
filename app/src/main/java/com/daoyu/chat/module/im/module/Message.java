package com.daoyu.chat.module.im.module;

import android.os.Parcel;
import android.os.Parcelable;

public class Message  implements Parcelable {
    public String userid;
    public String userImage;//发送人用户头像
    public String time;//发送时间
    public String content;//message内容
    public int chattype;//1单聊  2群聊
    public String username;//发送用户名
    public int type;//消息类型 1是文字消息

    public Message(String userid, String userImage, String time, String content, int chattype, String username, int type) {
        this.userid = userid;
        this.userImage = userImage;
        this.time = time;
        this.content = content;
        this.chattype = chattype;
        this.username = username;
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userid);
        dest.writeString(this.userImage);
        dest.writeString(this.time);
        dest.writeString(this.content);
        dest.writeInt(this.chattype);
        dest.writeString(this.username);
        dest.writeInt(this.type);
    }

    protected Message(Parcel in) {
        this.userid = in.readString();
        this.userImage = in.readString();
        this.time = in.readString();
        this.content = in.readString();
        this.chattype = in.readInt();
        this.username = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
