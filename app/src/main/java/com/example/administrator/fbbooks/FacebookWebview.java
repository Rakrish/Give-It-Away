package com.example.administrator.fbbooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

public class FacebookWebview extends AppCompatActivity {

    private WebView webView;
    private String id, userAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_webview);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        id = getIntent().getStringExtra("id");
        userAccessToken = getIntent().getStringExtra("access_token");
        webView.loadUrl("https://www.facebook.com/" + id + "/" + "?access_token=" + userAccessToken);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                startActivity(new Intent(FacebookWebview.this, MainActivity.class));
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, PublishPost.class));
    }
}
