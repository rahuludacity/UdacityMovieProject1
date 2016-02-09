package com.appin.udacitymovieproject1;

import android.app.Application;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MySingelton.getInstance();

    }
}
