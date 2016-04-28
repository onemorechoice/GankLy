package com.gank.gankly.ui.web;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.gank.gankly.App;
import com.gank.gankly.R;
import com.gank.gankly.data.entity.UrlCollect;
import com.gank.gankly.data.entity.UrlCollectDao;
import com.gank.gankly.ui.base.BaseActivity;
import com.gank.gankly.utils.AppUtils;
import com.gank.gankly.utils.ShareUtils;
import com.gank.gankly.utils.ToastUtils;

import java.io.InputStream;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebActivity extends BaseActivity {
    @Bind(R.id.web_view)
    WebView mWebView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.web_progress_bar)
    ProgressBar mProgressBar;

    private String mUrl;
    private String mTitle;
    private UrlCollectDao mUrlCollectDao;

    private void addUrl() {
        UrlCollect urlCollect = new UrlCollect(null, mWebView.getUrl(), mTitle, new Date());
        mUrlCollectDao.insert(urlCollect);
        ToastUtils.showToast(R.string.collect_success);
    }

    public class MyWebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 这些视频需要hack CSS才能达到全屏播放的效果
            if (url.contains("www.vmovier.com")) {
                injectCSS("vmovier.css");
            }
            else if (url.contains("video.weibo.com")) {
                injectCSS("weibo.css");
            }
            else if (url.contains("m.miaopai.com")) {
                injectCSS("miaopai.css");
            }
        }
    }

    public class MyWebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mProgressBar == null) {
                return;
            }
            mProgressBar.setProgress(newProgress);

            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return true;
        }
    }

    // Inject CSS method: read style.css from assets folder
    // Append stylesheet to document head
    private void injectCSS(String filename) {
        try {
            InputStream inputStream = this.getAssets().open(filename);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            mWebView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startWebActivity(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, WebActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.welfare_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.welfare_collect:
                addUrl();
                return true;
            case R.id.welfare_share:
                ShareUtils.getInstance().shareQQ(mTitle, mWebView.getUrl(), "干货分享", "");
                return true;
            case R.id.welfare_copy_url:
                AppUtils.copyText(this, mWebView.getUrl());
                ToastUtils.showToast(R.string.tip_copy_success);
                return true;
            case R.id.welfare_refresh:
                mWebView.reload();
                return true;
            case R.id.welfare_browser:
                openBrowser(mWebView.getUrl());
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        if (intent.resolveActivity(WebActivity.this.getPackageManager()) != null) {
            WebActivity.this.startActivity(intent);
        } else {
            ToastUtils.showToast(R.string.web_open_failed);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initViews() {
        WebSettings settings = mWebView.getSettings();
        //支持获取手势焦点，输入用户名、密码或其他
        mWebView.requestFocusFromTouch();

        settings.setJavaScriptEnabled(true);  //支持js
        settings.setSupportZoom(true); //设置支持缩放
        settings.setBuiltInZoomControls(true); //
        settings.setDisplayZoomControls(false);//是否显示缩放控件
        settings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        settings.supportMultipleWindows();  //多窗口
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
        settings.setAllowFileAccess(true);  //设置可以访问文件
        settings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式


        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void bindListener() {
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true); //显示返回箭头
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUrl = bundle.getString("url");
            mTitle = bundle.getString("title");
        }

        mUrlCollectDao = App.getDaoSession().getUrlCollectDao();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        ButterKnife.unbind(this);
    }
}