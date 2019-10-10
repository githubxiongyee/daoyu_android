package com.daoyu.chat.utils;

import android.os.Handler;
import android.os.Message;


import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseFragment;

import java.lang.ref.WeakReference;


public class NoLeakHandler extends Handler {

    WeakReference<BaseActivity> mActivityReference;
    WeakReference<BaseFragment> mFragmentReference;

    public NoLeakHandler(BaseActivity activity) {
        mActivityReference = new WeakReference<>(activity);
    }

    public NoLeakHandler(BaseFragment fragment) {
        mFragmentReference = new WeakReference<>(fragment);
    }

    @Override
    public void handleMessage(Message msg) {
        BaseActivity activity = null;
        if (mActivityReference != null) {
            activity = mActivityReference.get();
        }

        BaseFragment fragment = null;
        if (mFragmentReference != null) {
            fragment = mFragmentReference.get();
        }

        if (activity != null) {
            activity.handleMessage(msg);
        }
        if (fragment != null) {
            fragment.handleMessage(msg);
        }
    }

}
