package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;

import butterknife.BindView;

public class CommonWebViewActivity extends BaseTitleActivity {
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    private String url;
    private String title;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_common_web_view;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        url = intent.getStringExtra(Constant.WEB_VIEW_URL);
        title = intent.getStringExtra(Constant.WEB_VIEW_TITLE);
        setDefaultWebSettings(webview);
        webview.setWebChromeClient(webChromeClient);
        webview.setWebViewClient(webViewClient);
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(title)) {
            finish();
        }
        setCurrentTitle(title);
        webview.loadUrl(url);
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressBar == null) return;
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (progressBar == null) return;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (progressBar == null) return;
            progressBar.setProgress(newProgress);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void setDefaultWebSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        //允许js代码
        webSettings.setJavaScriptEnabled(true);

    }
}
