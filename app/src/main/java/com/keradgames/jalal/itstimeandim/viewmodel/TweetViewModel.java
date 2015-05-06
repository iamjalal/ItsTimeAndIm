package com.keradgames.jalal.itstimeandim.viewmodel;

import android.app.Activity;
import android.content.Context;

import com.keradgames.jalal.itstimeandim.activity.MainActivity;
import com.keradgames.jalal.itstimeandim.twitter.TwitterManager;

import java.util.List;

import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import twitter4j.Status;

public class TweetViewModel {

    private Context mContext;
    private CompositeSubscription mCompositeSubscription;
    private OnViewModelDataReady mCallback;

    public TweetViewModel(MainActivity activity, CompositeSubscription compositeSubscription) {
        mContext = activity;
        mCompositeSubscription = compositeSubscription;
        mCallback = activity;
    }

    public void loadData() {
        TwitterManager twitter = TwitterManager.getInstance(mContext);
        mCompositeSubscription.add(AppObservable.bindActivity((Activity) mContext, twitter.authenticateApplication())
                .subscribeOn(Schedulers.io())
                .flatMap(token -> twitter.getTweets(token).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .map(tweets -> twitter.sortByTweetCount(tweets))
                .subscribe(new Subscriber<List<Status>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCallback.onError(e);
                    }

                    @Override
                    public void onNext(List<Status> tweets) {
                        mCallback.onComplete(tweets.get(0));
                    }
                }));
    }
}
