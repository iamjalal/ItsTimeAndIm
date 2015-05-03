package com.keradgames.jalal.itstimeandim.twitter;

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
    private static final String CONSUMER_SECRET = "Add your consumer secret here";


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
        query.query("\"It's 12:00 and I'm\"");

        return Observable.defer(() -> {
            try {
                return Observable.just(twitter.search(query).getTweets());
            } catch (TwitterException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
