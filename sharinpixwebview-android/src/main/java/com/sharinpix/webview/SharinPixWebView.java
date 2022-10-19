package com.sharinpix.webview;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

public class SharinPixWebView {
    public interface OnResultListener {
        void onResult(JSONObject jsonObject) throws JSONException;
    }

    private Activity activity;
    private int webViewId;
    private String url;

    private View parentView;
    private ValueCallback<Uri[]> uploadMessage;
    private SharinPixWebViewController sharinPixWebViewController;
    private OnResultListener onResultListener;

    public SharinPixWebView(Activity activity, int webViewId, String url) {
        this.activity = activity;
        this.webViewId = webViewId;
        this.url = url;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        this.parentView = this.activity.getWindow().getDecorView().findViewById(android.R.id.content);
        WebView webView = (WebView) this.parentView.findViewById(this.webViewId);
        webView.loadUrl(this.url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient(this.activity));
        this.sharinPixWebViewController = new SharinPixWebViewController();
        webView.addJavascriptInterface(this.sharinPixWebViewController, "SharinPixWebViewController");
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
        this.sharinPixWebViewController.setOnResultListener(this.onResultListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
            if (null == uploadMessage || intent == null || resultCode != RESULT_OK) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
                return;
            }
            String dataString = intent.getDataString();
            if (dataString != null) {
                uploadMessage.onReceiveValue(new Uri[]{Uri.parse(dataString)});
            }
            uploadMessage = null;
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        private Activity activity;

        CustomWebChromeClient(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
            }
            uploadMessage = filePathCallback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            this.activity.startActivityForResult(Intent.createChooser(i, "SharinPix File Chooser"), 1);
            return true;
        }
    }
}
