package com.fz.network.demo;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class DataBindingBaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected T mBinding;

    @Override
    public void setContentView(int layoutResID) {
        mBinding = DataBindingUtil.setContentView(this, layoutResID);
        super.setContentView(mBinding.getRoot());
    }

    @Override
    public void setContentView(View view) {
        mBinding = DataBindingUtil.bind(view);
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mBinding = DataBindingUtil.bind(view);
        super.setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        mBinding = DataBindingUtil.bind(view);
        super.addContentView(view, params);
    }
}
