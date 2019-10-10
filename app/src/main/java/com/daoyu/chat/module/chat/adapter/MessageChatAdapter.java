package com.daoyu.chat.module.chat.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.chat.bean.EmojiBean;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.DensityUtil;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.view.CircleImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ONESELF_LAYOUT_TEXT = 0;
    public static final int ONESELF_LAYOUT_IMAGE = 2;
    public static final int ONESELF_LAYOUT_VOICE = 4;
    public static final int FRIEND_LAYOUT_TEXT = 1;
    public static final int FRIEND_LAYOUT_IMAGE = 3;
    public static final int FRIEND_LAYOUT_VOICE = 5;
    public static final int ONESELF_LAYOUT_REDPAPER = 6;
    public static final int FRIEND_LAYOUT_REDPAPER = 7;
    public static final int RECEIVE_PACKAGE_LAYOUT = 8;//红包结果
    public static final int ONESElF_LAYOUT_CARD = 9;
    public static final int FRIEND_LAYOUT_CARD = 10;
    public static final int ONESELF_LAYOUT_LOCATION = 11;
    public static final int FRIEND_LAYOUT_LOCATION = 12;
    private Context context;
    private List<MessageDetailTable> messages;
    private String uid;
    private long topShowTime;
    private int receivedId;
    private int sendId;

    public void setPopBackGround(int sendId, int receivedId) {
        this.sendId = sendId;
        this.receivedId = receivedId;
        notifyDataSetChanged();
    }

    public MessageChatAdapter(Context context, List<MessageDetailTable> messages) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        this.context = context;
        this.messages = messages;
        uid = String.valueOf(userInfoData.uid);
        int pop = SharedPreferenceUtil.getInstance().getInt(Constant.POP);
        switch (pop) {
            case 0:
                sendId = R.drawable.chat_bg_0_left;
                receivedId = R.drawable.chat_bg_0_right;
                break;
            case 2:
                sendId = R.drawable.chat_bg_1_left;
                receivedId = R.drawable.chat_bg_1_right;
                break;
            case 1:
                sendId = R.drawable.chat_bg_2_left;
                receivedId = R.drawable.chat_bg_2_right;
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageDetailTable message = messages.get(position);
        int messageType = message.message_type;
        if (messageType == IMConstant.MessageType.RECEIVE_PACKAGE) {
            return RECEIVE_PACKAGE_LAYOUT;
        } else {
            if (uid.equals(message.user_id)) {
                if (messageType == IMConstant.MessageType.TEXT) {
                    return ONESELF_LAYOUT_TEXT;
                } else if (messageType == IMConstant.MessageType.IMAGE) {
                    return ONESELF_LAYOUT_IMAGE;
                } else if (messageType == IMConstant.MessageType.VOICE) {
                    return ONESELF_LAYOUT_VOICE;
                } else if (messageType == IMConstant.MessageType.REDPAPER) {
                    return ONESELF_LAYOUT_REDPAPER;
                } else if (messageType == IMConstant.MessageType.CARD) {
                    return ONESElF_LAYOUT_CARD;
                } else if (messageType == IMConstant.MessageType.LOCATION) {
                    return ONESELF_LAYOUT_LOCATION;
                }

            } else {
                if (messageType == IMConstant.MessageType.TEXT) {
                    return FRIEND_LAYOUT_TEXT;
                } else if (messageType == IMConstant.MessageType.IMAGE) {
                    return FRIEND_LAYOUT_IMAGE;
                } else if (messageType == IMConstant.MessageType.VOICE) {
                    return FRIEND_LAYOUT_VOICE;
                } else if (messageType == IMConstant.MessageType.REDPAPER) {
                    return FRIEND_LAYOUT_REDPAPER;
                } else if (messageType == IMConstant.MessageType.CARD) {
                    return FRIEND_LAYOUT_CARD;
                } else if (messageType == IMConstant.MessageType.LOCATION) {
                    return FRIEND_LAYOUT_LOCATION;
                }
            }
        }

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ONESELF_LAYOUT_TEXT) {
            return new OneselfTextViewHolder(LayoutInflater.from(context).inflate(R.layout.row_sent_message, viewGroup, false));
        } else if (i == FRIEND_LAYOUT_TEXT) {
            return new FriendTextViewHolder(LayoutInflater.from(context).inflate(R.layout.row_received_message, viewGroup, false));
        } else if (i == ONESELF_LAYOUT_IMAGE) {
            return new OneselfImageViewHolder(LayoutInflater.from(context).inflate(R.layout.row_sent_picture, viewGroup, false));
        } else if (i == FRIEND_LAYOUT_IMAGE) {
            return new FriendImageViewHolder(LayoutInflater.from(context).inflate(R.layout.row_received_picture, viewGroup, false));
        } else if (i == ONESELF_LAYOUT_REDPAPER) {
            return new OneselfRedPackageViewHolder(LayoutInflater.from(context).inflate(R.layout.row_send_red_package, viewGroup, false));
        } else if (i == FRIEND_LAYOUT_REDPAPER) {
            return new FriendRedPackageViewHolder(LayoutInflater.from(context).inflate(R.layout.row_received_red_package, viewGroup, false));
        } else if (i == RECEIVE_PACKAGE_LAYOUT) {
            return new ReceivePackageViewHolder(LayoutInflater.from(context).inflate(R.layout.row_receive_package_result, viewGroup, false));
        } else if (i == ONESElF_LAYOUT_CARD) {
            return new OneselfCardViewHolder(LayoutInflater.from(context).inflate(R.layout.row_sent_card, viewGroup, false));
        } else if (i == FRIEND_LAYOUT_CARD) {
            return new FriendCardViewHolder(LayoutInflater.from(context).inflate(R.layout.row_received_card, viewGroup, false));
        } else if (i == ONESELF_LAYOUT_LOCATION) {
            return new OneselfLocationViewHolder(LayoutInflater.from(context).inflate(R.layout.row_sent_location, viewGroup, false));
        } else if (i == FRIEND_LAYOUT_LOCATION) {
            return new FriendLocationViewHolder(LayoutInflater.from(context).inflate(R.layout.row_received_location, viewGroup, false));
        } else if (i == ONESELF_LAYOUT_VOICE) {
            return new OneselfVoiceViewHolder(LayoutInflater.from(context).inflate(R.layout.row_send_voice, viewGroup, false));
        } else if (i == FRIEND_LAYOUT_VOICE) {
            return new FriendVoiceViewHolder(LayoutInflater.from(context).inflate(R.layout.row_received_voice, viewGroup, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        MessageDetailTable message = messages.get(i);
        topShowTime = Long.parseLong(messages.get(0).message_time);
        if (holder instanceof OneselfTextViewHolder) {
            oneselfText((OneselfTextViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof FriendTextViewHolder) {
            friendText((FriendTextViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof OneselfImageViewHolder) {
            oneselfImage((OneselfImageViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof FriendImageViewHolder) {
            friendImage((FriendImageViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof OneselfRedPackageViewHolder) {
            oneselfRedPackage((OneselfRedPackageViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof FriendRedPackageViewHolder) {
            friendRedPackage((FriendRedPackageViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof ReceivePackageViewHolder) {
            receivePackage((ReceivePackageViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof OneselfCardViewHolder) {
            oneselfCard((OneselfCardViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof FriendCardViewHolder) {
            friendCard((FriendCardViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof OneselfLocationViewHolder) {
            oneselfLocation((OneselfLocationViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof FriendLocationViewHolder) {
            friendLocation((FriendLocationViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof OneselfVoiceViewHolder) {
            oneselfVoice((OneselfVoiceViewHolder) holder, i, message, topShowTime);
        } else if (holder instanceof FriendVoiceViewHolder) {
            friendVoice((FriendVoiceViewHolder) holder, i, message, topShowTime);
        }
    }


    private void oneselfText(@NonNull OneselfTextViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        OneselfTextViewHolder oneselfTextViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, oneselfTextViewHolder.ivUserhead);

        SpannableString emotionContent = getEmotionContent(context, oneselfTextViewHolder.tvChatcontent, message.message);
        oneselfTextViewHolder.tvChatcontent.setText(emotionContent);

        oneselfTextViewHolder.tvChatcontent.setBackgroundResource(sendId);

        int messageState = message.message_state;
        switch (messageState) {
            case IMConstant.MessageStatus.DELIVERING:
                oneselfTextViewHolder.pbSending.setVisibility(View.VISIBLE);
                oneselfTextViewHolder.msgStatus.setVisibility(View.GONE);
                break;
            case IMConstant.MessageStatus.FAILED:
                oneselfTextViewHolder.pbSending.setVisibility(View.GONE);
                oneselfTextViewHolder.msgStatus.setVisibility(View.VISIBLE);
                break;
            case IMConstant.MessageStatus.SUCCESSED:
                oneselfTextViewHolder.pbSending.setVisibility(View.GONE);
                oneselfTextViewHolder.msgStatus.setVisibility(View.GONE);
                break;
        }
        if (i == 0) {
            oneselfTextViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            oneselfTextViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                oneselfTextViewHolder.timestamp.setVisibility(View.VISIBLE);
                oneselfTextViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                oneselfTextViewHolder.timestamp.setVisibility(View.GONE);
            }
        }
        oneselfTextViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
    }

    private void friendText(@NonNull FriendTextViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        FriendTextViewHolder friendTextViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar, R.drawable.my_user_default, R.drawable.my_user_default, friendTextViewHolder.ivUserhead);
        friendTextViewHolder.tvChatcontent.setText(message.message);
        friendTextViewHolder.tvChatcontent.setBackgroundResource(receivedId);
        SpannableString emotionContent = getEmotionContent(context, friendTextViewHolder.tvChatcontent, message.message);
        friendTextViewHolder.tvChatcontent.setText(emotionContent);

        String time = message.message_time;
        if (i == 0) {
            friendTextViewHolder.timestamp.setVisibility(View.VISIBLE);
            friendTextViewHolder.timestamp.setVisibility(View.VISIBLE);
            friendTextViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.parseLong(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                friendTextViewHolder.timestamp.setVisibility(View.VISIBLE);
                friendTextViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                friendTextViewHolder.timestamp.setVisibility(View.GONE);
            }
        }
        friendTextViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
    }

    private void oneselfImage(@NonNull OneselfImageViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        OneselfImageViewHolder oneselfImageViewHolder = holder;

        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, oneselfImageViewHolder.ivUserhead);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) oneselfImageViewHolder.ivSendPicture.getLayoutParams();
        int widthHalf = DensityUtil.getScreenWidth() / 3;
        int width = message.width;
        int height = message.height;
        int widthParams = 0;
        int heightParams = 0;
        int ratio = 1;
        if (widthHalf > width) {
            ratio = widthHalf / width;
            widthParams = width;
            heightParams = height;
        } else {
            ratio = width / widthHalf;
            widthParams = widthHalf;
            heightParams = height / ratio;
        }
        params.width = widthParams;
        params.height = heightParams;
        oneselfImageViewHolder.ivSendPicture.setLayoutParams(params);
        ImageUtils.setRoundCornerImageView(context, message.message+"?imageMogr2/quality/60", oneselfImageViewHolder.ivSendPicture);
        oneselfImageViewHolder.ivSendPicture.setBackgroundResource(sendId);
        int messageState = message.message_state;
        switch (messageState) {
            case IMConstant.MessageStatus.DELIVERING:
                oneselfImageViewHolder.progressBar.setVisibility(View.VISIBLE);
                oneselfImageViewHolder.msgStatus.setVisibility(View.GONE);
                break;
            case IMConstant.MessageStatus.FAILED:
                oneselfImageViewHolder.progressBar.setVisibility(View.GONE);
                oneselfImageViewHolder.msgStatus.setVisibility(View.VISIBLE);
                break;
            case IMConstant.MessageStatus.SUCCESSED:
                oneselfImageViewHolder.progressBar.setVisibility(View.GONE);
                oneselfImageViewHolder.msgStatus.setVisibility(View.GONE);
                break;
        }
        if (i == 0) {
            oneselfImageViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            oneselfImageViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                oneselfImageViewHolder.timestamp.setVisibility(View.VISIBLE);
                oneselfImageViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                oneselfImageViewHolder.timestamp.setVisibility(View.GONE);
            }
        }

        oneselfImageViewHolder.ivSendPicture.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onImageClickListener(message, i);
        });
        oneselfImageViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
    }

    private void friendImage(@NonNull FriendImageViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        FriendImageViewHolder friendImageViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, friendImageViewHolder.ivUserhead);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) friendImageViewHolder.ivSendPicture.getLayoutParams();
        int widthHalf = DensityUtil.getScreenWidth() / 3;
        int width = message.width;
        int height = message.height;
        int widthParams = 0;
        int heightParams = 0;
        int ratio = 1;
        if (widthHalf > width) {
            ratio = widthHalf / width;
            widthParams = width;
            heightParams = height;
        } else {
            ratio = width / widthHalf;
            widthParams = widthHalf;
            heightParams = height / ratio;
        }
        params.width = widthParams;
        params.height = heightParams;
        friendImageViewHolder.ivSendPicture.setLayoutParams(params);
        ImageUtils.setRoundCornerImageView(context, message.message+"?imageMogr2/quality/60", friendImageViewHolder.ivSendPicture);
        friendImageViewHolder.ivSendPicture.setBackgroundResource(receivedId);
        if (i == 0) {
            friendImageViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            friendImageViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                friendImageViewHolder.timestamp.setVisibility(View.VISIBLE);
                friendImageViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                friendImageViewHolder.timestamp.setVisibility(View.GONE);
            }
        }
        friendImageViewHolder.ivSendPicture.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onImageClickListener(message, i);
        });
        friendImageViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
    }

    private void oneselfRedPackage(@NonNull OneselfRedPackageViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        OneselfRedPackageViewHolder oneselfRedPackageViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, oneselfRedPackageViewHolder.ivUserhead);
        oneselfRedPackageViewHolder.tvGreetings.setText(message.message);

        if (i == 0) {
            oneselfRedPackageViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            oneselfRedPackageViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                oneselfRedPackageViewHolder.timestamp.setVisibility(View.VISIBLE);
                oneselfRedPackageViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                oneselfRedPackageViewHolder.timestamp.setVisibility(View.GONE);
            }
        }
        int messageState = message.message_state;
        if (messageState == IMConstant.MessageStatus.DELIVERING) {
            oneselfRedPackageViewHolder.clRedPackage.setAlpha(1f);
            oneselfRedPackageViewHolder.tvReceive.setVisibility(View.GONE);
        } else {
            oneselfRedPackageViewHolder.clRedPackage.setAlpha(0.5f);
            oneselfRedPackageViewHolder.tvReceive.setVisibility(View.VISIBLE);
        }
        oneselfRedPackageViewHolder.clRedPackage.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onRedPackageClickListener(message, i);
        });

        oneselfRedPackageViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });

    }

    private void friendRedPackage(@NonNull FriendRedPackageViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        FriendRedPackageViewHolder friendRedPackageViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, friendRedPackageViewHolder.ivUserhead);
        friendRedPackageViewHolder.tvGreetings.setText(message.message);

        if (i == 0) {
            friendRedPackageViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            friendRedPackageViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                friendRedPackageViewHolder.timestamp.setVisibility(View.VISIBLE);
                friendRedPackageViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                friendRedPackageViewHolder.timestamp.setVisibility(View.GONE);
            }
        }
        int messageState = message.message_state;
        if (messageState == IMConstant.MessageStatus.DELIVERING) {
            friendRedPackageViewHolder.clRedPackage.setAlpha(1f);
            friendRedPackageViewHolder.tvReceive.setVisibility(View.GONE);
        } else {
            friendRedPackageViewHolder.clRedPackage.setAlpha(0.5f);
            friendRedPackageViewHolder.tvReceive.setVisibility(View.VISIBLE);
        }
        friendRedPackageViewHolder.clRedPackage.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onRedPackageClickListener(message, i);
        });
        friendRedPackageViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
    }

    private void oneselfCard(@NonNull OneselfCardViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        OneselfCardViewHolder oneselfCardViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, oneselfCardViewHolder.ivUserhead);
        ImageUtils.setRoundCornerImageView(context, message.card_image, R.drawable.my_user_default, oneselfCardViewHolder.ivCardHeader);
        oneselfCardViewHolder.tvUserName.setText(message.card_name);
        oneselfCardViewHolder.clCard.setBackgroundResource(sendId);
        if (i == 0) {
            oneselfCardViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            oneselfCardViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                oneselfCardViewHolder.timestamp.setVisibility(View.VISIBLE);
                oneselfCardViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                oneselfCardViewHolder.timestamp.setVisibility(View.GONE);
            }
        }

        oneselfCardViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });

        oneselfCardViewHolder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onCardViewClickListener(message, i);
        });

    }

    private void friendCard(@NonNull FriendCardViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        FriendCardViewHolder friendCardViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, friendCardViewHolder.ivUserhead);

        ImageUtils.setRoundCornerImageView(context, message.card_image, R.drawable.my_user_default, friendCardViewHolder.ivCardHeader);
        friendCardViewHolder.tvUserName.setText(message.card_name);
        friendCardViewHolder.clCard.setBackgroundResource(receivedId);
        if (i == 0) {
            friendCardViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            friendCardViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                friendCardViewHolder.timestamp.setVisibility(View.VISIBLE);
                friendCardViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                friendCardViewHolder.timestamp.setVisibility(View.GONE);
            }
        }

        friendCardViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
        friendCardViewHolder.itemView.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onCardViewClickListener(message, i);
        });
    }

    private void oneselfLocation(@NonNull OneselfLocationViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        OneselfLocationViewHolder oneselfLocationViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, oneselfLocationViewHolder.ivUserhead);
        String addressMapUrl = "http://restapi.amap.com/v3/staticmap?location=" + message.location + "&zoom=18&size=600*400&markers=mid,0xff0000,A:" + message.location + "&key=1a32c0167e8db25ecacb5bec6ccbef74";
        ImageUtils.setNormalImage(context, addressMapUrl, oneselfLocationViewHolder.ivAddress);
        oneselfLocationViewHolder.tvAddressName.setText(message.message);
        oneselfLocationViewHolder.rlLocation.setBackgroundResource(sendId);
        if (i == 0) {
            oneselfLocationViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            oneselfLocationViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                oneselfLocationViewHolder.timestamp.setVisibility(View.VISIBLE);
                oneselfLocationViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                oneselfLocationViewHolder.timestamp.setVisibility(View.GONE);
            }
        }

        oneselfLocationViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
        oneselfLocationViewHolder.rlLocation.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onLocationClickListener(message, i);
        });

    }

    private void friendLocation(@NonNull FriendLocationViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        FriendLocationViewHolder friendLocationViewHolder = holder;
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, friendLocationViewHolder.ivUserhead);
        String addressMapUrl = "http://restapi.amap.com/v3/staticmap?location=" + message.location + "&zoom=18&size=600*400&markers=mid,0xff0000,A:" + message.location + "&key=1a32c0167e8db25ecacb5bec6ccbef74";
        ImageUtils.setNormalImage(context, addressMapUrl, friendLocationViewHolder.ivAddress);
        friendLocationViewHolder.tvAddressName.setText(message.message);
        friendLocationViewHolder.rlLocation.setBackgroundResource(receivedId);
        if (i == 0) {
            friendLocationViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            friendLocationViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                friendLocationViewHolder.timestamp.setVisibility(View.VISIBLE);
                friendLocationViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                friendLocationViewHolder.timestamp.setVisibility(View.GONE);
            }
        }

        friendLocationViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
        friendLocationViewHolder.rlLocation.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onLocationClickListener(message, i);
        });
    }


    private void oneselfVoice(@NonNull OneselfVoiceViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        OneselfVoiceViewHolder oneselfVoiceViewHolder = holder;
        oneselfVoiceViewHolder.idRecordTime.setText(String.format("%.0fS", Double.parseDouble(message.voice_id)));
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, oneselfVoiceViewHolder.ivUserhead);
        oneselfVoiceViewHolder.idRecorderLength.setBackgroundResource(sendId);
        if (i == 0) {
            oneselfVoiceViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            topShowTime = Long.parseLong(time);
            oneselfVoiceViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                oneselfVoiceViewHolder.timestamp.setVisibility(View.VISIBLE);
                oneselfVoiceViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                oneselfVoiceViewHolder.timestamp.setVisibility(View.GONE);
            }
        }

        oneselfVoiceViewHolder.idRecorderLength.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onVoiceClickListener(message, i, oneselfVoiceViewHolder.idRecorderAmin);
        });

        oneselfVoiceViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
    }

    private void friendVoice(@NonNull FriendVoiceViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        FriendVoiceViewHolder friendVoiceViewHolder = holder;
        friendVoiceViewHolder.idRecordTime.setText(String.format("%.0fS", Double.parseDouble(message.voice_id)));
        ImageUtils.setNormalImage(context, message.avatar+"?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, friendVoiceViewHolder.ivUserhead);
        friendVoiceViewHolder.idRecorderLength.setBackgroundResource(receivedId);
        if (i == 0) {
            friendVoiceViewHolder.timestamp.setVisibility(View.VISIBLE);
            String time = message.message_time;
            topShowTime = Long.parseLong(time);
            friendVoiceViewHolder.timestamp.setText(ToolsUtil.formatTime(Long.valueOf(time), "MM-dd HH:mm"));
        } else {
            long messageTime = Long.parseLong(message.message_time);
            long interval = messageTime - topShowTime;
            if (interval > 3 * 60 * 1000) {
                topShowTime = messageTime;
                friendVoiceViewHolder.timestamp.setVisibility(View.VISIBLE);
                friendVoiceViewHolder.timestamp.setText(ToolsUtil.formatTime(messageTime, "MM-dd HH:mm"));
            } else {
                friendVoiceViewHolder.timestamp.setVisibility(View.GONE);
            }
        }
        friendVoiceViewHolder.ivUserhead.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onHeaderViewClickListener(message, i);
        });
        friendVoiceViewHolder.idRecorderLength.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onVoiceClickListener(message, i, friendVoiceViewHolder.idRecorderAmin);
        });
    }


    private void receivePackage(@NonNull ReceivePackageViewHolder holder, int i, MessageDetailTable message, long topShowTime) {
        holder.tvRedSult.setText(message.message);
    }


    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    static class OneselfTextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.tv_chatcontent)
        TextView tvChatcontent;
        @BindView(R.id.msg_status)
        ImageView msgStatus;
        @BindView(R.id.tv_ack)
        TextView tvAck;
        @BindView(R.id.tv_delivered)
        TextView tvDelivered;
        @BindView(R.id.pb_sending)
        ProgressBar pbSending;

        public OneselfTextViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OneselfImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.iv_sendPicture)
        ImageView ivSendPicture;
        @BindView(R.id.rl_picture)
        RelativeLayout rlPicture;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.ll_loading)
        LinearLayout llLoading;
        @BindView(R.id.msg_status)
        ImageView msgStatus;

        public OneselfImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OneselfRedPackageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.iv_red_package)
        ImageView ivRedPackage;
        @BindView(R.id.tv_greetings)
        TextView tvGreetings;
        @BindView(R.id.tv_receive)
        TextView tvReceive;
        @BindView(R.id.cl_red_package)
        ConstraintLayout clRedPackage;

        public OneselfRedPackageViewHolder(@NonNull View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OneselfCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.iv_card_header)
        ImageView ivCardHeader;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.cl_card)
        ConstraintLayout clCard;

        public OneselfCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FriendTextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.tv_userid)
        TextView tvUserid;
        @BindView(R.id.tv_chatcontent)
        TextView tvChatcontent;

        public FriendTextViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FriendImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.iv_sendPicture)
        ImageView ivSendPicture;

        public FriendImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FriendRedPackageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.iv_red_package)
        ImageView ivRedPackage;
        @BindView(R.id.tv_greetings)
        TextView tvGreetings;
        @BindView(R.id.tv_receive)
        TextView tvReceive;
        @BindView(R.id.cl_red_package)
        ConstraintLayout clRedPackage;

        public FriendRedPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FriendCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.iv_card_header)
        ImageView ivCardHeader;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.cl_card)
        ConstraintLayout clCard;

        public FriendCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ReceivePackageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_red_sult)
        TextView tvRedSult;

        public ReceivePackageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OneselfLocationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.tv_address_name)
        TextView tvAddressName;
        @BindView(R.id.iv_address)
        ImageView ivAddress;
        @BindView(R.id.rl_location)
        RelativeLayout rlLocation;

        public OneselfLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FriendLocationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.tv_address_name)
        TextView tvAddressName;
        @BindView(R.id.iv_address)
        ImageView ivAddress;
        @BindView(R.id.rl_location)
        RelativeLayout rlLocation;

        public FriendLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    static class OneselfVoiceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.id_recorder_amin)
        View idRecorderAmin;
        @BindView(R.id.id_recorder_length)
        FrameLayout idRecorderLength;
        @BindView(R.id.id_record_time)
        TextView idRecordTime;

        public OneselfVoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FriendVoiceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView timestamp;
        @BindView(R.id.iv_userhead)
        CircleImageView ivUserhead;
        @BindView(R.id.id_recorder_amin)
        View idRecorderAmin;
        @BindView(R.id.id_recorder_length)
        FrameLayout idRecorderLength;
        @BindView(R.id.id_record_time)
        TextView idRecordTime;

        public FriendVoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onTextClickListener(MessageDetailTable message, int position);

        void onImageClickListener(MessageDetailTable message, int position);

        void onVoiceClickListener(MessageDetailTable message, int position, View view);

        void onRedPackageClickListener(MessageDetailTable message, int position);

        void onHeaderViewClickListener(MessageDetailTable message, int position);

        void onCardViewClickListener(MessageDetailTable message, int position);

        void onLocationClickListener(MessageDetailTable message, int position);
    }

    public SpannableString getEmotionContent(final Context context, final TextView tv, String source) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();
        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);
        while (matcherEmotion.find()) {
            String key = matcherEmotion.group();
            int start = matcherEmotion.start();
            Integer imgRes = getResourceByReflect(getIDS(key));
            if (imgRes != -1) {
                int size = (int) tv.getTextSize();
                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    public int getResourceByReflect(String imageName) {
        Class drawable = R.drawable.class;
        Field field = null;
        int r_id;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id = -1;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }

    private String getIDS(String key) {
        String emojiImage = null;
        String emoji = ToolsUtil.getJson(context, "emoji.json");
        ArrayList<EmojiBean> emojis = new Gson().fromJson(emoji, new TypeToken<ArrayList<EmojiBean>>() {
        }.getType());
        for (int i = 0; i < emojis.size(); i++) {
            EmojiBean emojiBean = emojis.get(i);
            if (key.equals(emojiBean.code)) {
                emojiImage = emojiBean.png.split("\\.")[0];
                return emojiImage;
            }
        }
        return emojiImage;
    }
}
