package com.sharinpix.webview;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SharinPixWebView {
    public interface OnResultListener {
        void onResult(JSONObject jsonObject) throws JSONException;
    }

    private Activity activity;
    private int webViewId;
    private String url;

    private View parentView;
    private ValueCallback<Uri[]> uploadMessage;
    private boolean allowMultipleFiles = false;
    private String cameraFileData;
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
            if (null == uploadMessage || resultCode != RESULT_OK) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
                return;
            }
            if (intent == null) {
                if (cameraFileData != null) {
                    uploadMessage.onReceiveValue(new Uri[] { Uri.parse(cameraFileData) });
                }
            } else {
                Uri uri = intent.getData();
                if (uri != null) {
                    uploadMessage.onReceiveValue(new Uri[]{uri});
                }
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

            if (ContextCompat.checkSelfPermission(this.activity,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                (ContextCompat.checkSelfPermission(this.activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this.activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                uploadMessage = filePathCallback;
                Intent takePictureIntent;

                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.activity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpeg", this.activity.getCacheDir());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        photoFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    takePictureIntent.putExtra("takePictureIntentPath", cameraFileData);
                    if (photoFile != null) {
                        cameraFileData = "file:" + photoFile.getAbsolutePath();
                        Uri photoFileUri = FileProvider.getUriForFile(this.activity, "com.sharinpix.webview.SharinPixFileProvider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    } else {
                        cameraFileData = null;
                        takePictureIntent = null;
                    }
                }

                Intent fileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileChooserIntent.addCategory(Intent.CATEGORY_OPENABLE);
                fileChooserIntent.setType("*/*");
                if (allowMultipleFiles) {
                    fileChooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, fileChooserIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "SharinPix File Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                this.activity.startActivityForResult(chooserIntent, 1);
                return true;
            } else {
                ActivityCompat.requestPermissions(this.activity,
                        new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE },
                        1);
                return false;
            }
        }
    }
}
