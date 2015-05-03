package com.keradgames.jalal.itstimeandim.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keradgames.jalal.itstimeandim.R;
import com.keradgames.jalal.itstimeandim.twitter.TwitterManager;

import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import twitter4j.Status;

public class MainActivity extends Activity {

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final LinearLayout container = (LinearLayout)findViewById(R.id.container);

        mCompositeSubscription.add(AppObservable.bindActivity(this, TwitterManager.authenticateApplication())
                .subscribeOn(Schedulers.io())
                .flatMap(token -> TwitterManager.getTweets(token).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(tweets -> {
                    for (Status tweet : tweets) {
                        TextView text = new TextView(this);
                        text.setText(tweet.getText());
                        container.addView(text);
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }
}
