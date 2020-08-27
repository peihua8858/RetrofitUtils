/*
 * Copyright (C) 2016 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.adapter.rxjava2;

import com.fz.network.cache.ICacheResponse;
import com.fz.network.utils.NetworkUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Call;
import retrofit2.Response;

import com.fz.network.cache.HttpCacheManager;

/**
 * app缓存处理，如果请求参数中有参数isOpenCache为true，则会先读取缓存数据，再请求网络
 * 即有两次数据返回，第一次是缓存数据，第二次为网络接口数据。
 * 判断是否是缓存数据，则使用{@link ICacheResponse#isCacheData}，
 * 如果为true表示当前返回为缓存数据，否则是网络接口数据
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/1/24 13:39
 */
final class CallExecuteObservable<T> extends Observable<Response<T>> {
    private final Call<T> originalCall;

    CallExecuteObservable(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    protected void subscribeActual(Observer<? super Response<T>> observer) {
        // Since Call is a one-shot type, clone it for each new observer.
        Call<T> call = originalCall.clone();
        CallDisposable disposable = new CallDisposable(call);
        observer.onSubscribe(disposable);
        if (disposable.isDisposed()) {
            return;
        }
        Call<T> call2 = originalCall.clone();
        Response<T> response = HttpCacheManager.instance().get(call2);
        if (NetworkUtil.isConnected(true)) {
            if (response != null) {
                if (!disposable.isDisposed()) {
                    observer.onNext(response);
                }
            }
            boolean terminated = false;
            try {
                response = call.execute();
                HttpCacheManager.instance().put(call, response);
                if (!disposable.isDisposed()) {
                    observer.onNext(response);
                }
                if (!disposable.isDisposed()) {
                    terminated = true;
                    observer.onComplete();
                }
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                if (terminated) {
                    RxJavaPlugins.onError(t);
                } else if (!disposable.isDisposed()) {
                    try {
                        observer.onError(t);
                    } catch (Throwable inner) {
                        Exceptions.throwIfFatal(inner);
                        RxJavaPlugins.onError(new CompositeException(t, inner));
                    }
                }
            }
        } else {
            if (response != null && !disposable.isDisposed()) {
                observer.onNext(response);
            } else {
                if (!disposable.isDisposed()) {
                    Exception t = new Exception(getMessage());
                    try {
                        observer.onError(t);
                    } catch (Throwable inner) {
                        Exceptions.throwIfFatal(inner);
                        RxJavaPlugins.onError(new CompositeException(t, inner));
                    }
                }
            }
        }
    }

    private static String getMessage() {
        return "{\"statusCode\":-101,\"message\":\"\"}";
    }

    private static final class CallDisposable implements Disposable {
        private final Call<?> call;
        private volatile boolean disposed;

        CallDisposable(Call<?> call) {
            this.call = call;
        }

        @Override
        public void dispose() {
            disposed = true;
            call.cancel();
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}
