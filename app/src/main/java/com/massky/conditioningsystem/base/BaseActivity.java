package com.massky.conditioningsystem.base;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.massky.conditioningsystem.Utils.App;
import com.massky.conditioningsystem.Utils.AppManager;
import com.massky.conditioningsystem.di.component.ActivityComponent;
import com.massky.conditioningsystem.di.component.DaggerActivityComponent;
import com.massky.conditioningsystem.di.module.ActivityModule;
import com.massky.conditioningsystem.di.module.EntityModule;
import javax.inject.Inject;
import butterknife.ButterKnife;

/**
 * Created by zhu on 2017/7/18.
 */

public abstract class BaseActivity<T extends IPresenter> extends AppCompatActivity implements View.OnClickListener,IView {
    public static boolean isForegrounds = false;
    public static boolean isDestroy = false;
    public Bundle savedInstanceState;
    private static final String TAG = "BaseActivity";

    @Inject
    protected T mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(viewId());
        this.savedInstanceState = savedInstanceState;
        AppManager.getAppManager().addActivity(this);//添加activity
        ButterKnife.inject(this);
        isDestroy = false;
        onPrepare();
        onView();
        onEvent();
        onData();
    }

    protected ActivityComponent getActivityComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(App.getInstance().getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }

    protected ActivityComponent getActivityComponent(EntityModule entityModule) {
        return DaggerActivityComponent.builder()
                .appComponent(App.getInstance().getAppComponent())
                .activityModule(getActivityModule())
                .entityModule(entityModule)
                .build();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }


    @SuppressWarnings("unchecked")
    private void onPrepare() {
        initInject();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    /**
     * 注入
     */
    protected void initInject() {
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 手势密码两种状态（点击home键和手机屏幕状态进行判定）
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        isForegrounds = false;
        super.onPause();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.onTouchEvent(event);
    }

    protected abstract int viewId();

    @Override
    protected void onResume() {
        isForegrounds = true;
        super.onResume();
    }

    protected abstract void onView();

    protected abstract void onEvent();

    protected abstract void onData();

    /**
     * 取消广播状态
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

}
