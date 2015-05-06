package com.keradgames.jalal.itstimeandim.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keradgames.jalal.itstimeandim.R;
import com.keradgames.jalal.itstimeandim.util.RoundedTransformation;
import com.keradgames.jalal.itstimeandim.viewmodel.OnViewModelDataReady;
import com.keradgames.jalal.itstimeandim.viewmodel.TweetViewModel;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import rx.subscriptions.CompositeSubscription;
import twitter4j.Status;

public class MainActivity extends Activity implements OnViewModelDataReady {

    private static final String SAVED_TWEET = "tweet";

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private TweetViewModel mViewModel;

    private Status mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if(savedInstanceState != null) {
            mTweet = (Status)savedInstanceState.getSerializable(SAVED_TWEET);
        }

        mViewModel = new TweetViewModel(this, mCompositeSubscription);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mTweet == null) {
            mViewModel.loadData();
        }
        else {
            onComplete(mTweet);
        }
    }

    @Override
    public void onComplete(Status tweet) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_TWEET, mTweet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }
}
