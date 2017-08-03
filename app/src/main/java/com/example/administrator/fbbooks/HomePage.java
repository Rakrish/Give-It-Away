package com.example.administrator.fbbooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class HomePage extends CustomMenuActivity {

    private AccessToken accessToken;
    private HashMap<Integer, String[]> pageDetails = new HashMap<Integer, String[]>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //Button logoutButton = (Button)findViewById(R.id.logout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessToken = (AccessToken) getIntent().getParcelableExtra("accessToken"); //Obtaining data
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.v("LoginActivity Response ", response.toString());

                            try {
                                String Name = object.getString("name");
                                String id = object.getString("id");
                                Bundle p = new Bundle();
                                //final AccessToken token = new AccessToken("1737560376544065|Op5s7uS298J36Tr8Rb8_m4oeZYU", AccessToken.getCurrentAccessToken().getApplicationId(), AccessToken.getCurrentAccessToken().getUserId(), Arrays.asList("publish_actions"), null, AccessTokenSource.FACEBOOK_APPLICATION_SERVICE, AccessToken.getCurrentAccessToken().getExpires(), AccessToken.getCurrentAccessToken().getLastRefresh());
                                //AccessToken.setCurrentAccessToken(token);

                                p.putString("access_token", accessToken.getToken());
                                //p.putString("member", id);
                                /*p.putString("access_token", "1737560376544065|Op5s7uS298J36Tr8Rb8_m4oeZYU");
                                p.putString("installed", "true");
                                //Toast.makeText(getApplicationContext(), "Name " + Name, Toast.LENGTH_LONG).show();

                                GraphRequest testUser = new GraphRequest(token, "/1737560376544065/accounts/test-users", p, HttpMethod.POST, new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        JSONObject values = response.getJSONObject();
                                        try {
                                            System.out.println(values.getString("access_token"));
                                            Bundle newParams = new Bundle();
                                            AccessToken newToken = new AccessToken(values.getString("access_token"), AccessToken.getCurrentAccessToken().getApplicationId(), AccessToken.getCurrentAccessToken().getUserId(), Arrays.asList("publish_actions"), null, AccessTokenSource.FACEBOOK_APPLICATION_SERVICE, AccessToken.getCurrentAccessToken().getExpires(), AccessToken.getCurrentAccessToken().getLastRefresh());
                                            //newParams.putString("message", "hello");
                                            GraphRequest getInvite = new GraphRequest(newToken, "/1395971520440188/feed", null, HttpMethod.GET, new GraphRequest.Callback() {
                                                @Override
                                                public void onCompleted(GraphResponse response) {
                                                    System.out.println(response);
                                                }
                                            });
                                            getInvite.executeAsync();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                testUser.executeAsync();*/

                                GraphRequest pagesRequest = new GraphRequest(null, "/" + "1566967186660478" + "/accounts", p, HttpMethod.GET, new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        FacebookRequestError error = response.getError();
                                        if (error != null) {
                                            Log.e("Error", error.getErrorMessage());
                                        } else {
                                            //ok so here, we're getting the pages back in a few JSON wrappers
                                            JSONObject values = response.getJSONObject();
                                            JSONArray array = null;
                                            try {
                                                array = values.getJSONArray("data");
                                                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_home_page);// you are getting a refrence to your layout
                                                //ok, so here we're just iterating through the pages a user has, obviously you can handle this accordingly..
                                                for (int index = 0; index < array.length(); index++) {
                                                    //ok, here's how to actually get the token
                                                    String pageAccessToken = array.getJSONObject(index).getString("access_token");
                                                    String name = array.getJSONObject(index).getString("name");
                                                    String id = array.getJSONObject(index).getString("id");
                                                    addPages(index, name, id, pageAccessToken);
                                                    System.out.println(array.getJSONObject(index));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                });
                                pagesRequest.executeAsync();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }
        /*logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(HomePage.this,MainActivity.class);
                startActivity(intent);
            }
        });*/
    }


    public void addPages(int index, String btnText, String id, String pageAccessToken) {
        String detailsArr[] = {id, btnText, pageAccessToken};
        pageDetails.put(index, detailsArr);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pages_container_list);
        final Button btn = new Button(this);
        linearLayout.addView(btn);
        btn.setText(btnText);
        btn.setId(index);
        btn.setWidth(320);
        btn.setHeight(40);
        btn.setOnClickListener(btnClick);
    }
    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.println(view.getId());
            System.out.println(pageDetails.get(view.getId())[0]);
            System.out.println(pageDetails.get(view.getId())[1]);
            System.out.println(pageDetails.get(view.getId())[2]);
            Intent intent = new Intent(HomePage.this, PublishPost.class);
            Bundle extras = new Bundle();
            HashMap<String, String> pageInfo = new HashMap<String, String>();
            pageInfo.put("id", pageDetails.get(view.getId())[0]);
            pageInfo.put("name", pageDetails.get(view.getId())[1]);
            pageInfo.put("pageAccessToken", pageDetails.get(view.getId())[2]);
            extras.putSerializable("pageInfo", pageInfo);
            intent.putExtras(extras);
            startActivity(intent);
            //overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
            //String pageInfo[] = {pageDetails.get(view.getId())[0], pageDetails.get(view.getId())[1], pageDetails.get(view.getId())[2]};
            //intent.putExtra("pageInfo", pageInfo);
        }
    };
}
