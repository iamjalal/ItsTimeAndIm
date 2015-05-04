package com.keradgames.jalal.itstimeandim.util;


import java.util.Comparator;

import twitter4j.Status;

public class TweetComparator implements Comparator<Status> {

    @Override
    public int compare(Status x, Status y) {
        int comparison = TweetComparator.compare(x.getRetweetCount(), y.getRetweetCount()) * -1;
        return comparison != 0 ? comparison :
                TweetComparator.compare(x.getCreatedAt().getTime(), y.getCreatedAt().getTime()) * -1;
    }

    private static int compare(int a, int b) {
        return a < b ? -1 : a > b ? 1 : 0;
    }

    private static int compare(long a, long b) {
        return a < b ? -1 : a > b ? 1 : 0;
    }
}
