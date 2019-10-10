package com.daoyu.chat.module.im.module;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

public class ChatTable extends LitePalSupport implements Parcelable {
    public String chat_id;//当前聊天界面ID
    public String message_time;//消息时间戳
    public String user_id;//用户ID
    public String avatar;//用户头像 url
    public String username = "";//用户昵称
    public int message_type;//消息类型
    public boolean top;//是否置顶 默认false
    public boolean is_read;//消息是否已读
    public String last_message;//最后一条消息
    public String mobile;//手机号码
    public int number;//数量
    public String current_id;
    public boolean shield = false;
    public boolean chat_type = false;//聊天类型
    public boolean is_private = false;//是否是私人特助



    public ChatTable() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.chat_id);
        dest.writeString(this.message_time);
        dest.writeString(this.user_id);
        dest.writeString(this.avatar);
        dest.writeString(this.username);
        dest.writeInt(this.message_type);
        dest.writeByte(this.top ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_read ? (byte) 1 : (byte) 0);
        dest.writeString(this.last_message);
        dest.writeString(this.mobile);
        dest.writeInt(this.number);
        dest.writeString(this.current_id);
        dest.writeByte(this.shield ? (byte) 1 : (byte) 0);
        dest.writeByte(this.chat_type ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_private ? (byte) 1 : (byte) 0);
    }

    protected ChatTable(Parcel in) {
        this.chat_id = in.readString();
        this.message_time = in.readString();
        this.user_id = in.readString();
        this.avatar = in.readString();
        this.username = in.readString();
        this.message_type = in.readInt();
        this.top = in.readByte() != 0;
        this.is_read = in.readByte() != 0;
        this.last_message = in.readString();
        this.mobile = in.readString();
        this.number = in.readInt();
        this.current_id = in.readString();
        this.shield = in.readByte() != 0;
        this.chat_type = in.readByte() != 0;
        this.is_private = in.readByte() != 0;
    }

    public static final Creator<ChatTable> CREATOR = new Creator<ChatTable>() {
        @Override
        public ChatTable createFromParcel(Parcel source) {
            return new ChatTable(source);
        }

        @Override
        public ChatTable[] newArray(int size) {
            return new ChatTable[size];
        }
    };
}
