package com.fz.network;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction;
import com.uber.autodispose.lifecycle.LifecycleEndedException;

import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
                        return Lifecycle.Event.ON_DESTROY;
                    case ON_START:
                        return Lifecycle.Event.ON_DESTROY;
                    case ON_RESUME:
                        return Lifecycle.Event.ON_DESTROY;
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
