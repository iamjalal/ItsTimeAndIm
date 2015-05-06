package com.keradgames.jalal.itstimeandim.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keradgames.jalal.itstimeandim.R;
import com.keradgames.jalal.itstimeandim.viewmodel.OnViewModelDataReady;
import com.keradgames.jalal.itstimeandim.viewmodel.TweetViewModel;

import rx.subscriptions.CompositeSubscription;
import twitter4j.Status;

public class MainActivity extends Activity implements OnViewModelDataReady {

    private static final String SAVED_TWEET = "tweet";

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private TweetViewModel mViewModel;

    private ViewGroup mContainer;

    private Status mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if(savedInstanceState != null) {
            mTweet = (Status)savedInstanceState.getSerializable(SAVED_TWEET);
        }

        mContainer = (LinearLayout)findViewById(R.id.container);
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
        TextView text = new TextView(this);
        text.setText(tweet.getText());
        mContainer.addView(text);
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
