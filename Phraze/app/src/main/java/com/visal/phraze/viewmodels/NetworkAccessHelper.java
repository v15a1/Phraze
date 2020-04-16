package com.visal.phraze.viewmodels;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//class and method to check if a network is connected or not to the device
public class NetworkAccessHelper {
    private Context context;
    public NetworkAccessHelper(Context context) {
        this.context = context;
    }

    //method to Access HAL to check if network is available
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
