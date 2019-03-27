package com.massky.conditioningsystem.di.module;


import android.content.Context;


import com.massky.conditioningsystem.Utils.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }


}
