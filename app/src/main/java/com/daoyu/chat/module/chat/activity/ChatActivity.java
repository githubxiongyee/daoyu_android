package com.daoyu.chat.module.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.EmojiEvent;
import com.daoyu.chat.event.MenuClickEvent;
import com.daoyu.chat.event.MessageTotalEvent;
import com.daoyu.chat.event.ReceiveMessageEvent;
import com.daoyu.chat.event.UpdateFRemarkEvent;
import com.daoyu.chat.module.chat.adapter.FragAdapter;
import com.daoyu.chat.module.chat.adapter.MessageChatAdapter;
import com.daoyu.chat.module.chat.dialog.ReceiveRedPackageDialog;
import com.daoyu.chat.module.chat.fragment.DefaultFirstMoreFragment;
import com.daoyu.chat.module.chat.fragment.DefaultSecondMoreFragment;
import com.daoyu.chat.module.chat.fragment.Emoji1Fragment;
import com.daoyu.chat.module.chat.fragment.Emoji2Fragment;
import com.daoyu.chat.module.chat.fragment.Emoji3Fragment;
import com.daoyu.chat.module.chat.fragment.Emoji4Fragment;
import com.daoyu.chat.module.chat.fragment.SpecialMoreFragment;
import com.daoyu.chat.module.chat.manager.MediaManager;
import com.daoyu.chat.module.chat.view.AudioRecorderButton;
import com.daoyu.chat.module.home.activity.CommonWebViewActivity;
import com.daoyu.chat.module.home.activity.ContactDetailsActivity;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.Message;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.QiNiuUtil;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.ViewPagerIndicator;
import com.dy.dyim.android.core.LocalUDPDataSender;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.model.Response;
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

public class ChatActivity extends BaseTitleActivity implements MessageChatAdapter.OnItemClickListener, RadioGroup.OnCheckedChangeListener, LocationSource, AMapLocationListener, ReceiveRedPackageDialog.OnReceivePackageListener {
    @BindView(R.id.rv_chat_content)
    RecyclerView rvChatContent;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.iv_show_input)
    ImageView ivShowInput;
    @BindView(R.id.rb_customer_service)
    RadioButton rbCustomerService;
    @BindView(R.id.rb_AI)
    RadioButton rbAI;
    @BindView(R.id.rg_private_help)
    RadioGroup rgPrivateHelp;
    @BindView(R.id.ll_special_default)
    LinearLayout llSpecialDefault;
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
    @BindView(R.id.view_pager_special)
    ViewPager viewPagerSpecial;
    @BindView(R.id.view_pager_special_indicator)
    ViewPagerIndicator viewPagerSpecialIndicator;
    @BindView(R.id.ll_more_special)
    RelativeLayout llMoreSpecial;
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
    @BindView(R.id.tv_right_title)
    TextView tvSigning;

    private InputMethodManager manager;
    private ArrayList<MessageDetailTable> messages;
    private MessageChatAdapter adapter;
    private String friendName;
    private String friendUid;
    private MessageDetailTable sendMessage;
    private UserBean.UserData userInfoData;
    private String sendType;
    private List<Fragment> defaultFragments;
    private List<Fragment> specialFragments;
    private List<Fragment> emojiFragments;
    private DefaultFirstMoreFragment firstMoreFragment;
    private DefaultSecondMoreFragment secondMoreFragment;
    private FragAdapter viewPagerAdapter;
    private boolean isPrivateHelp;//是否是私人特助
    private FragAdapter specialViewPagerAdapter;
    private AMap map;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private String friendHeader;
    private ReceiveRedPackageDialog receiveRedPackageDialog;
    private FragAdapter emojiAdapter;

    private boolean isVoiceSpace = true;
    private View currentView;
    private MessageDetailTable sendVoice;
    private View currentViewVoice;
    private String isAI;
    private String orderId;

    @Override
    public void savedInstanceState(Bundle savedInstanceState) {
        super.savedInstanceState(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (mapView == null) return;
        mapView.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_chat;
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
        Intent intent = getIntent();
        friendName = intent.getStringExtra(Constant.FRIEND_NAME);
        friendUid = intent.getStringExtra(Constant.FRIEND_UID);
        friendHeader = intent.getStringExtra(Constant.FRIEND_HEADER);
        isPrivateHelp = intent.getBooleanExtra(Constant.IS_PRIVATE_HELP, false);
        isAI = intent.getStringExtra(Constant.IS_AI);
        String remarks = intent.getStringExtra(Constant.CONTACT_REMARKS_NAME);
        orderId = intent.getStringExtra("dNum");
        if (!TextUtils.isEmpty(remarks)) {
            friendName = remarks;
        }
        if (TextUtils.isEmpty(friendUid)) {
            finish();
        }


        Log.d("TAG", "friendUid" + friendUid);

        if (isPrivateHelp) {//私人特助
            llSpecialDefault.setVisibility(View.VISIBLE);
            llSendContentSpecial.setVisibility(View.GONE);
            ivInput.setVisibility(View.VISIBLE);
            hideRightAdd();
            showRightTxtTitle("签 约");

            specialFragments = new ArrayList<>();
            SpecialMoreFragment specialMoreFragment = new SpecialMoreFragment();
            specialFragments.add(specialMoreFragment);
            hideMenu();
            specialViewPagerAdapter = new FragAdapter(getSupportFragmentManager(), specialFragments);
            viewPagerSpecial.setAdapter(specialViewPagerAdapter);
            viewPagerSpecialIndicator.setViewPager(viewPagerSpecial);
            viewPagerSpecialIndicator.setNum(1);
            rgPrivateHelp.check(R.id.rb_customer_service);
            map = mapView.getMap();
            map.setLocationSource(this);
            map.setMyLocationEnabled(true);
            map.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        } else {//聊天普通
            if ("Y".equals(isAI)) {
                hideMenu();
            } else {
                showMenu();
            }
            rvChatContent.setVisibility(View.VISIBLE);
            mapView.setVisibility(View.GONE);
            llSendContentSpecial.setVisibility(View.VISIBLE);
            ivInput.setVisibility(View.GONE);
            llSpecialDefault.setVisibility(View.GONE);


            defaultFragments = new ArrayList<>();
            firstMoreFragment = new DefaultFirstMoreFragment();
            secondMoreFragment = new DefaultSecondMoreFragment();
            defaultFragments.add(firstMoreFragment);
            defaultFragments.add(secondMoreFragment);
            viewPagerAdapter = new FragAdapter(getSupportFragmentManager(), defaultFragments);
            viewPagerMore.setAdapter(viewPagerAdapter);
            viewPagerIndicator.setViewPager(viewPagerMore);
            viewPagerIndicator.setNum(2);
        }

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

        setCurrentTitle(TextUtils.isEmpty(friendName) ? "未设置" : friendName);

        rvChatContent.setLayoutManager(new LinearLayoutManager(this));
        messages = new ArrayList<>();
        adapter = new MessageChatAdapter(this, messages);
        adapter.setListener(this);
        rvChatContent.setAdapter(adapter);


        if (isPrivateHelp && !TextUtils.isEmpty(orderId)) {
            sendTextMessage(orderId);
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        setChatBackgroundImage();

        LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            if (list != null || list.size() > 0) {
                messages.addAll(list);
            }
            if (adapter == null) return;
            adapter.notifyDataSetChanged();
            rvChatContent.scrollToPosition(adapter.getItemCount() - 1);
        });
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

        MediaManager.resume();
        if (mapView == null) return;
        mapView.onResume();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        rvChatContent.setOnTouchListener((v, event) -> {
            llMoreDefault.setVisibility(View.GONE);
            llMoreSpecial.setVisibility(View.GONE);
            llEmoji.setVisibility(View.GONE);
            return false;
        });

        llRoot.setOnTouchListener((v, event) -> {
            llMoreDefault.setVisibility(View.GONE);
            llMoreSpecial.setVisibility(View.GONE);
            llEmoji.setVisibility(View.GONE);
            return false;
        });
        ivShowInput.setOnClickListener(this);
        ivInput.setOnClickListener(this);
        ivVoiceSpecial.setOnClickListener(this);
        ivExpressionSpecial.setOnClickListener(this);
        ivMoreSpecial.setOnClickListener(this);
        tvSendSpecial.setOnClickListener(this);
        rgPrivateHelp.setOnCheckedChangeListener(this);
        edChatMessageSpecial.setOnTouchListener((v, event) -> {
            llMoreDefault.setVisibility(View.GONE);
            llMoreSpecial.setVisibility(View.GONE);
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

        tvSigning.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            List<ChatTable> chatTables = LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).find(ChatTable.class);
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
                List<ChatTable> chatTables = LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).find(ChatTable.class);
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
                Intent intent = new Intent(this, ChatSettingActivity.class);
                intent.putExtra(Constant.CONTACT_FRIEND_ID, friendUid);
                startActivity(intent);
                break;

            case R.id.iv_show_input://切换到私人特助输入文字页面
                llEmoji.setVisibility(View.GONE);
                llMoreSpecial.setVisibility(View.GONE);
                llSpecialDefault.setVisibility(View.GONE);
                llSendContentSpecial.setVisibility(View.VISIBLE);
                rgPrivateHelp.check(R.id.rb_customer_service);
                break;
            case R.id.iv_input://切换到私人特助默认
                llEmoji.setVisibility(View.GONE);
                llMoreSpecial.setVisibility(View.GONE);
                llSpecialDefault.setVisibility(View.VISIBLE);
                llSendContentSpecial.setVisibility(View.GONE);
                break;
            case R.id.iv_voice_special://语音
                llEmoji.setVisibility(View.GONE);
                llMoreSpecial.setVisibility(View.GONE);
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
                llMoreSpecial.setVisibility(View.GONE);
                if (llEmoji.getVisibility() == View.VISIBLE) {
                    llEmoji.setVisibility(View.GONE);
                } else {
                    llEmoji.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_more_special://更多菜单
                llMoreDefault.setVisibility(View.GONE);
                llEmoji.setVisibility(View.GONE);
                if (isPrivateHelp) {
                    if (llMoreSpecial.getVisibility() == View.VISIBLE) {
                        llMoreSpecial.setVisibility(View.GONE);
                    } else {
                        llMoreSpecial.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (llMoreDefault.getVisibility() == View.VISIBLE) {
                        llMoreDefault.setVisibility(View.GONE);
                    } else {
                        llMoreDefault.setVisibility(View.VISIBLE);
                    }
                }

                break;
            case R.id.tv_send_special:
                llMoreDefault.setVisibility(View.GONE);
                llMoreSpecial.setVisibility(View.GONE);
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

            case R.id.tv_right_title:
                Intent intent2 = new Intent(this, CommonWebViewActivity.class);
                intent2.putExtra(Constant.WEB_VIEW_URL, "http://futruedao.com:8080/nq/guide/daoyu_agent");
                intent2.putExtra(Constant.WEB_VIEW_TITLE, "签约");
                startActivity(intent2);
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
            if (TextUtils.isEmpty(userId) || !userId.equals(String.valueOf(friendUid))) {
                return;
            }
            LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).findAsync(MessageDetailTable.class).listen(list -> {
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
                llMoreSpecial.setVisibility(View.GONE);
                sendType = "camera";
                requestPermission(Permission.Group.CAMERA, Permission.Group.STORAGE);
                break;
            case "相册":
                llMoreDefault.setVisibility(View.GONE);
                llMoreSpecial.setVisibility(View.GONE);
                sendType = "album";
                requestPermission(Permission.Group.STORAGE);
                break;
            case "红包":
                llMoreDefault.setVisibility(View.GONE);
                llMoreSpecial.setVisibility(View.GONE);
                Intent intent = new Intent(this, SendRebBagActivity.class);
                intent.putExtra(Constant.CONTACT_FRIEND_ID, friendUid);
                intent.putExtra(Constant.CONTACT_NAME, friendName);
                intent.putExtra(Constant.FRIEND_HEADER, friendHeader);
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
    public void onUpdateFRemarkEvent(UpdateFRemarkEvent event) {
        if (event != null) {
            String frmarks = event.frmarks;
            if (!TextUtils.isEmpty(frmarks)) {
                setCurrentTitle(frmarks);
            } else {
                setCurrentTitle(event.nickName);
            }
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

    @SuppressLint("StaticFieldLeak")
    private void doSendMessage(String msg, String friendId) {
        new LocalUDPDataSender.SendCommonDataAsync(this, msg, friendId) {
            @Override
            protected void onPostExecute(Integer code) {
                if (code == 0) {
                    if (sendMessage == null) return;
                    sendMessage.message_state = IMConstant.MessageStatus.SUCCESSED;
                } else {
                    if (sendMessage == null) return;
                    sendMessage.message_state = IMConstant.MessageStatus.SUCCESSED;
                }
                sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).findAsync(MessageDetailTable.class).listen(list -> {
                    messages.clear();
                    messages.addAll(list);
                    if (rvChatContent != null && adapter != null) {
                        adapter.notifyDataSetChanged();
                        rvChatContent.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }));
                long currentTimeMillis = System.currentTimeMillis();
                LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).findAsync(MessageDetailTable.class).listen(list -> {
                    if (list == null || list.size() <= 0) return;
                    for (MessageDetailTable messageDetail : list) {
                        long messageTime = Long.parseLong(messageDetail.message_time);
                        if (currentTimeMillis - messageTime >= 10) {
                            messageDetail.message_state = IMConstant.MessageStatus.FAILED;
                            messageDetail.saveAsync();
                        }
                    }
                });


            }
        }.execute();
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
                intentMap.putExtra(Constant.CONTACT_FRIEND_ID, friendUid);
                intentMap.putExtra(Constant.CONTACT_NAME, friendName);
                intentMap.putExtra(Constant.FRIEND_HEADER, friendHeader);
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
        params.put("chattype", 1);
        params.put("username", userInfoData.nickName);
        params.put("type", IMConstant.MessageType.CARD);
        params.put("card", userInfoData.nickName);
        params.put("cardImage", headImg);

        sendMessage = new MessageDetailTable();
        String chatId = userInfoData.uid + "DL" + friendUid;
        sendMessage.chat_id = chatId;

        sendMessage.message_type = IMConstant.MessageType.CARD;
        sendMessage.message_state = IMConstant.MessageStatus.DELIVERING;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 1;
        sendMessage.message_time = time;
        sendMessage.message = uid;
        sendMessage.card_image = headImg;
        sendMessage.card_name = userInfoData.nickName;
        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            messages.addAll(list);
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
            chatTable.avatar = friendHeader;
            chatTable.user_id = String.valueOf(friendUid);
            chatTable.username = friendName;
            chatTable.top = false;
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
            chatTable.username = friendName;
            chatTable.avatar = friendHeader;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));
        }
        doSendMessage(new Gson().toJson(params), String.valueOf(friendUid));
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
        params.put("chattype", 1);
        params.put("username", userInfoData.nickName);
        params.put("voiceDuration", String.format("%.0f", seconds));
        params.put("type", IMConstant.MessageType.VOICE);
        params.put("voiceName", System.currentTimeMillis());

        sendMessage = new MessageDetailTable();
        sendMessage.chat_id = userInfoData.uid + "DL" + friendUid;
        sendMessage.message_type = IMConstant.MessageType.VOICE;
        sendMessage.message_state = IMConstant.MessageStatus.DELIVERING;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 1;
        sendMessage.message_time = time;
        sendMessage.message = voiceUrl;
        sendMessage.voice_id = String.valueOf(seconds);

        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            messages.addAll(list);
            if (adapter == null) return;
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
            chatTable.avatar = friendHeader;
            chatTable.user_id = String.valueOf(friendUid);
            chatTable.username = friendName;
            chatTable.top = false;
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
            chatTable.username = friendName;
            chatTable.avatar = friendHeader;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));
        }
        doSendMessage(new Gson().toJson(params), String.valueOf(friendUid));
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
        params.put("chattype", 1);
        params.put("username", TextUtils.isEmpty(userInfoData.nickName) ? userInfoData.userPhone : userInfoData.nickName);
        params.put("imageWidth", width);
        params.put("imageHeight", height);
        params.put("type", IMConstant.MessageType.IMAGE);
        params.put("imageName", System.currentTimeMillis() + ".jpg");


        sendMessage = new MessageDetailTable();
        sendMessage.chat_id = userInfoData.uid + "DL" + friendUid;
        sendMessage.message_type = IMConstant.MessageType.IMAGE;
        sendMessage.message_state = IMConstant.MessageStatus.DELIVERING;
        sendMessage.avatar = headImg;
        sendMessage.width = width;
        sendMessage.height = height;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 1;
        sendMessage.message_time = time;
        sendMessage.message = imageUrl;
        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            messages.addAll(list);
            if (adapter == null) return;
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
            chatTable.avatar = friendHeader;
            chatTable.user_id = String.valueOf(friendUid);
            chatTable.username = friendName;
            chatTable.top = false;
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
            chatTable.username = friendName;
            chatTable.avatar = friendHeader;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));
        }
        doSendMessage(new Gson().toJson(params), String.valueOf(friendUid));
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

        Message messageData = new Message(uid, headImg, time,
                message, 1, TextUtils.isEmpty(userInfoData.nickName) ? userInfoData.userPhone : userInfoData.nickName, 1);

        sendMessage = new MessageDetailTable();
        sendMessage.chat_id = userInfoData.uid + "DL" + friendUid;
        sendMessage.message_type = IMConstant.MessageType.TEXT;
        sendMessage.message_state = IMConstant.MessageStatus.DELIVERING;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 1;
        sendMessage.message_time = time;
        sendMessage.message = message;
        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendUid).findAsync(MessageDetailTable.class).listen(list -> {
            messages.clear();
            messages.addAll(list);
            if (adapter == null) return;
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
            chatTable.avatar = friendHeader;
            chatTable.user_id = String.valueOf(friendUid);
            chatTable.username = friendName;
            chatTable.top = false;
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
            chatTable.username = friendName;
            chatTable.avatar = friendHeader;
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));

        }
        Log.d("TAG", "id:" + friendUid);
        doSendMessage(new Gson().toJson(messageData), String.valueOf(friendUid));
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
        doSendMessage(new Gson().toJson(params), String.valueOf(friendUid));
    }

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
            Intent intent = new Intent(ChatActivity.this, ChatBigImageActivity.class);
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
        int userId = Integer.parseInt(message.user_id);
        if (userId == userInfoData.uid) {
            //自己发出去的
            Intent intent = new Intent(ChatActivity.this, RedPackageDetailActivity.class);
            intent.putExtra(Constant.RED_SEND_NAME, userInfoData.nickName);
            intent.putExtra(Constant.RED_SEND_HEADER, userInfoData.headImg);
            intent.putExtra(Constant.RED_RECEIVE_NAME, friendName);
            intent.putExtra(Constant.RED_RECEIVE_HEADER, friendHeader);
            intent.putExtra(Constant.RED_ID, message.red_id);
            RedPackageDetailActivity.messageDetailTable = message;
            startActivity(intent);
        } else {
            //收到的红包
            if (message.message_state == IMConstant.MessageStatus.SUCCESSED) {
                Intent intent = new Intent(ChatActivity.this, RedPackageDetailActivity.class);
                intent.putExtra(Constant.RED_SEND_NAME, message.username);
                intent.putExtra(Constant.RED_SEND_HEADER, message.avatar);
                intent.putExtra(Constant.RED_RECEIVE_NAME, userInfoData.nickName);
                intent.putExtra(Constant.RED_RECEIVE_HEADER, userInfoData.headImg);
                intent.putExtra(Constant.RED_ID, message.red_id);
                startActivity(intent);
            } else {
                //领取逻辑
                showReceivePackageDialog(message);
            }
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
        params.put("user_friend_id", message.user_id);
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_RECEIVE_RED_PACKAGE, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    String changes = (String) body.data;
                    UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                    changes = changes.replaceAll(",", "");
                    userInfoData.creditTotal = Double.parseDouble(changes) * 100;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    //领取成功
                    sendSuccessReceiveRadPackage();
                    hideReceivePackageDialog();
                    message.message_state = IMConstant.MessageStatus.SUCCESSED;
                    message.saveAsync().listen(success -> {
                        adapter.notifyDataSetChanged();
                        Intent intent = new Intent(ChatActivity.this, DemolitionRedPackageResultActivity.class);
                        intent.putExtra(Constant.MESSAGE_RED_PACKAGE_INFO, message);
                        startActivity(intent);
                    });
                } else {
                    toast.toastShow(body.msg);

                }
            }
        });
    }

    @Override
    public void onReceivePackage(MessageDetailTable message) {
        requestReceiveEadPackage(message);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_customer_service://人工客服
                rvChatContent.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                hideRightAdd();
                showRightTxtTitle("签 约");
                break;
            case R.id.rb_AI://AI助手
                rvChatContent.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                hideRightTxtTitle();
                showRightAdd(R.drawable.chat_ai_robot);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
        if (mapView == null) return;
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView == null) return;
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mapView) {
            mapView.onDestroy();
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        MediaManager.release();
        EventBus.getDefault().unregister(this);
    }
}
