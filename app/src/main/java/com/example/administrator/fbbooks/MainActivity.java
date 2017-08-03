package com.example.administrator.fbbooks;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "You are not connected to the internet",
                    Toast.LENGTH_LONG).show();
        }
        String permissionsArray[] = {
                "email",
                "public_profile",
                "manage_pages",
                "pages_show_list",
                "user_posts"
        };
        loginButton.setReadPermissions(Arrays.asList(permissionsArray));
        loginButton.clearPermissions();
        String writePermArray[] = {
                "publish_actions"
        };
        loginButton.setPublishPermissions(Arrays.asList(writePermArray));
        loginButton.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        next = (Button)findViewById(R.id.loggedin);
        if(isLoggedin()){
            next.setVisibility(View.VISIBLE);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, PublishPost.class);
                    intent.putExtra("accessToken", AccessToken.getCurrentAccessToken());
                    startActivity(intent);
                }
            });
        } if(!isLoggedin()) {
            next.setVisibility(View.GONE);
        }
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null) {
                    next.setVisibility(View.GONE);
                } else {
                    next.setVisibility(View.VISIBLE);
                }
            }
        };
        getLoginDetails(loginButton);
    }

    protected void getLoginDetails(LoginButton loginButton){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(MainActivity.this, PublishPost.class);
                intent.putExtra("accessToken", loginResult.getAccessToken());
                startActivity(intent);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(), "You are not connected to the internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public static String getSubnet(String currentIP) {
        int firstSeparator = currentIP.lastIndexOf("/");
        int lastSeparator = currentIP.lastIndexOf(".");
        return currentIP.substring(firstSeparator+1, lastSeparator+1);
    }
    public boolean isLoggedin(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("Data", data.toString());
    }
}
