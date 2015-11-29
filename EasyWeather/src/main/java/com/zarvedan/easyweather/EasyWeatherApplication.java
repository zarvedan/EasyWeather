package com.zarvedan.easyweather;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by andre on 28/11/15.
 */
public class EasyWeatherApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}


