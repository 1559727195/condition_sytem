package com.massky.conditioningsystem.di.component;

import android.content.Context;
import com.massky.conditioningsystem.di.module.AppModule;
import javax.inject.Singleton;
import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    Context provideContext();

}
