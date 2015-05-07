package com.keradgames.jalal.itstimeandim.viewmodel;


import twitter4j.Status;

public interface OnViewModelDataReady {
    void onDataReady(Status tweet);
    void onError();
    void onNoData();
}
