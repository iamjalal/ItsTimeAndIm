package com.keradgames.jalal.itstimeandim.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

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

public class TweetViewModel implements Parcelable {

    private CompositeSubscription mSubscriptions;
    private OnViewModelDataReady mCallback;

    private CacheManager mCacheManager;

    private Status mTweet;
    private Context mContext;

    public TweetViewModel() {}

    private TweetViewModel(Parcel in) {
        mTweet = (Status)in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mTweet);
    }

    public void resume(MainActivity activity, CompositeSubscription subscriptions) {
        mContext = activity;
        mCacheManager = CacheManager.getInstance(activity);
        mSubscriptions = subscriptions;
        mCallback = activity;

        Status preloadTweet = mTweet != null ? mTweet : mCacheManager.getTweet();
        mCallback.onComplete(preloadTweet);

        loadData();
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
                        mCallback.onError(e);
                    }

                    @Override
                    public void onNext(List<Status> tweets) {
                        if (tweets != null && !tweets.isEmpty()) {
                            mTweet = tweets.get(0);
                            mCallback.onComplete(mTweet);
                            mCacheManager.saveTweet(mTweet);
                        }
                    }
                }));
    }

    public static final Parcelable.Creator<TweetViewModel> CREATOR =
            new Parcelable.Creator<TweetViewModel>() {

                @Override
                public TweetViewModel createFromParcel(Parcel in) {
                    return new TweetViewModel(in);
                }

                @Override
                public TweetViewModel[] newArray(int size) {
                    return new TweetViewModel[size];
                }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
