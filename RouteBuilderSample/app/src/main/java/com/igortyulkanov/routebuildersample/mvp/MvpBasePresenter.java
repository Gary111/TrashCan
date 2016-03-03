package com.igortyulkanov.routebuildersample.mvp;

import java.lang.ref.WeakReference;

public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private WeakReference<V> viewReference;

    public static boolean isViewAttached(MvpView mvpView) {
        return mvpView != null;
    }

    @Override
    public void attachView(V view) {
        viewReference = new WeakReference<>(view);
    }

    public V getView() {
        return viewReference == null ? null : viewReference.get();
    }

    @Override
    public void detachView(boolean retainInstance) {
        if (viewReference != null) {
            viewReference.clear();
            viewReference = null;
        }
    }
}