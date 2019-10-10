package com.daoyu.chat.module.im.module;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

public class MessageDetailTable extends LitePalSupport implements Parcelable {
    public String chat_id;//当前聊天界面ID
    public String message_time;//消息时间戳
    public String user_id;//用户ID
    public String username;//用户名
    public String avatar;//用户头像 url
    public String mobile;//手机号
    public int message_type;//消息类型
    public int chat_type;//聊天类型   1:单聊 2:群聊
    public int message_state;//消息状态
    public String message;//消息内容

    //群
    public String group_img;//群头像
    public String group_id;//群ID
    public String group_name;//群名称



    public int width;
    public int height;
    public String red_id;
    public String video_id;
    public String voice_id;

    public String card_image;
    public String card_name;

    public String location;



    public MessageDetailTable() {
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
        dest.writeString(this.username);
        dest.writeString(this.avatar);
        dest.writeString(this.mobile);
        dest.writeInt(this.message_type);
        dest.writeInt(this.chat_type);
        dest.writeInt(this.message_state);
        dest.writeString(this.message);
        dest.writeString(this.group_img);
        dest.writeString(this.group_id);
        dest.writeString(this.group_name);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.red_id);
        dest.writeString(this.video_id);
        dest.writeString(this.voice_id);
        dest.writeString(this.card_image);
        dest.writeString(this.card_name);
        dest.writeString(this.location);
    }

    protected MessageDetailTable(Parcel in) {
        this.chat_id = in.readString();
        this.message_time = in.readString();
        this.user_id = in.readString();
        this.username = in.readString();
        this.avatar = in.readString();
        this.mobile = in.readString();
        this.message_type = in.readInt();
        this.chat_type = in.readInt();
        this.message_state = in.readInt();
        this.message = in.readString();
        this.group_img = in.readString();
        this.group_id = in.readString();
        this.group_name = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.red_id = in.readString();
        this.video_id = in.readString();
        this.voice_id = in.readString();
        this.card_image = in.readString();
        this.card_name = in.readString();
        this.location = in.readString();
    }

    public static final Creator<MessageDetailTable> CREATOR = new Creator<MessageDetailTable>() {
        @Override
        public MessageDetailTable createFromParcel(Parcel source) {
            return new MessageDetailTable(source);
        }

        @Override
        public MessageDetailTable[] newArray(int size) {
            return new MessageDetailTable[size];
        }
    };
}
