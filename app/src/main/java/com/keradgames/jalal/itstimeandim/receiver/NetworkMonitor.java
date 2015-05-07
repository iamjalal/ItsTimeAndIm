package com.keradgames.jalal.itstimeandim.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkMonitor extends BroadcastReceiver {

    Context mContext;
    OnConnectionChangeListener mOnConnectionChangeListener;

    public NetworkMonitor(Context context) {
        mContext = context;
    }

    public void setOnConnectionChangeListener(OnConnectionChangeListener listener) {
        mOnConnectionChangeListener = listener;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if(isNetworkAvailable(context)) {
                mOnConnectionChangeListener.hasConnected();
            }
            else {
                mOnConnectionChangeListener.hasDisconnected();
            }
        }
    }

    public interface OnConnectionChangeListener {
        void hasConnected();
        void hasDisconnected();
    }
}
