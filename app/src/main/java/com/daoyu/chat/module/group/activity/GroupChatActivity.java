package com.daoyu.chat.module.group.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.ClosePageEvent;
import com.daoyu.chat.event.EmojiEvent;
import com.daoyu.chat.event.MenuClickEvent;
import com.daoyu.chat.event.MessageTotalEvent;
import com.daoyu.chat.event.ReceiveMessageEvent;
import com.daoyu.chat.module.chat.activity.ChatBigImageActivity;
import com.daoyu.chat.module.chat.activity.DemolitionRedPackageResultActivity;
import com.daoyu.chat.module.chat.activity.LocationDetailsActivity;
import com.daoyu.chat.module.chat.activity.SendLocationActivity;
import com.daoyu.chat.module.chat.adapter.FragAdapter;
import com.daoyu.chat.module.chat.adapter.MessageChatAdapter;
import com.daoyu.chat.module.chat.dialog.ReceiveRedPackageDialog;
import com.daoyu.chat.module.chat.fragment.DefaultFirstMoreFragment;
import com.daoyu.chat.module.chat.fragment.DefaultSecondMoreFragment;
import com.daoyu.chat.module.chat.fragment.Emoji1Fragment;
import com.daoyu.chat.module.chat.fragment.Emoji2Fragment;
import com.daoyu.chat.module.chat.fragment.Emoji3Fragment;
import com.daoyu.chat.module.chat.fragment.Emoji4Fragment;
import com.daoyu.chat.module.chat.manager.MediaManager;
import com.daoyu.chat.module.chat.view.AudioRecorderButton;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.module.group.bean.GroupRedpackageBean;
import com.daoyu.chat.module.group.dialog.GroupReceiveRedPackageDialog;
import com.daoyu.chat.module.home.activity.ContactDetailsActivity;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.service.MqttService;
import com.daoyu.chat.utils.QiNiuUtil;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.ViewPagerIndicator;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class GroupChatActivity extends BaseTitleActivity implements
        MessageChatAdapter.OnItemClickListener, ReceiveRedPackageDialog.OnReceivePackageListener,GroupReceiveRedPackageDialog.OnNotReceivePackageListener {
    @BindView(R.id.rv_chat_content)
    RecyclerView rvChatContent;
    @BindView(R.id.iv_input)
    ImageView ivInput;
    @BindView(R.id.iv_voice_special)
    ImageView ivVoiceSpecial;
    @BindView(R.id.ed_chat_message_special)
    EditText edChatMessageSpecial;
    @BindView(R.id.tv_voice_special)
    AudioRecorderButton tvVoiceSpecial;
    @BindView(R.id.iv_expression_special)
    ImageView ivExpressionSpecial;
    @BindView(R.id.iv_more_special)
    ImageView ivMoreSpecial;
    @BindView(R.id.tv_send_special)
    TextView tvSendSpecial;
    @BindView(R.id.ll_send_content_special)
    LinearLayout llSendContentSpecial;
    @BindView(R.id.view_pager_more)
    ViewPager viewPagerMore;
    @BindView(R.id.view_pager_indicator)
    ViewPagerIndicator viewPagerIndicator;
    @BindView(R.id.ll_more_default)
    RelativeLayout llMoreDefault;
    @BindView(R.id.view_pager_emoji)
    ViewPager viewPagerEmoji;
    @BindView(R.id.indeicator_emoji)
    ViewPagerIndicator indeicatorEmoji;
    @BindView(R.id.iv_add_more)
    ImageView ivAddMore;
    @BindView(R.id.iv_emoji)
    ImageView ivEmoji;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.ll_emoji)
    LinearLayout llEmoji;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    private InputMethodManager manager;
    private ArrayList<MessageDetailTable> messages;
    private MessageChatAdapter adapter;

    private String groupName;
    private String groupHeader;
    private String groupGid;

    private MessageDetailTable sendMessage;
    private UserBean.UserData userInfoData;
    private String sendType;
    private List<Fragment> defaultFragments;
    private List<Fragment> emojiFragments;
    private DefaultFirstMoreFragment firstMoreFragment;
    private DefaultSecondMoreFragment secondMoreFragment;
    private FragAdapter viewPagerAdapter;

    private ReceiveRedPackageDialog receiveRedPackageDialog;
    private FragAdapter emojiAdapter;

    private boolean isVoiceSpace = true;
    private View currentView;
    private MessageDetailTable sendVoice;
    private View currentViewVoice;
    private GroupInfoBean.GroupInfoData groupInfoData;
    private GroupReceiveRedPackageDialog mGroupReceiveRedPackageDialog;
    private double vipCredit;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_group_chat;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    @Override
    protected void initEvent() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        showMenu();
        Intent intent = getIntent();
        groupGid = intent.getStringExtra(Constant.FRIEND_UID);
        if (TextUtils.isEmpty(groupGid)) {
            finish();
            return;
        }
        Log.d("TAG", " group ID:" + groupGid);

        rvChatContent.setVisibility(View.VISIBLE);
        llSendContentSpecial.setVisibility(View.VISIBLE);
        ivInput.setVisibility(View.GONE);

        defaultFragments = new ArrayList<>();
        firstMoreFragment = new DefaultFirstMoreFragment();
        secondMoreFragment = new DefaultSecondMoreFragment();
        defaultFragments.add(firstMoreFragment);
        defaultFragments.add(secondMoreFragment);
        viewPagerAdapter = new FragAdapter(getSupportFragmentManager(), defaultFragments);
        viewPagerMore.setAdapter(viewPagerAdapter);
        viewPagerIndicator.setViewPager(viewPagerMore);
        viewPagerIndicator.setNum(2);

        emojiFragments = new ArrayList<>();
        Emoji1Fragment emoji1Fragment = new Emoji1Fragment();
        Emoji2Fragment emoji2Fragment = new Emoji2Fragment();
        Emoji3Fragment emoji3Fragment = new Emoji3Fragment();
        Emoji4Fragment emoji4Fragment = new Emoji4Fragment();
        emojiFragments.add(emoji1Fragment);
        emojiFragments.add(emoji2Fragment);
        emojiFragments.add(emoji3Fragment);
        emojiFragments.add(emoji4Fragment);
        emojiAdapter = new FragAdapter(getSupportFragmentManager(), emojiFragments);
        viewPagerEmoji.setAdapter(emojiAdapter);
        indeicatorEmoji.setViewPager(viewPagerEmoji);
        indeicatorEmoji.setNum(4);

        initListener();

        rvChatContent.setLayoutManager(new LinearLayoutManager(this));
        messages = new ArrayList<>();
        adapter = new MessageChatAdapter(this, messages);
        adapter.setListener(this);
        rvChatContent.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
        setChatBackgroundImage();
        setPopground();
        requestGroupInfo();

        LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupGid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            if (list != null || list.size() > 0) {
                messages.addAll(list);
            }
            if (adapter == null || rvChatContent == null) return;
            adapter.notifyDataSetChanged();
            rvChatContent.scrollToPosition(adapter.getItemCount() - 1);
        });

    }

    /**
     * 设置聊天背景
     */
    private void setChatBackgroundImage() {
        int anInt = SharedPreferenceUtil.getInstance().getInt(Constant.FACE, 0);
        switch (anInt) {
            case 0:
                rootLayout.setBackgroundResource(R.color.color_F2F2F2);
                break;
            case 1:
                rootLayout.setBackgroundResource(R.drawable.my_skin_bubble_background_bg_one);
                break;
            case 2:
                rootLayout.setBackgroundResource(R.drawable.my_skin_bubble_background_bg_two);
                break;
        }
    }

    /**
     * 设置pop背景
     */
    private void setPopground() {
        if (adapter != null) {
            int pop = SharedPreferenceUtil.getInstance().getInt(Constant.POP);
            int sendId = -1;
            int receivedId = -1;
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
            if (sendId == -1 || receivedId == -1) return;
            adapter.setPopBackGround(sendId, receivedId);
        }
    }


    private void requestGroupInfo() {
        showLoading("加载中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupGid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_GROUT_INFO, params, this, new JsonCallback<GroupInfoBean>(GroupInfoBean.class) {
            @Override
            public void onSuccess(Response<GroupInfoBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                GroupInfoBean body = response.body();
                if (body.code == 1) {
                    groupInfoData = body.data;
                    if (groupInfoData == null) return;
                    groupName = groupInfoData.groupname;
                    setCurrentTitle(groupName);
                    groupHeader = groupInfoData.groupurl;
                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<GroupInfoBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        }, UrlConfig.BASE_GROUP_URL);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        rvChatContent.setOnTouchListener((v, event) -> {
            llMoreDefault.setVisibility(View.GONE);
            llEmoji.setVisibility(View.GONE);
            return false;
        });

        llRoot.setOnTouchListener((v, event) -> {
            llMoreDefault.setVisibility(View.GONE);
            llEmoji.setVisibility(View.GONE);
            return false;
        });

        ivInput.setOnClickListener(this);
        ivVoiceSpecial.setOnClickListener(this);
        ivExpressionSpecial.setOnClickListener(this);
        ivMoreSpecial.setOnClickListener(this);
        tvSendSpecial.setOnClickListener(this);

        edChatMessageSpecial.setOnTouchListener((v, event) -> {
            llMoreDefault.setVisibility(View.GONE);
            llEmoji.setVisibility(View.GONE);
            return false;
        });
        edChatMessageSpecial.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String message = s.toString();
                if (TextUtils.isEmpty(message)) {
                    tvSendSpecial.setVisibility(View.GONE);
                    ivMoreSpecial.setVisibility(View.VISIBLE);
                } else {
                    tvSendSpecial.setVisibility(View.VISIBLE);
                    ivMoreSpecial.setVisibility(View.GONE);
                }
            }
        });
        tvVoiceSpecial.setOnAudioFinishRecorderListener((seconds, filePath) -> QiNiuUtil.request(new File(filePath), System.currentTimeMillis() + "", (url, status) -> {
            if (status) {
                Log.d("TAG", url);
                sendVoice(url, seconds);
                new File(filePath).delete();
            }
        }, ".amr"));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            List<ChatTable> chatTables = LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupGid).find(ChatTable.class);
            if (chatTables != null && chatTables.size() > 0) {
                ChatTable chatTable = chatTables.get(0);
                chatTable.number = 0;
                EventBus.getDefault().post(new MessageTotalEvent());
                chatTable.saveAsync().listen(success -> Log.d("TAG", "保存成功"));

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                List<ChatTable> chatTables = LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupGid).find(ChatTable.class);
                if (chatTables != null && chatTables.size() > 0) {
                    ChatTable chatTable = chatTables.get(0);
                    chatTable.number = 0;
                    EventBus.getDefault().post(new MessageTotalEvent());
                    chatTable.saveAsync().listen(success -> Log.d("TAG", "保存成功"));
                    finish();
                } else {
                    finish();
                }
                break;
            case R.id.iv_menu://顶部菜单
                llEmoji.setVisibility(View.GONE);
                Intent intent = new Intent(this, GroupChatSettingActivity.class);
                intent.putExtra(Constant.CONTACT_GROUP_INFO, groupInfoData);
                startActivity(intent);
                break;

            case R.id.iv_show_input://切换到私人特助输入文字页面
                llEmoji.setVisibility(View.GONE);

                llSendContentSpecial.setVisibility(View.VISIBLE);

                break;
            case R.id.iv_input://切换到私人特助默认
                llEmoji.setVisibility(View.GONE);

                llSendContentSpecial.setVisibility(View.GONE);
                break;
            case R.id.iv_voice_special://语音
                llEmoji.setVisibility(View.GONE);

                if (isVoiceSpace) {
                    sendType = "voiceSpecial";
                    requestPermission(Permission.Group.STORAGE, Permission.Group.MICROPHONE);
                } else {
                    ivVoiceSpecial.setImageResource(R.drawable.home_icon_chat_speak);
                    edChatMessageSpecial.setVisibility(View.VISIBLE);
                    tvVoiceSpecial.setVisibility(View.GONE);
                    isVoiceSpace = true;
                }

                break;
            case R.id.iv_expression_special://表情
                edChatMessageSpecial.setVisibility(View.VISIBLE);
                tvVoiceSpecial.setVisibility(View.GONE);
                llMoreDefault.setVisibility(View.GONE);

                if (llEmoji.getVisibility() == View.VISIBLE) {
                    llEmoji.setVisibility(View.GONE);
                } else {
                    llEmoji.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_more_special://更多菜单
                llMoreDefault.setVisibility(View.GONE);
                llEmoji.setVisibility(View.GONE);
                if (llMoreDefault.getVisibility() == View.VISIBLE) {
                    llMoreDefault.setVisibility(View.GONE);
                } else {
                    llMoreDefault.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.tv_send_special:
                llMoreDefault.setVisibility(View.GONE);
                llEmoji.setVisibility(View.GONE);
                String messageSpecial = edChatMessageSpecial.getText().toString().trim();
                if (TextUtils.isEmpty(messageSpecial)) {
                    toast.toastShow("不能发送空白内容");
                    edChatMessageSpecial.setText("");
                    return;
                }
                sendTextMessage(messageSpecial);
                hideKeyboard();
                edChatMessageSpecial.setText("");
                break;


        }

    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceviceMessageEvent(ReceiveMessageEvent event) {
        if (event != null) {
            String userId = event.userId;
            if (TextUtils.isEmpty(userId) || !userId.equals(String.valueOf(groupGid))) {
                return;
            }
            LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupGid).findAsync(MessageDetailTable.class).listen(list -> {
                messages.clear();
                messages.addAll(list);
                adapter.notifyDataSetChanged();
                rvChatContent.scrollToPosition(adapter.getItemCount() - 1);
            });

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEmojiEvent(EmojiEvent event) {
        if (event != null) {
            String emoji = event.emoji;
            String s = edChatMessageSpecial.getText().toString();
            if (TextUtils.isEmpty(s)) {
                s = emoji;
            } else {
                s = s + emoji;
            }
            edChatMessageSpecial.setText(s);
            edChatMessageSpecial.setSelection(s.length());

        }

    }

    /**
     * 底部菜单按钮响应事件回调
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMenuClickEvent(MenuClickEvent event) {
        if (event == null) return;
        llMoreDefault.setVisibility(View.GONE);
        String menu = event.menu;
        switch (menu) {
            case "拍照":
                llMoreDefault.setVisibility(View.GONE);
                sendType = "camera";
                requestPermission(Permission.Group.CAMERA, Permission.Group.STORAGE);
                break;
            case "相册":
                llMoreDefault.setVisibility(View.GONE);
                sendType = "album";
                requestPermission(Permission.Group.STORAGE);
                break;
            case "红包":
                llMoreDefault.setVisibility(View.GONE);
                if (groupInfoData == null) return;
                Intent intent = new Intent(this, SendGroupRedBagActivity.class);
                intent.putExtra(Constant.CONTACT_GROUP_INFO, groupInfoData);
                startActivity(intent);
                break;
            case "转账":
                break;
            case "语音":
                break;
            case "视频":
                break;
            case "位置":
                sendType = "location";
                requestPermission(Permission.ACCESS_COARSE_LOCATION);
                break;
            case "文件":
                break;
            case "名片":
                sendCard();
                break;
            case "收藏":
                break;
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClosePageEvent(ClosePageEvent event) {
        if (event != null) {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = selectList.get(0);
                    String compressPath = localMedia.getCompressPath();
                    if (TextUtils.isEmpty(compressPath)) return;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(compressPath, options);
                    QiNiuUtil.request(new File(compressPath), String.valueOf(System.currentTimeMillis()), (url, status) -> {
                        if (status) {
                            sendImage(url, options.outWidth, options.outHeight);
                        }
                    }, ".jpg");
                    break;
            }
        }
    }

    @Override
    public void onPermissionGrant(List<String> permissions) {
        if (TextUtils.isEmpty(sendType)) return;
        switch (sendType) {
            case "album":
                onAlbum();
                break;
            case "camera":
                onCamera();
                break;
            case "location":
                Intent intentMap = new Intent(this, SendLocationActivity.class);
                intentMap.putExtra(Constant.CONTACT_FRIEND_ID, groupGid);
                intentMap.putExtra(Constant.CONTACT_NAME, groupName);
                intentMap.putExtra(Constant.FRIEND_HEADER, groupHeader);
                intentMap.putExtra(Constant.IS_GROUP, true);
                intentMap.putExtra(Constant.CONTACT_GROUP_INFO, groupInfoData);
                startActivity(intentMap);
                break;
            case "storage":
                if (sendVoice == null || currentViewVoice == null) return;
                if (sendVoice.user_id.equals(String.valueOf(userInfoData.uid))) {
                    if (currentView != null) {
                        currentView.setBackgroundResource(R.mipmap.adj);
                        currentView = null;
                    }
                    currentView = currentViewVoice;
                    currentView.setBackgroundResource(R.drawable.play_anim);
                    AnimationDrawable anim = (AnimationDrawable) currentView.getBackground();
                    anim.start();
                    MediaManager.playSound(sendVoice.message, mp -> currentView.setBackgroundResource(R.mipmap.adj));
                } else {
                    if (currentView != null) {
                        currentView.setBackgroundResource(R.mipmap.voice_seleted_left);
                        currentView = null;
                    }
                    currentView = currentViewVoice;
                    currentView.setBackgroundResource(R.drawable.play_anim_left);
                    AnimationDrawable anim = (AnimationDrawable) currentView.getBackground();
                    anim.start();
                    MediaManager.playSound(sendVoice.message, mp -> currentView.setBackgroundResource(R.mipmap.voice_seleted_left));
                }
                break;
            case "voiceSpecial":
                ivVoiceSpecial.setImageResource(R.drawable.home_icon_chat_keyboard);
                edChatMessageSpecial.setVisibility(View.GONE);
                tvVoiceSpecial.setVisibility(View.VISIBLE);
                isVoiceSpace = false;
                break;
        }

    }


    /**
     * 发送名片
     */
    private void sendCard() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("userid", uid);
        params.put("userImage", headImg);
        params.put("time", time);
        params.put("content", uid);
        params.put("chattype", 2);
        params.put("username", userInfoData.nickName);
        params.put("type", IMConstant.MessageType.CARD);
        params.put("card", userInfoData.nickName);
        params.put("cardImage", headImg);

        params.put("groupimg", groupInfoData.groupurl);
        params.put("groupname", groupInfoData.groupname);
        params.put("groupid", groupGid);

        sendMessage = new MessageDetailTable();
        String chatId = userInfoData.uid + "GL" + groupGid;
        sendMessage.chat_id = chatId;

        sendMessage.message_type = IMConstant.MessageType.CARD;
        sendMessage.message_state = IMConstant.MessageStatus.DELIVERING;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 2;
        sendMessage.message_time = time;
        sendMessage.message = uid;
        sendMessage.card_image = headImg;
        sendMessage.card_name = userInfoData.nickName;
        sendMessage.group_name = groupInfoData.groupname;
        sendMessage.group_img = groupInfoData.groupurl;
        sendMessage.group_id = groupGid;
        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupGid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            messages.addAll(list);
            if (adapter == null || rvChatContent == null) return;
            adapter.notifyDataSetChanged();
            rvChatContent.scrollToPosition(adapter.getItemCount() - 1);
        }));

        List<ChatTable> chatTables = LitePal.where("chat_id = ?", sendMessage.chat_id).find(ChatTable.class);

        if (chatTables == null || chatTables.size() <= 0) {
            ChatTable chatTable = new ChatTable();
            chatTable.mobile = userInfoData.userPhone;
            chatTable.chat_id = sendMessage.chat_id;
            chatTable.number = 0;
            chatTable.is_read = true;
            chatTable.last_message = "[名片]";
            chatTable.avatar = groupHeader;
            chatTable.user_id = String.valueOf(groupGid);
            chatTable.username = groupName;
            chatTable.top = false;
            chatTable.chat_type = true;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_type = IMConstant.MessageType.CARD;
            chatTable.message_time = time;
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "保存成功!");
                }
            });
        } else {
            ChatTable chatTable = chatTables.get(0);
            chatTable.is_read = true;
            chatTable.last_message = "[名片]";
            chatTable.number = 0;
            chatTable.message_time = time;
            chatTable.username = groupName;
            chatTable.avatar = groupHeader;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));
        }

        MqttService.publish(new Gson().toJson(params), groupGid);

    }


    /**
     * 发送语音消息
     */
    private void sendVoice(String voiceUrl, float seconds) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("userid", uid);
        params.put("userImage", headImg);
        params.put("time", time);
        params.put("content", voiceUrl);
        params.put("chattype", 2);
        params.put("username", userInfoData.nickName);
        params.put("voiceDuration", String.format("%.0f", seconds));
        params.put("type", IMConstant.MessageType.VOICE);
        params.put("voiceName", System.currentTimeMillis());
        params.put("groupimg", groupInfoData.groupurl);
        params.put("groupname", groupInfoData.groupname);
        params.put("groupid", groupGid);


        sendMessage = new MessageDetailTable();
        sendMessage.chat_id = userInfoData.uid + "GL" + groupGid;
        sendMessage.message_type = IMConstant.MessageType.VOICE;
        sendMessage.message_state = IMConstant.MessageStatus.DELIVERING;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 2;
        sendMessage.message_time = time;
        sendMessage.message = voiceUrl;
        sendMessage.voice_id = String.valueOf(seconds);
        sendMessage.group_name = groupInfoData.groupname;
        sendMessage.group_img = groupInfoData.groupurl;
        sendMessage.group_id = groupGid;

        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupGid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            messages.addAll(list);
            if (adapter == null || rvChatContent == null) return;
            adapter.notifyDataSetChanged();
            rvChatContent.scrollToPosition(adapter.getItemCount() - 1);
        }));

        List<ChatTable> chatTables = LitePal.where("chat_id = ?", sendMessage.chat_id).find(ChatTable.class);

        if (chatTables == null || chatTables.size() <= 0) {
            ChatTable chatTable = new ChatTable();
            chatTable.mobile = userInfoData.userPhone;
            chatTable.chat_id = sendMessage.chat_id;
            chatTable.number = 0;
            chatTable.is_read = true;
            chatTable.last_message = "[语音消息]";
            chatTable.avatar = groupHeader;
            chatTable.user_id = String.valueOf(groupGid);
            chatTable.username = groupName;
            chatTable.top = false;
            chatTable.chat_type = true;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_type = IMConstant.MessageType.VOICE;
            chatTable.message_time = time;
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "保存成功!");
                }
            });
        } else {
            ChatTable chatTable = chatTables.get(0);
            chatTable.is_read = true;
            chatTable.number = 0;
            chatTable.last_message = "[语音消息]";
            chatTable.message_time = time;
            chatTable.username = groupName;
            chatTable.avatar = groupHeader;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));
        }

        MqttService.publish(new Gson().toJson(params), groupGid);
    }


    /**
     * 发送图片消息
     *
     * @param imageUrl
     * @param width
     * @param height
     */
    private void sendImage(String imageUrl, int width, int height) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("userid", uid);
        params.put("userImage", headImg);
        params.put("time", time);
        params.put("content", imageUrl);
        params.put("chattype", 2);
        params.put("username", TextUtils.isEmpty(userInfoData.nickName) ? userInfoData.userPhone : userInfoData.nickName);
        params.put("imageWidth", width);
        params.put("imageHeight", height);
        params.put("type", IMConstant.MessageType.IMAGE);
        params.put("imageName", System.currentTimeMillis() + ".jpg");
        params.put("groupimg", groupInfoData.groupurl);
        params.put("groupname", groupInfoData.groupname);
        params.put("groupid", groupGid);


        sendMessage = new MessageDetailTable();
        sendMessage.chat_id = userInfoData.uid + "GL" + groupGid;
        sendMessage.message_type = IMConstant.MessageType.IMAGE;
        sendMessage.message_state = IMConstant.MessageStatus.SUCCESSED;
        sendMessage.avatar = headImg;
        sendMessage.width = width;
        sendMessage.height = height;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 2;
        sendMessage.message_time = time;
        sendMessage.message = imageUrl;
        sendMessage.group_name = groupInfoData.groupname;
        sendMessage.group_img = groupInfoData.groupurl;
        sendMessage.group_id = groupGid;


        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupGid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            messages.addAll(list);
            if (adapter == null || rvChatContent == null) return;
            adapter.notifyDataSetChanged();
            rvChatContent.scrollToPosition(adapter.getItemCount() - 1);
        }));

        List<ChatTable> chatTables = LitePal.where("chat_id = ?", sendMessage.chat_id).find(ChatTable.class);

        if (chatTables == null || chatTables.size() <= 0) {
            ChatTable chatTable = new ChatTable();
            chatTable.mobile = userInfoData.userPhone;
            chatTable.chat_id = sendMessage.chat_id;
            chatTable.number = 0;
            chatTable.is_read = true;
            chatTable.last_message = "[图片消息]";
            chatTable.avatar = groupHeader;
            chatTable.user_id = String.valueOf(groupGid);
            chatTable.username = groupName;
            chatTable.top = false;
            chatTable.chat_type = true;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_type = IMConstant.MessageType.IMAGE;
            chatTable.message_time = time;
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "保存成功!");
                }
            });
        } else {
            ChatTable chatTable = chatTables.get(0);
            chatTable.is_read = true;
            chatTable.number = 0;
            chatTable.last_message = "[图片消息]";
            chatTable.message_time = time;
            chatTable.username = groupName;
            chatTable.avatar = groupHeader;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));
        }

        MqttService.publish(new Gson().toJson(params), groupGid);
    }

    /**
     * 发送文本消息
     *
     * @param message
     */
    private void sendTextMessage(String message) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("userid", uid);
        params.put("userImage", headImg);
        params.put("time", time);
        params.put("content", message);
        params.put("chattype", 2);
        params.put("username", userInfoData.nickName);
        params.put("type", IMConstant.MessageType.TEXT);
        params.put("groupimg", groupInfoData.groupurl);
        params.put("groupname", groupInfoData.groupname);
        params.put("groupid", groupGid);

        sendMessage = new MessageDetailTable();
        sendMessage.chat_id = userInfoData.uid + "GL" + groupGid;
        sendMessage.message_type = IMConstant.MessageType.TEXT;
        sendMessage.message_state = IMConstant.MessageStatus.SUCCESSED;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 1;
        sendMessage.message_time = time;
        sendMessage.message = message;
        sendMessage.group_name = groupInfoData.groupname;
        sendMessage.group_img = groupInfoData.groupurl;
        sendMessage.group_id = groupGid;

        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupGid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            messages.addAll(list);
            if (adapter == null || rvChatContent == null) return;
            adapter.notifyDataSetChanged();
            rvChatContent.scrollToPosition(adapter.getItemCount() - 1);
        }));

        List<ChatTable> chatTables = LitePal.where("chat_id = ?", sendMessage.chat_id).find(ChatTable.class);
        if (chatTables == null || chatTables.size() <= 0) {
            ChatTable chatTable = new ChatTable();
            chatTable.mobile = userInfoData.userPhone;
            chatTable.chat_id = sendMessage.chat_id;
            chatTable.number = 0;
            chatTable.is_read = true;
            chatTable.last_message = message;
            chatTable.avatar = groupHeader;
            chatTable.user_id = String.valueOf(groupGid);
            chatTable.username = groupName;
            chatTable.top = false;
            chatTable.chat_type = true;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_type = IMConstant.MessageType.TEXT;
            chatTable.message_time = time;
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "保存成功!");
                }
            });
        } else {
            ChatTable chatTable = chatTables.get(0);
            chatTable.last_message = message;
            chatTable.is_read = true;
            chatTable.number = 0;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_time = time;
            chatTable.username = groupName;
            chatTable.avatar = groupHeader;
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));

        }
        Log.d("TAG", "id:" + groupGid);
        MqttService.publish(new Gson().toJson(params), groupGid);
    }

    /**
     * 发送领取红包成功消息给对方
     */
    private void sendSuccessReceiveRadPackage() {
        String time = String.valueOf(System.currentTimeMillis());
        Map<String, Object> params = new HashMap<>();
        params.put("userid", userInfoData.uid);
        params.put("userImage", userInfoData.headImg);
        params.put("time", time);
        params.put("content", userInfoData.nickName + "领取你的红包");
        params.put("chattype", 1);
        params.put("username", TextUtils.isEmpty(userInfoData.nickName) ? userInfoData.userPhone : this.userInfoData.nickName);
        params.put("type", IMConstant.MessageType.RECEIVE_PACKAGE);
        MqttService.publish(groupGid, new Gson().toJson(params));
    }


    /************************************************************************点击*************************************************************************/
    /**
     * 点击文字
     *
     * @param message
     * @param position
     */
    @Override
    public void onTextClickListener(MessageDetailTable message, int position) {

    }

    /**
     * 点击图片
     *
     * @param message
     * @param position
     */
    @Override
    public void onImageClickListener(MessageDetailTable message, int position) {
        String imagesUrl = message.message;
        LitePal.where("chat_id = ? and message_type = ?", message.chat_id, String.valueOf(IMConstant.MessageType.IMAGE)).findAsync(MessageDetailTable.class).listen(list -> {
            if (list == null || list.size() <= 0) return;
            ArrayList<MessageDetailTable> arrayList = new ArrayList<>(list);
            Intent intent = new Intent(GroupChatActivity.this, ChatBigImageActivity.class);
            intent.putParcelableArrayListExtra(Constant.CHAT_IMAGE_LIST, arrayList);
            intent.putExtra(Constant.IMAGE_LARGE, imagesUrl);
            startActivity(intent);
        });
    }

    /**
     * 点击声音
     *
     * @param message
     * @param position
     */
    @Override
    public void onVoiceClickListener(MessageDetailTable message, int position, View view) {
        sendVoice = message;
        currentViewVoice = view;
        sendType = "storage";
        requestPermission(Permission.Group.STORAGE);
    }

    /**
     * 点击红包
     *
     * @param message
     * @param position
     */
    @Override
    public void onRedPackageClickListener(MessageDetailTable message, int position) {
        //收到的红包
        if (message.message_state == IMConstant.MessageStatus.SUCCESSED) {
            Intent intent = new Intent(GroupChatActivity.this, GroupRedPackageDetailActivity.class);
            intent.putExtra(Constant.MESSAGE_RED_PACKAGE_INFO, message);
            startActivity(intent);
        } else {
            //领取逻辑
            showReceivePackageDialog(message);
        }
    }

    /**
     * 点击头像
     *
     * @param message
     * @param position
     */
    @Override
    public void onHeaderViewClickListener(MessageDetailTable message, int position) {
        if (message == null) return;
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        String user_id = message.user_id;
        if (!user_id.equals(String.valueOf(userInfoData.uid))) {
            intent.putExtra(Constant.CONTACT_ID, user_id);

            startActivity(intent);
        }

    }

    @Override
    public void onCardViewClickListener(MessageDetailTable message, int position) {
        if (message == null) return;
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        String user_id = message.message;
        intent.putExtra(Constant.CONTACT_ID, user_id);
        startActivity(intent);

    }

    /**
     * 定位点击
     *
     * @param message
     * @param position
     */
    @Override
    public void onLocationClickListener(MessageDetailTable message, int position) {
        if (message == null) return;
        String location = message.location;
        String longitude = location.split(",")[0];
        String latitude = location.split(",")[1];
        Intent intent = new Intent(this, LocationDetailsActivity.class);
        intent.putExtra(Constant.LATITUDE, latitude);
        intent.putExtra(Constant.LONGITUDE, longitude);
        startActivity(intent);
    }

    /**************************************************************************点击结束**********************************************************************************/

    private void showReceivePackageDialog(MessageDetailTable messageDetailTable) {
        if (receiveRedPackageDialog != null) {
            receiveRedPackageDialog.dismissAllowingStateLoss();
        }
        receiveRedPackageDialog = ReceiveRedPackageDialog.getInstance(messageDetailTable);
        if (!receiveRedPackageDialog.isAdded()) {
            receiveRedPackageDialog.show(getSupportFragmentManager(), "receiveRedPackageDialog");
        } else {
            receiveRedPackageDialog.dismissAllowingStateLoss();
        }
    }

    private void hideReceivePackageDialog() {
        if (receiveRedPackageDialog == null) return;
        receiveRedPackageDialog.dismissAllowingStateLoss();
    }

    private void requestReceiveEadPackage(MessageDetailTable message) {
        Map<String, Object> params = new HashMap<>();
        params.put("hid", message.red_id);
        params.put("auth_token", userInfoData.token);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_RECEVICE_RED_BAAGE, params, this, new JsonCallback<GroupRedpackageBean>(GroupRedpackageBean.class) {
            @Override
            public void onSuccess(Response<GroupRedpackageBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                GroupRedpackageBean body = response.body();
                if (body.code == 1) {
                    UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                    int uid = userInfoData.uid;
                    ArrayList<GroupRedpackageBean.GroupRedpackageData> groupRedpackages = body.data;
                    boolean isRecrvice = false;
                    for (int i = 0; i < groupRedpackages.size(); i++) {
                        GroupRedpackageBean.GroupRedpackageData groupRedpackageData = groupRedpackages.get(i);
                        int uidCurrent = groupRedpackageData.uid;
                        int vipStatus = groupRedpackageData.vipStatus;
                        if (uid == uidCurrent && vipStatus == 1) {
                            isRecrvice = true;
                            vipCredit = groupRedpackageData.vipCredit;
                            userInfoData.creditTotal = vipCredit * 100;
                            BaseApplication.getInstance().saveUserInfo(userInfoData);
                            break;
                        }
                    }
                    if (isRecrvice) {
                        //发送谁领取了
                        sendSuccessReceiveRadPackage();
                        hideReceivePackageDialog();
                        message.message_state = IMConstant.MessageStatus.SUCCESSED;
                        message.saveAsync().listen(success -> {
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            Intent intent = new Intent(GroupChatActivity.this, DemolitionRedPackageResultActivity.class);
                            intent.putExtra(Constant.MESSAGE_RED_PACKAGE_INFO, message);
                            intent.putExtra("moeny", vipCredit);
                            startActivity(intent);
                        });
                    } else {
                        hideReceivePackageDialog();
                        showNotReceiveDialog(message);
                    }
                } else if (body.code==5){//已经抢过了
                    toast.toastShow(body.msg);
                    message.message_state = IMConstant.MessageStatus.SUCCESSED;
                    message.saveAsync().listen(success -> {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    });

                }else {
                    toast.toastShow(body.msg);
                }
            }
        });
    }

    @Override
    public void onReceivePackage(MessageDetailTable message) {
        requestReceiveEadPackage(message);
    }


    private void showNotReceiveDialog(MessageDetailTable message) {
        if (mGroupReceiveRedPackageDialog != null) {
            mGroupReceiveRedPackageDialog.dismissAllowingStateLoss();
        }
        mGroupReceiveRedPackageDialog = GroupReceiveRedPackageDialog.getInstance(message);
        if (mGroupReceiveRedPackageDialog.isAdded()) {
            mGroupReceiveRedPackageDialog.dismissAllowingStateLoss();
        } else {
            mGroupReceiveRedPackageDialog.show(getSupportFragmentManager(), "mGroupReceiveRedPackageDialog");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        MediaManager.pause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onNotReceivePackage(MessageDetailTable message) {
        //点击查看领取详情
    }
}
