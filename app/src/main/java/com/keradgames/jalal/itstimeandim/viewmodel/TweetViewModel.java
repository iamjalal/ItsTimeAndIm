package com.keradgames.jalal.itstimeandim.viewmodel;

import android.app.Activity;
import android.content.Context;

import com.keradgames.jalal.itstimeandim.activity.MainActivity;
import com.keradgames.jalal.itstimeandim.twitter.TwitterManager;
import com.keradgames.jalal.itstimeandim.util.CacheManager;

import java.util.List;

import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import twitter4j.Status;

public class TweetViewModel {

    private CompositeSubscription mSubscriptions;
    private OnViewModelDataReady mCallback;

    private CacheManager mCacheManager;
    private Context mContext;

    public TweetViewModel(MainActivity activity, CompositeSubscription subscriptions) {
        mContext = activity;
        mCacheManager = CacheManager.getInstance(activity);
        mSubscriptions = subscriptions;
        mCallback = activity;

        Status preloadTweet = mCacheManager.getTweet();
        if(preloadTweet != null) {
            mCallback.onDataReady(preloadTweet);
        }
    }

    public void loadData() {

        TwitterManager twitter = TwitterManager.getInstance(mContext);
        mSubscriptions.add(
                AppObservable.bindActivity((Activity) mContext, twitter.authenticateApplication())
                .subscribeOn(Schedulers.io())
                .flatMap(token -> twitter.getTweets(token).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .map(tweets -> twitter.sortTweets(tweets))
                .subscribe(new Subscriber<List<Status>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCallback.onError();
                    }

                    @Override
                    public void onNext(List<Status> tweets) {
                        if (tweets != null && !tweets.isEmpty()) {
                            Status tweet = tweets.get(0);
                            mCallback.onDataReady(tweet);
                            mCacheManager.saveTweet(tweet);
                        }
                        else {
                            mCallback.onNoData();
                        }
                    }
                }));
    }
}
