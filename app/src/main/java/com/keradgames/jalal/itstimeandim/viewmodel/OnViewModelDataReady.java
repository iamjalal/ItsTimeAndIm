package com.keradgames.jalal.itstimeandim.viewmodel;


import twitter4j.Status;

public interface OnViewModelDataReady {
    void onComplete(Status tweet);
    void onError(Throwable e);
}
