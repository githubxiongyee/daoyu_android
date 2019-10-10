package com.daoyu.chat.utils.http;

public class UrlConfig {
    public static final String BASE_URL = "http://120.78.169.242:9090/api/";
    //public static final String BASE_URL = "http://192.168.1.121:9090/api/";
    public static final String BASE_GROUP_URL = "http://120.24.233.101:10905/";
    //public static final String BASE_URL = "http://192.168.1.143:9090/api/";
    //public static final String BASE_GROUP_URL = "http://192.168.1.143:10905/";

    public static final String URL_LOGIN = "login/mobile";//用户登录 post
    public static final String URL_SEND_VERIFICATION_CODE = "captcha/";//短信验证码 get
    public static final String URL_DETECTION_MOBILE = "check/mobile/";//检测手机是否已注册 get
    public static final String URL_REGISTERED = "mobile/register";//注册 post
    public static final String URL_FORGET_PWD = "/forgetPwd";//忘记密码

    public static final String URL_REB_BAG_ADS_LIST = "product/list";//红包广告
    public static final String URL_EXPLOSION_LIST = "product/list";//爆款列表

    public static final String URL_GET_RED_PACKAGE = "credit/updateCreditByUserId";//领取红包
    public static final String URL_CHANGE_INFO = "credit/getCreditByUserId";//零钱信息

    public static final String URL_GET_PACKAGE_LIST = "credit/list";//获取红包（积分）列表
    public static final String URL_ORDER_LIST = "orders/list";//订单列表

    public static final String URL_CHANGE_LIST_DETAIL = "orders/getOrderByUserCredit";//订单详情0808

    public static final String URL_CREATE_ORDER = "orders/create";//创建订单
    public static final String URL_CREATE_CHANGE = "hongbao/createByUserCredit";//红包兑换订单生成
    public static final String URL_RED_PACKAGE_CHANGE = "hongbao/payByUserCredit";//红包兑换


    public static final String URL_WECHAT_ORDER = "wx/app/pay";//微信支付

    public static final String URL_PAYMENT_ALIPAY_UNION = "union/payUnionBusinessQRVal";//支付包云闪付
    public static final String URL_REMAIN_AMOUNT = "business/getBusinessFinance";//余额
    public static final String URL_REMAIN_DETAIL = "business/getBalanceList";//余额明细
    public static final String URL_PAYMENT_QR = "union/getUnionBusinessQRVal";//付款方式
    public static final String URL_PAYMENT_QR_TEST = "union/getUnionBusinessQRValByTest";//付款方式

    public static final String URL_SIGN_IN = "giftmoney/checkin";//签到
    public static final String URL_GET_LAST_SIGN = "giftmoney/getcheckininfo";//得到上一次的签到信息


    public static final String URL_QR_AAD_FRIEND = "user/getUserQRVal";//二维码扫描添加好友
    public static final String URL_CONTACT_FRIEND_LIST = "friend/friendbyid";//好友列表
    public static final String URL_SEARCH_CONTACT = "friend/searchf";//搜索联系人
    public static final String URL_ADD_CONTACT = "friend/addfriendnew";//添加联系人

    public static final String URL_UPDATE_USER_INFO = "user/update";//完善个人信息

    public static final String URL_QINIU_GETTOKEN = "friend/gettoken";//获取七牛云的token

    public static final String URL_GET_DEFAULT_ADDRESS = "address/getDef";//获取默认地址

    public static final String URL_UPDATA_ADD_ADDRESS = "address/updateByAddressId";//新增/更新地址
    public static final String URL_ADDRESS_LIST = "address/list";//地址列表
    public static final String URL_DELETE_ADDRESS = "address/del";//删除地址
    public static final String URL_BIND_ADDRESS = "orderAddress/add";//订单地址关联
    public static final String URL_SET_DEFAULT_ADDRESS = "address/updateDef";//设置默认地址
    public static final String URL_MODIFY_LOGIN_PWD = "user/resetPwd";//修改登录密码

    public static final String URL_SET_FRIEND_BLACK = "friend/setfriendblack";//拉黑某个好友
    public static final String URL_SET_FRIEND_WHITE = "friend/setfriendwhite";//拉白某个好友

    public static final String URL_DELETE_FRIEND_SINGLE = "friend/delfriendsingle";//单边删除好友

    public static final String URL_SET_FRIEND_REMARKS = "friend/setfriendremarks";//设置好友备注名
    public static final String URL_SET_PAY_PWD = "user/setPaySign";//设置支付密码
    public static final String URL_CHECK_PAY_PWD = "user/resetPaySign";//设置支付密码的验证码

    public static final String URL_SET_FRIEND_CATEGORY = "friend/setfriendtype";//设置好友分类
    public static final String URL_NEARBY_PEOPLE_LIST = "user/getUserNearFriend";//附近的人列表

    public static final String URL_NEARBY_PEOPLE_ADD_GET_MONEY = "giftmoney/addfriend";//通过附近的人加好友
    public static final String URL_IS_FRIEND = "friend/getfriendinfo";//查询是否是好友，如果是好友，显示好友的关系以及所有好友信息，如果不是，返回空列表

    public static final String URL_SEND_RED_BAG = "friend/setgiftmoney";//发红包给个人

    public static final String URL_GET_RED_PACKAGE_DETAILS = "friend/getsendedmoneybyid";//根据ID查询红包情况 vip_status=3表示未领红包，vip_status=2表示已经领过了

    public static final String URL_RECEIVE_RED_PACKAGE = "friend/getgiftmoney";//个人收好友红包,用红包ID收取好友红包

    public static final String URL_GET_USER_INFO_BY_ID = "friend/userbyid";//获取用户信息通过id

    public static final String URL_CLASS_CONTACT = "friend/selectwhitelistbytype";//得到某个分类的【工作类,生活类】好友白名单列表

    public static final String URL_BLACK_LIST_CONTACT = "friend/selectblacklist";//获取好友黑名单列表

    public static final String URL_BATCHES_ADD_TO_LABEL = "friend/setfriendtypes";// 批量添加到指定分组内

    public static final String URL_WHITE_LABEL_LIST = "friend/selectwhitelist";//白名单列表
    public static final String URL_SELECT_WHITE_LIST_BY_TYPE = "friend/selectwhitelistbynftype";//得到非某个分类的【工作类,生活类】好友白名单列表, 传入工作类

    public static final String URL_OFFLINE_MESSAGE = "offline/offlineallmsg";//收取离线消息

    public static final String URL_CONTACT_MOBILE = "friend/getmobilereturn";//通讯录好友信息

    public static final String URL_UPGRADE = "systemconfig/getandroidappvalue";//升级;得到android版本变量

    public static final String URL_SCAN_SEND_MONEY = "friend/sendgiftmoney";//扫码转钱

    public static final String URL_CREATE_GROUP = "group/creategroup";//创建群

    public static final String URL_GET_USERS_HEADER = "friend/getUsernh";//获取用户头像昵称

    public static final String URL_BIND_GROUP_USER = "group/updategroupurl";//绑定群头像

    public static final String URL_GET_GROUT_INFO = "group/findgroup";//查询群信息

    public static final String URL_MODIFY_GROUP_NAME = "group/updategroupname";//修改群名称

    public static final String URL_SET_USER_VALUE = "systemconfig/setuservalue";//设置用户自身变量和值
    public static final String URL_GET_USER_VALUE = "systemconfig/getuservalue";//得到用户变量设置用户自身变量和值
    public static final String URL_MODIFY_MY_GROUP_NICK = "group/updategroupusernickname";//修改用户群昵称

    public static final String URL_GROUP_SET_NOTICE = "group/updategroupnotice";//设置群公告

    public static final String URL_DELETE_GROUP = "group/deletegroup";//删除群聊

    public static final String URL_EXIT_GROUP = "group/exitgroup";//推出群聊

    public static final String URL_SEARCH_USER_ALL_GROUP = "group/getusergroup";//查询用户所有的群聊

    public static final String URL_ADD_CHAT_FROUP = "group/joingroup";//加入群聊

    public static final String URL_REMOVE_CHAT_FROUP = "group/removegroup";//删除群聊

    public static final String URL_LORD_TRANSFER = "group/changegroupadmin";//转让群主
    public static final String URL_UPLOAD_CONTACT = "giftmoney/putmobilelist";//批量存储通讯录

    public static final String URL_SEND_GROUP_RED_BAG = "giftmoney/setgroupgiftmoney";//发送群红包
    public static final String URL_RECEVICE_RED_BAAGE = "giftmoney/getgroupgiftmoney";//领取群红包
    public static final String URL_RECEVICE_DETAILS = "giftmoney/getgroupgiftmoneyinfo";//领取群红包详细

    public static final String URL_GET_FRIEND_AI = "friend/getai";//得到AI人工助手ID


}