package com.massky.conditioningsystem.di.module;

import android.app.Activity;
import com.massky.conditioningsystem.di.scope.ActivityScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityScope
    Activity provideActivity() {
        return  mActivity;
    }
}
