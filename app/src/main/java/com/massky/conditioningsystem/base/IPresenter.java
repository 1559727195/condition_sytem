package com.massky.conditioningsystem.base;

public interface IPresenter<T extends IView> {
    void attachView(T view);
    void detachView();
}
