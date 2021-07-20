package com.mysafe.lib_base.util

import androidx.lifecycle.MutableLiveData

/**
 * @author Create By 张晋铭
 * @Date on 2021/6/16
 * @Describe:
 */
object LiveDataEvent {
    val strLive by lazy { MutableLiveData<String>() }
    val faceIdLive by lazy { MutableLiveData<String>() }
    val mealContentLive by lazy { MutableLiveData<String>() }

    val finishLive by lazy { UnFlowLiveData<String>() }
}
