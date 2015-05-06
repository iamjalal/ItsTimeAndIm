package com.keradgames.jalal.itstimeandim.util;


import android.content.Context;

import com.keradgames.jalal.itstimeandim.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class QueryBuilder {

    private static final String[] TIME_FORMATS = {
            "kk:mm", "hh:mm", "hh:mma", "hh:mm a"
    };

    private static String getFormattedTime(String format) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        DateTime date = DateTime.now();
        String time = formatter.print(date);
        return time;
    }

    private static String getFormattedTweet(String tweet, String time) {
        return new StringBuilder().append("\"").append(String.format(tweet, time)).append("\"").toString();
    }

    public static String getQuery(Context context) {
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < TIME_FORMATS.length; i++) {
            //Remove leading zeros as people rarely use them, e.g: 03:45 -> 3:45.
            String trimmedTime = getFormattedTime(TIME_FORMATS[i]).replaceFirst("^0+(?!$)", "");

            query.append(getFormattedTweet(context.getResources().getString(R.string.timeFormat1), trimmedTime)).append("OR");
            query.append(getFormattedTweet(context.getResources().getString(R.string.timeFormat2), trimmedTime));

            if (i < TIME_FORMATS.length - 1) {
                query.append("OR");
            }
        }

        return query.toString();
    }
}
