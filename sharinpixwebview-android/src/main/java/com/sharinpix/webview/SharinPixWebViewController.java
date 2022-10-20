package com.sharinpix.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

class SharinPixWebViewController {
    private SharinPixWebView.OnResultListener onResultListener;

    public void setOnResultListener(SharinPixWebView.OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @JavascriptInterface
    public boolean postMessage(String message) throws JSONException {
        Log.d("WebViewController", message);
        JSONObject jsonObject = new JSONObject(message);
        if (onResultListener != null) {
            this.onResultListener.onResult(jsonObject);
        }
        return false;
    }
}
