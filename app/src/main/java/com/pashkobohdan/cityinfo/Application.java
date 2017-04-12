package com.pashkobohdan.cityinfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pashkobohdan.cityinfo.data.ormLite.HelperFactory;

/**
 * Created by bohdan on 10.04.17.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HelperFactory.setHelper(getApplicationContext());

    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();

        super.onTerminate();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
