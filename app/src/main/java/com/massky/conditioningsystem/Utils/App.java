package com.massky.conditioningsystem.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.massky.conditioningsystem.di.component.AppComponent;
import com.massky.conditioningsystem.di.component.DaggerAppComponent;
import com.massky.conditioningsystem.di.module.AppModule;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;



/**
 * Created by masskywcy on 2017-01-04.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks {


    private Context context;
    public String calledAcccout;
    private static App _instance;
    /**
     * 当前Acitity个数
     */
    private int activityAount = 0;

    // 开放平台申请的APP key & secret key
    public static String APP_KEY = "ccd38858cc5a459bbeedcf93a25ae6be";
    public static String API_URL = "https://open.ys7.com";
    public static String WEB_URL = "https://auth.ys7.com";
    private boolean isForeground;
    private boolean isDoflag;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
//        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志

//        CrashHandlerUtil.getInstance().init_crash(_instance);

        //okhttp网络配置
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //.addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

        //application生命周期
        this.registerActivityLifecycleCallbacks(this);//注册


    }

    /**
     * @return
     */
    public static App getInstance() {
        return _instance;
    }


    public AppComponent mAppComponent;


    public AppComponent getAppComponent() {
        if (mAppComponent == null) {
            mAppComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(_instance))
                    .build();
        }
        return mAppComponent;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }


    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
//		ToastUtils.getInstances().cancel();// activity死的时候，onActivityPaused(Activity activity)
        //ToastUtils.getInstances().cancel();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

}
