package com.sharinpix.webviewtest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sharinpix.webview.SharinPixWebView;
import com.sharinpix.webviewtest.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharinPixWebView sharinPixWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.sharinPixWebView = new SharinPixWebView(MainActivity.this, R.id.webView, "https://app.sharinpix.com");
        this.sharinPixWebView.setOnResultListener(new SharinPixWebView.OnResultListener() {
            @Override
            public void onResult(JSONObject jsonObject) throws JSONException {
                Toast.makeText(getApplicationContext(), jsonObject.getString("name"), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.sharinPixWebView.onActivityResult(requestCode, resultCode, data);
    }
}