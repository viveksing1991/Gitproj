package com.example.root.testproject.ui.activities.main;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;

import android.widget.Toast;

import com.example.root.testproject.R;
import com.example.root.testproject.ui.activities.base.navigationaction.BaseActivity;

import com.example.root.testproject.ui.activities.facebook.FacebookIntActivity;
import com.example.root.testproject.ui.activities.profiletwitter.ProfileActivityTwi;
import com.facebook.FacebookSdk;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterRateLimit;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "d8eShj3HgvjAi84GgOhBoZup8";
    private static final String TWITTER_SECRET = "qOpe2q1nYKa4z875GFLaNhn7uRzq8HaEwlvnsRrRHnYbmQcHiB";


    private ViewPagerIndicator mViewPagerIndicator;
    private ViewPager mViewPager;

    private Button tvFacebookLogin;
    TwitterLoginButton twitterLoginButton;

    String userName;
    public static  String KEY_USERNAME="username";
   public static  String KEY_PROFILE_IMAGE_URL = "https://www.google.co.in/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=0ahUKEwiDtMei08TTAhUfSI8KHZUDAr8QjBwIBA&url=https%3A%2F%2Fwww.w3schools.com%2Fcss%2Ftrolltunga.jpg&psig=AFQjCNEay2FDVnUL8OUESW3RdUiBkL-bQw&ust=1493382975123675";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        setReferences();
        setListeners();
        setAdaptor();
        twitterCallback();

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                login(result);

                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                Call<User> userCall = twitterApiClient.getAccountService().verifyCredentials(true, true);
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        //If it succeeds creating a User object from userResult.data
                        User user = result.data;
                        //Getting the profile image url
                        String profileImage = user.profileImageUrl.replace("_normal", "");

                        //Creating an Intent
                        Intent intent = new Intent(MainActivity.this, ProfileActivityTwi.class);

                        //Adding the values to intent
                        intent.putExtra(KEY_USERNAME,userName);
                        intent.putExtra(KEY_PROFILE_IMAGE_URL, profileImage);

                        //Starting intent
                        startActivity(intent);
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });
            }

            private void login(Result<TwitterSession> result) {
                TwitterSession twitterSession = result.data;
                TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                 userName = twitterSession.getUserName();
                Log.e("Main", userName);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    private void twitterCallback() {


    }


    /*This method is used to set the adaptor*/
    private void setAdaptor() {
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPagerIndicator.setupWithViewPager(mViewPager);
        mViewPagerIndicator.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    protected void setListeners() {
        tvFacebookLogin.setOnClickListener(this);
    }

    @Override
    protected void setReferences() {
        mViewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.view_pager_indicator);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        tvFacebookLogin = (Button) findViewById(R.id.tvFacebookLogin);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitterLogin);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvFacebookLogin:
                startActivity(new Intent(MainActivity.this, FacebookIntActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    /*Class works as Adaptor for View pager  */
    class MyPagerAdapter extends PagerAdapter {

        Integer[] integers = new Integer[]{R.drawable.email_icon, R.drawable.ic_call_black_24dp, R.drawable.img};

        @Override
        public int getCount() {
            return integers.length;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource(integers[position]);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return String.valueOf(position);
        }
    }

    @NonNull
    private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            Toast.makeText(MainActivity.this, "Page selected " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageScrollStateChanged(final int state) {

        }
    };


}
