package com.keradgames.jalal.itstimeandim.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.keradgames.jalal.itstimeandim.R;
import com.keradgames.jalal.itstimeandim.receiver.NetworkMonitor;
import com.keradgames.jalal.itstimeandim.util.RoundedTransformation;
import com.keradgames.jalal.itstimeandim.viewmodel.OnViewModelDataReady;
import com.keradgames.jalal.itstimeandim.viewmodel.TweetViewModel;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.subscriptions.CompositeSubscription;
import twitter4j.Status;

public class MainActivity extends Activity implements OnViewModelDataReady,
        NetworkMonitor.OnConnectionChangeListener {

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private TweetViewModel mViewModel;

    private TickReceiver mTickReceiver;
    private NetworkMonitor mNetworkMonitor;

    @InjectView(R.id.tweet)
    public View tweetContainer;

    @InjectView(R.id.profile_pic)
    public ImageView profilePic;

    @InjectView(R.id.profile_name)
    public TextView profileName;

    @InjectView(R.id.twitter_name)
    public TextView twitterName;

    @InjectView(R.id.tweet_text)
    public TextView tweetText;

    @InjectView(R.id.tweet_time)
    public TextView tweetTime;

    @InjectView(R.id.retweet_count)
    public TextView retweetCount;

    private class TickReceiver extends BroadcastReceiver {

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
        ButterKnife.inject(this);

        mViewModel = new TweetViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerTimeTick();
        registerNetworkMonitor();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.resume(this, mCompositeSubscription);
    }

    @Override
    public void onDataReady(Status tweet) {

        if(tweet == null) {
            return;
        }

        loadImage(profilePic, tweet.getUser().getOriginalProfileImageURL());

        profileName.setText(tweet.getUser().getName());
        twitterName.setText("@" + tweet.getUser().getScreenName());
        tweetText.setText(tweet.getText());
        tweetTime.setText(getTweetTime(tweet.getCreatedAt().getTime()));
        retweetCount.setText(getRetweetText(tweet.getRetweetCount()));

        tweetContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError() {

        loadImage(profilePic, R.drawable.profile_pic);

        Resources res = getResources();
        profileName.setText(res.getText(R.string.profile_name_error));
        twitterName.setText("@" + res.getText(R.string.twitter_name_error));
        tweetText.setText(res.getText(R.string.text_error));
        tweetTime.setText(res.getText(R.string.time_error));
        retweetCount.setText(getRetweetText(0));

        tweetContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoData() {

        loadImage(profilePic, R.drawable.kerad_profile);

        Resources res = getResources();
        profileName.setText(res.getText(R.string.profile_name_no_tweet));
        twitterName.setText("@" + res.getText(R.string.twitter_name_no_tweet));
        tweetText.setText(res.getText(R.string.text_no_tweet));
        tweetTime.setText(res.getText(R.string.time_no_tweet));
        retweetCount.setText(getRetweetText(23));

        tweetContainer.setVisibility(View.VISIBLE);
    }

    private void loadImage(ImageView view, String url) {
        int size = getResources().getDimensionPixelSize(R.dimen.imageSize);
        Picasso.with(this).load(url).transform(new RoundedTransformation(10,0))
                .resize(size, size).placeholder(R.drawable.image_placeholder).into(view);
    }

    private void loadImage(ImageView view, int id) {
        int size = getResources().getDimensionPixelSize(R.dimen.imageSize);
        Picasso.with(this).load(id).transform(new RoundedTransformation(10,0))
                .resize(size, size).placeholder(R.drawable.image_placeholder).into(view);
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
    public void hasConnected() {
        findViewById(R.id.disconnected).setVisibility(View.GONE);
        mViewModel.loadData();
    }

    @Override
    public void hasDisconnected() {
        findViewById(R.id.disconnected).setVisibility(View.VISIBLE);
        mCompositeSubscription.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterTimeTick();
        unregisterNetworkMonitor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    private void registerNetworkMonitor() {
        mNetworkMonitor = new NetworkMonitor(this);
        mNetworkMonitor.setOnConnectionChangeListener(this);
        registerReceiver(mNetworkMonitor, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    private void unregisterNetworkMonitor() {
        unregisterReceiver(mNetworkMonitor);
    }

    private void registerTimeTick() {
        mTickReceiver = new TickReceiver();
        registerReceiver(mTickReceiver, new IntentFilter("android.intent.action.TIME_TICK"));
    }

    private void unregisterTimeTick() {
        unregisterReceiver(mTickReceiver);
    }
}