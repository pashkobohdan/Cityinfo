package com.pashkobohdan.cityinfo;

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

}
