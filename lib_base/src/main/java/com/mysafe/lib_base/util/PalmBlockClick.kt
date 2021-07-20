package com.mysafe.lib_base.util

import android.view.View
import java.util.*

/**
 * @author Create By 张晋铭
 * @Date on 2021/3/31
 * @Describe:
 */
abstract class PalmBlockClick : View.OnClickListener {
    private val TAG = "TAG_PalmBlockClick"
    private val MIN_CLICK_DELAY_TIME = 1500 //这里设置不能超过多长时间
    private var lastClickTime: Long = 0
    protected abstract fun onNoDoubleClick(v: View?)

    override fun onClick(v: View?) {
        val currentTime: Long = Calendar.getInstance().timeInMillis
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            onNoDoubleClick(v)
        }
    }
}