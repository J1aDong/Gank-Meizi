package com.j1adong.meizi.rx.rxandroid;

import rx.Observer;

public class SimpleObserver<T> implements Observer<T> {
  @Override public void onCompleted() {
  }

  @Override public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override public void onNext(T o) {

  }
}
