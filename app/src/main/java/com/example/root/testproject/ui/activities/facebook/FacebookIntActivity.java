package com.example.root.testproject.ui.activities.facebook;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.testproject.R;
import com.example.root.testproject.ui.activities.base.navigationaction.BaseActivity;
import com.example.root.testproject.ui.activities.main.MainActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.facebook.AccessToken.getCurrentAccessToken;

public class FacebookIntActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY = "KEY";
    private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires";
    CallbackManager callbackManager;
    Button share, details;
    ShareDialog shareDialog;
    LoginButton login;
    ProfilePictureView profile;
    Dialog details_dialog;
    TextView details_txt;
    ShareButton shareButton;

    String id = "1343873972369636";
    private Bitmap image;
    private AccessToken mAccessToken;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_int);
        Log.e("FacebookIntActivity", "token is " + mAccessToken);
        initilizeSdk();

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                Log.e("FacebookIntActivity", "onSuccess" + loginResult.getAccessToken());
                Log.e("FacebookIntActivity", "Token" + loginResult.getAccessToken().getToken());
                Log.e("FacebookIntActivity", "Permision" + loginResult.getRecentlyGrantedPermissions());

                Log.e("OnGraph", "------------------------");
                Log.e("FacebookIntActivity", "in success" + mAccessToken);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        shareDialog = new ShareDialog(this);
        createDialog();

        setContantOnDialog();
        setReferences();
        setListeners();
        setPermission();
        saveCredentials(mAccessToken);
        Log.e(FacebookIntActivity.class.getSimpleName(), "" + AccessToken.getCurrentAccessToken());
        Log.e("FacebookIntActivity", "shaer call");
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FacebookIntActivity", " call");
                sharePhotoToFacebook();
            }
        });
    }

    private void setContantOnDialog() {

        details_dialog.setContentView(R.layout.dialogs_details);
        details_dialog.setTitle("Details");
        Log.e("FacebookIntActivity", "On setContantOnDialog");
    }

    private void createDialog() {
        details_dialog = new Dialog(this);
    }


    private void setPermission() {
        if (login != null)
            login.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email", "user_birthday", "user_likes"));

    }

    public void requestData() {
        GraphRequest request = GraphRequest.newMeRequest(getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                Log.e("FacebookIntActivity", "requestData start");
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(json.getString("name")).append("\n" + json.getString("email"))
                                .append("\n" + json.getString("gender"))
                                .append("\n" + json.getString("birthday"));
                        details_txt.setText(stringBuffer);
                        profile.setProfileId(json.getString("id"));
                        Log.e("FacebookIntActivity", "requestData start" + stringBuffer);
                        Log.e("FacebookIntActivity", "Likes found " + json.optString("likes"));
                    }

                    details_dialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,first_name,last_name,location,locale,timezone");
        request.setParameters(parameters);
        request.executeAsync();

        getFriendInfo();
        getUserLikes();
    }

    private void getUserLikes() {

    /* make the API call */
        new GraphRequest(
                mAccessToken,
                id + "/likes",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        Log.e("FacebookIntActivity", "-------------" + response.toString());

                    }
                }
        ).executeAsync();
    }

    private void getFriendInfo() {
        new GraphRequest(
                mAccessToken,
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                                /* handle the result */
                        Log.e("FacebookIntActivity", "Friend in List" + response.toString());
                    }
                }
        ).executeAsync();
    }

    private void initilizeSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }


    @Override
    protected void setListeners() {
        details.setOnClickListener(this);
        share.setOnClickListener(this);
    }


    @Override
    protected void setReferences() {

        details_txt = (TextView) details_dialog.findViewById(R.id.details);
        share = (Button) findViewById(R.id.share);
        details = (Button) findViewById(R.id.details);
        login = (LoginButton) findViewById(R.id.login_button);
        profile = (ProfilePictureView) findViewById(R.id.picture);
        shareButton = (ShareButton) findViewById(R.id.shareButton);

    }

    @Override
    protected void init() {
        restoreCredentials();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.details:
                Log.e("FacebookIntActivity", "On click" + mAccessToken);

                if (mAccessToken != null) {
                    requestData();
                    share.setVisibility(View.VISIBLE);
                    details.setVisibility(View.VISIBLE);
                    Log.e("FacebookIntActivity", "On click inside");
                } else {
                    details_dialog.show();
                    details_dialog.setTitle("Session expire");
                    Log.e("FacebookIntActivity", "session expire");
                }


                break;
            case R.id.share:
                //  postToWall();
                sharePhotoToFacebook();
                break;
            case R.id.shareButton:
                // postPicture();
                break;
            default:
        }
    }

    private void sharePhotoToFacebook() {
        Log.e(FacebookIntActivity.class.getSimpleName(), "share photo");
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("this is caption")
                .build();


        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
        ShareDialog dialog = new ShareDialog(this);
        if (dialog.canShow(SharePhotoContent.class)) {
            LoginManager.getInstance().logInWithPublishPermissions(
                    FacebookIntActivity.this,
                    Arrays.asList("publish_actions", "user_friends", "user_birthday"));
            dialog.show(content);
        } else {
            Log.d("Activity", "you cannot share photos :(");
        }

    }


    private void postToWall() {

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription(
                            "Sample my friend")
                    .setContentUrl(Uri.parse("http://www.technoteac.co.in"))
                    .build();

            shareDialog.show(linkContent);
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean restoreCredentials() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TOKEN, "");
        if (json != null) {
            mAccessToken = gson.fromJson(json, AccessToken.class);
        }
        sharedPreferences.getString(EXPIRES, null);
        return true;
    }

    public boolean saveCredentials(AccessToken accessToken) {
        if (accessToken != null) {
            Gson gson = new Gson();
            String json = gson.toJson(accessToken); // myObject - instance of MyObject
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
            editor.putString(TOKEN, json);
            return editor.commit();
        } else return false;
    }


}

