package com.daoyu.chat.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.CommonUploadListener;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.QiNiuTokenB;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;

public class QiNiuUtil {

    public static void request(Context context, File upFile, String niuFileName, ImageView imageView) {
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_QINIU_GETTOKEN, null, "QINIU", new JsonCallback<QiNiuTokenB>(QiNiuTokenB.class) {
            @Override
            public void onSuccess(Response<QiNiuTokenB> response) {
                if (response == null || response.body() == null) return;
                QiNiuTokenB body = response.body();
                if (body.isSuccess()) {
                    if (body.getData() == null) {
                        return;
                    }
                    Constant.QINIU_TOKEN = body.getData();
                    Log.i("info", "========qiniuToken========" + Constant.QINIU_TOKEN);
                    upload(context, upFile, niuFileName, body.getData(), imageView);
                }
            }
        });
        return;
    }

    public static void upload(Context context, File upFile, String niuFileName, String niuToken, ImageView imageView) {
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                //.recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
                //.recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                //.zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        // 重用uploadManager一般地，只需要创建一个uploadManager对象
        UploadManager uploadManager = new UploadManager(config);
        //data = <File对象、或 文件路径、或 字节数组>
        //String key = "<指定七牛服务上的文件名，或 null>";
        String key = "";
        //String token = "<从服务端获取>";
        String qiniuBaseURL = "http://image.futruedao.com/";
        //String token = "fP6KfIAAIJG0ZpEQQGxwbfu6TZyF_r2mCt5GHRUb:dHhKLcp83zrXBAYhm87PFnN-jQ0=:eyJpbnNlcnRPbmx5IjoxLCJzY29wZSI6InByb2R1Y3RzLWltYWdlIiwiZGVhZGxpbmUiOjE1NjQ4NTUwMjd9";//
        uploadManager.put(upFile, niuFileName + ".jpg", niuToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                //res包含hash、key等信息，具体字段取决于上传策略的设置
                if (info.isOK()) {
                    Log.i("qiniu", "QiNiu Upload Success===" + qiniuBaseURL + key);
                    ImageUtils.setRoundImageView(context, qiniuBaseURL + key, R.drawable.my_user_default, imageView);
                    UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                    userInfoData.headImg = qiniuBaseURL + key;
                } else {
                    Log.i("qiniu", "QiNiu Upload Fail");
                    //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                }
                Log.i("qiniu", key + ", " + info + ", " + res);

            }
        }, null);
    }

    public static String splitMediaName(String path) {
        // 判空操作必须要有 , 处理方式不唯一 , 根据实际情况可选其一 。
        if (path == null) {
            //throw new Exception("路径不能为null"); // 处理方法一
            //throw new RuntimeException("路径不能为null"); // 处理方法二
            //toast 提示用户 // 处理方法三
        }

        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return path.substring(start + 1, end);//包含头不包含尾 , 故:头 + 1
        } else {
            return "";
//            return null; 返回 null 还是 "" 根据情况抉择吧
        }
    }


    /**************************************************图片聊天上传***********************************************************/
    public static void request(File upFile, String niuFileName, CommonUploadListener listener,String extensionName) {
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_QINIU_GETTOKEN, null, "QINIU", new JsonCallback<QiNiuTokenB>(QiNiuTokenB.class) {
            @Override
            public void onSuccess(Response<QiNiuTokenB> response) {
                if (response == null || response.body() == null) return;
                QiNiuTokenB body = response.body();
                if (body.isSuccess()) {
                    if (body.getData() == null) {
                        return;
                    }
                    Constant.QINIU_TOKEN = body.getData();
                    upload(upFile, niuFileName, body.getData(), listener,extensionName);
                } else {
                    if (listener == null) return;
                    listener.onItemClick("", false);
                }
            }
        });
    }

    public static void upload(File upFile, String niuFileName, String niuToken, CommonUploadListener listener,String extensionName){
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)
                .putThreshhold(1024 * 1024)
                .connectTimeout(10)
                .useHttps(true)
                .responseTimeout(60)
                .build();
        UploadManager uploadManager = new UploadManager(config);
        String qiniuBaseURL = "http://image.futruedao.com/";
        uploadManager.put(upFile, niuFileName + extensionName, niuToken, (key, info, res) -> {
            if (info.isOK()) {
                String url = qiniuBaseURL + key;
                if (listener == null) return;
                listener.onItemClick(url, true);
            } else {
                if (listener == null) return;
                listener.onItemClick("", false);
            }
        }, null);
    }

    /**
     * 上传byte 图片数组
     *
     * @param data
     * @param niuFileName
     * @param listener
     * @param extensionName
     */
    public static void requestByteData(byte[] data, String niuFileName, CommonUploadListener listener, String extensionName) {
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_QINIU_GETTOKEN, null, "QINIU", new JsonCallback<QiNiuTokenB>(QiNiuTokenB.class) {
            @Override
            public void onSuccess(Response<QiNiuTokenB> response) {
                if (response == null || response.body() == null) return;
                QiNiuTokenB body = response.body();
                if (body.isSuccess()) {
                    if (body.getData() == null) {
                        return;
                    }
                    Constant.QINIU_TOKEN = body.getData();
                    upload(data, niuFileName, body.getData(), listener, extensionName);
                } else {
                    if (listener == null) return;
                    listener.onItemClick("", false);
                }
            }
        });
    }

    public static void upload(byte[] data, String niuFileName, String niuToken, CommonUploadListener listener, String extensionName) {
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)
                .putThreshhold(1024 * 1024)
                .connectTimeout(10)
                .useHttps(true)
                .responseTimeout(60)
                .build();
        UploadManager uploadManager = new UploadManager(config);
        String qiniuBaseURL = "http://image.futruedao.com/";
        uploadManager.put(data, niuFileName + extensionName, niuToken, (key, info, res) -> {
            if (info.isOK()) {
                String url = qiniuBaseURL + key;
                if (listener == null) return;
                listener.onItemClick(url, true);
            } else {
                if (listener == null) return;
                listener.onItemClick("", false);
            }
        }, null);
    }
}
