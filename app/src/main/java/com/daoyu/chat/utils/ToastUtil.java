package com.daoyu.chat.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtil {
    private  static Context context;
    private static Toast toast = null;

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    public ToastUtil(Context context) {
        this.context = context;
    }

    public void toastShow(String text) {
        if (!TextUtils.isEmpty(text)) {
            if ("session_error".equals(text.trim())) return;
            if (toast == null) {
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            } else {
                toast.setText(text);
            }
            toast.show();
        }
    }

    public void toastShow(String text, int duration) {
        if (!TextUtils.isEmpty(text)) {
            if (toast == null) {
                toast = Toast.makeText(context, text, duration);
            } else {
                toast.setText(text);
            }
            toast.show();
        }
    }


}    