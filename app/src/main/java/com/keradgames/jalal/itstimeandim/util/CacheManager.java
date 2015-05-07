package com.keradgames.jalal.itstimeandim.util;


import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import twitter4j.Status;

public class CacheManager {

    private File mCacheFile;

    private CacheManager(Context context) {
        mCacheFile = new File(context.getCacheDir(), "cache_file");
    }

    public static final CacheManager getInstance(Context context) {
        return new CacheManager(context);
    }

    public void saveTweet(Status tweet) {

        try {
            FileOutputStream fos = new FileOutputStream(mCacheFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tweet);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Status getTweet() {

        Status tweet = null;
        try {

            if(!mCacheFile.exists()) {
                return null;
            }

            FileInputStream fis = new FileInputStream(mCacheFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            tweet = (Status)ois.readObject();

            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return tweet;
    }
}
