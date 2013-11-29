package com.example.transitlogger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class AppStatus {
    public static boolean isOnline(Context context) {
        try {
        	ConnectivityManager connectivityManager = (ConnectivityManager)
            		context.getSystemService(Context.CONNECTIVITY_SERVICE);
        	
	        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	        return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return false;
    }
}