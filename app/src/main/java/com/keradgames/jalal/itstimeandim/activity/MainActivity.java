package com.keradgames.jalal.itstimeandim.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keradgames.jalal.itstimeandim.R;
import com.keradgames.jalal.itstimeandim.util.NetworkMonitor;
import com.keradgames.jalal.itstimeandim.util.RoundedTransformation;
import com.keradgames.jalal.itstimeandim.viewmodel.OnViewModelDataReady;
import com.keradgames.jalal.itstimeandim.viewmodel.TweetViewModel;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import rx.subscriptions.CompositeSubscription;
import twitter4j.Status;

public class MainActivity extends Activity implements OnViewModelDataReady, NetworkMonitor.OnConnectionChangeListener {

    private static final String SAVED_VIEW_MODEL = "viewmodel";

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private TweetViewModel mViewModel;

    private Status mTweet;
    private RefreshReceiver mRefreshReceiver;
    private NetworkMonitor mNetworkMonitor;

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(mNetworkMonitor.isNetworkAvailable(context)) {
                mViewModel.loadData();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if(savedInstanceState != null) {
            mViewModel = savedInstanceState.getParcelable(SAVED_VIEW_MODEL);
        }
        else {
            mViewModel = new TweetViewModel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerRefresh();
        registerNetworkMonitor();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.resume(this, mCompositeSubscription);
    }

    @Override
    public void onComplete(Status tweet) {

        if(tweet == null) {
            return;
        }

        mTweet = tweet;

        ImageView profilePic = (ImageView)findViewById(R.id.profile_pic);
        loadImage(profilePic);

        ((TextView)findViewById(R.id.profile_name)).setText(mTweet.getUser().getName());
        ((TextView)findViewById(R.id.twitter_name)).setText("@" + mTweet.getUser().getScreenName());
        ((TextView)findViewById(R.id.tweet_text)).setText(mTweet.getText());
        ((TextView)findViewById(R.id.tweet_time)).setText(getTweetTime(mTweet.getCreatedAt().getTime()));
        ((TextView)findViewById(R.id.retweet_count)).setText(getRetweetText(mTweet.getRetweetCount()));
    }

    private void loadImage(ImageView view) {
        int size = getResources().getDimensionPixelSize(R.dimen.imageSize);
        Picasso.with(this).load(mTweet.getUser().getOriginalProfileImageURL())
                .transform(new RoundedTransformation(10,0))
                .resize(size, size)
                .placeholder(R.drawable.image_placeholder)
                .into(view);
    }

    private String getTweetTime(long tweetMillis) {
        String format = "hh:mm a - dd MMM yyyy";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        DateTime date = new DateTime(tweetMillis);
        return formatter.print(date);
    }

    private String getRetweetText(int numRetweets) {
        return  getResources().getQuantityString(R.plurals.retweet_sufix, numRetweets, numRetweets);
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(this, "Ooops! This is embarrassing: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void hasConnected() {
        findViewById(R.id.disconnected).setVisibility(View.GONE);
        mViewModel.loadData();
    }

    @Override
    public void hasDisconnected() {
        findViewById(R.id.disconnected).setVisibility(View.VISIBLE);
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterRefresh();
        unregisterNetworkMonitor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_VIEW_MODEL, mViewModel);
    }

    private void registerNetworkMonitor() {
        mNetworkMonitor = new NetworkMonitor(this);
        mNetworkMonitor.setOnConnectionChangeListener(this);
        registerReceiver(mNetworkMonitor, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    private void unregisterNetworkMonitor() {
        unregisterReceiver(mNetworkMonitor);
    }

    private void registerRefresh() {
        mRefreshReceiver = new RefreshReceiver();
        registerReceiver(mRefreshReceiver, new IntentFilter("android.intent.action.TIME_TICK"));
    }

    private void unregisterRefresh() {
        unregisterReceiver(mRefreshReceiver);
    }
}