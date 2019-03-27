package com.massky.conditioningsystem.di.component;

import android.app.Activity;

import com.massky.conditioningsystem.activity.HomeActivity;
import com.massky.conditioningsystem.di.module.ActivityModule;
import com.massky.conditioningsystem.di.module.EntityModule;
import com.massky.conditioningsystem.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class,modules = {ActivityModule.class, EntityModule.class})
public interface ActivityComponent {
    Activity getActivity();
    void inject(HomeActivity homeActivity);
}
