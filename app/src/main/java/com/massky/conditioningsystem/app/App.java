package com.massky.conditioningsystem.app;

import android.app.Application;

public class App extends Application {
    private static App _instance;
    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
    }
}
