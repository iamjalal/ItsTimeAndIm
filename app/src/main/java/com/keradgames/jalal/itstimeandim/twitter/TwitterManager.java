package com.keradgames.jalal.itstimeandim.twitter;

import com.keradgames.jalal.itstimeandim.util.TweetComparator;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {

    private static final String CONSUMER_KEY = "w3QYzGymQ5D0Um3ziQXAabxYe";
    private static final String CONSUMER_SECRET = "VyzrkII2UHrt898luG2afDye3XlM3Zc5lvhGhq2Qf5iGg7OrlV";


    public static Observable<OAuth2Token> authenticateApplication(){
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        return Observable.defer(() -> {
            try {
                return Observable.just(twitter.getOAuth2Token());
            } catch (TwitterException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static Observable<List<Status>> getTweets(OAuth2Token token) {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        twitter.setOAuth2Token(token);

        Query query = new Query();
        query.count(100);
        query.query("#MayThe4thBeWithYou today and");

        return Observable.defer(() -> {
            try {
                return Observable.just(twitter.search(query).getTweets());
            } catch (TwitterException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static List<Status> sortByTweetCount(List<Status> tweets) {
        Collections.sort(tweets, new TweetComparator());
        return tweets;
    }
}
