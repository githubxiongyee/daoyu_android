package com.daoyu.chat.module.mine.activity;

import android.support.constraint.ConstraintLayout;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;

import butterknife.BindView;

public class DYWebsiteAct extends BaseTitleActivity {
    @BindView(R.id.wb_website)
    WebView wbDY;
    @BindView(R.id.cl_webContainer)
    ConstraintLayout clWeb;
    @Override
    protected int getLayoutResId() {
        return R.layout.act_dy_website;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("官网");

        WebSettings webSettings = wbDY.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);

        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);

        wbDY.loadUrl("http://futruedao.com/");

        wbDY.setWebViewClient(new WebViewClient());

        wbDY.setWebChromeClient(new WebChromeClient());//这行最好不要丢掉

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyWebView();
    }
    public void destroyWebView() {



        if(wbDY != null) {
            clWeb.removeAllViews();
            wbDY.clearCache(true);
            wbDY.removeAllViews();
            wbDY.destroy();
            wbDY = null; // Note that mWebView.destroy() and mWebView = null do the exact same thing
        }

    }
}
