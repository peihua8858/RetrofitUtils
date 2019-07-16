package com.fz.network.demo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * 对话框操作处理
 *
 * @param <T>
 */
public abstract class RSubscriber<T> implements Subscriber<T> {
    public Context context;
    public AlertDialog mAlertDialog;
    public boolean isShowDialog;
    public boolean isTouchOutSide;
    public Subscription mDis;
    public boolean isComplete;

    public RSubscriber() {
        this(null, false);
    }

    public RSubscriber(Context context) {
        this(context, true);
    }

    public RSubscriber(Context context, boolean isShowDialog) {
        this(context, isShowDialog, false);
    }

    public RSubscriber(Context context, boolean isShowDialog, boolean isTouchOutSide) {
        this.context = context;
        this.isShowDialog = isShowDialog;
        this.isTouchOutSide = isTouchOutSide;
    }

    public void showDialog() {
        try {
            if (isShowDialog) {
                showRequestDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDialog() {
        try {
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRequestDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new ProgressDialog(context);
            mAlertDialog.setCanceledOnTouchOutside(isTouchOutSide);
        }
        try {
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            appCompatActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAlertDialog.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSubscribe(@NonNull Subscription d) {
        mDis = d;
        d.request(Long.MAX_VALUE);
        try {
            if (!isComplete) {
                showDialog();
            }
        } catch (Exception e) {
            Log.e("onError", e.getMessage() + "");
        }
    }

    @Override
    public final void onNext(T t) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isDestroyed()) {
                return;
            }
        }
        success(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        onComplete();
        Log.e("onError", e.getMessage() + "");
    }

    @Override
    public void onComplete() {
        try {
            isComplete = true;
            closeDialog();
        } catch (Exception e) {
            Log.e("","onComplete:" + e.getMessage());
        }
    }

    /**
     * 成功回调，如果{@link #context}不为null，
     * 且为{@link Activity}时，当activity生命周期调用{@link Activity#onDestroy()}之后，
     * 则此方法不再回调
     *
     * @param response
     * @author dingpeihua
     * @date 2019/3/16 15:57
     * @version 1.0
     */
    protected abstract void success(T response);

}
