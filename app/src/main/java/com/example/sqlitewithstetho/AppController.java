package com.example.sqlitewithstetho;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Anurag on 18/7/19.
 */
public class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
