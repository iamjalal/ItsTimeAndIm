package com.keradgames.jalal.itstimeandim.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keradgames.jalal.itstimeandim.R;
import com.keradgames.jalal.itstimeandim.twitter.TwitterManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import rx.Subscriber;
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
                .map(tweets -> TwitterManager.sortByTweetCount(tweets)) // TODO: Check if map is done in worker thread. Probably not.
                .subscribe(new Subscriber<List<Status>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "Ooops! This is embarrassing: " +
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(List<Status> tweets) {
                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        for (Status tweet : tweets) {
                            TextView text = new TextView(MainActivity.this);
                            int retweet = tweet.getRetweetCount();
                            String date = format.format(tweet.getCreatedAt());
                            text.setText("RT: " + retweet + " | Date: " + date +" | " + tweet.getText());
                            container.addView(text);
                        }
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }
}
