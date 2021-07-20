package com.mysafe.lib_base.base;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mysafe.lib_base.util.TypeUtil;


/**
 * Create By 张晋铭
 * on 2020/11/2
 * Describe:
 */
public class BaseViewModel<T> extends AndroidViewModel {
    public T mRepository;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mRepository = TypeUtil.getNewInstance(this, 0);
    }

    public Activity getTopActivity(){
        return ActivityManager.Instance().getTopActivity();
    }
}
