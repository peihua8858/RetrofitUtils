package com.fz.network;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;


import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import autodispose2.lifecycle.CorrespondingEventsFunction;
import autodispose2.lifecycle.LifecycleEndedException;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * RxJava生命周期处理
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/6/13 14:53
 */
public final class RxLifecycleUtil {
    private static final CorrespondingEventsFunction<Lifecycle.Event> DEFAULT_CORRESPONDING_EVENTS =
            lastEvent -> {
                switch (lastEvent) {
                    case ON_CREATE:
                    case ON_START:
                    case ON_RESUME:
                    case ON_PAUSE:
                        return Lifecycle.Event.ON_DESTROY;
                    case ON_STOP:
                    case ON_DESTROY:
                    default:
                        throw new LifecycleEndedException("Lifecycle has ended! Last event was " + lastEvent);
                }
            };

    /**
     * RxJava 处理生命周期
     *
     * @param lifecycleOwner
     * @author dingpeihua
     * @date 2019/6/13 14:55
     * @version 1.0
     */
    public static <T> AutoDisposeConverter<T> bindLifecycle(LifecycleOwner lifecycleOwner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner, DEFAULT_CORRESPONDING_EVENTS));
    }

    /**
     * 统一线程切换处理，配合Rxjava compose 操作符
     * 如： .compose(RxLifecycleUtil.applySchedulers())
     *
     * @author dingpeihua
     * @date 2019/7/9 15:25
     * @version 1.0
     */
    public static <T> FlowableTransformer<T, T> applySchedulers() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
