package com.mysafe.msmealorder_public

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.mysafe.lib_base.base.ActivityManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create By 张晋铭
 * on 2020/12/2
 * Describe:
 */

fun View.setRoundRectBg(color: Int = Color.WHITE, cornerRadius: Int = 15) {
    background = GradientDrawable().apply {
        setColor(color)
        setCornerRadius(cornerRadius.toFloat())
    }
}

/**
 * 获取当前时间
 */
fun getNowTime(): String {
    val calendar = Calendar.getInstance()
    val dateFormat1 = SimpleDateFormat("yyyy-MM-dd")
    return dateFormat1.format(calendar.time)
}

fun getNowDetailTime(): String {
    val calendar = Calendar.getInstance()
    val dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return dateFormat1.format(calendar.time)
}

/**
 * 获取昨天日期
 */
fun getLastDay(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, -1)
    val dateFormat1 = SimpleDateFormat("yyyy-MM-dd")
    return dateFormat1.format(calendar.time)
}


/**
 * 自定义Toast
 */
fun toast(msg: String? = "") {
    if(ActivityManager.Instance().activitys>0){
        Toast.makeText(ActivityManager.Instance().topActivity, msg, Toast.LENGTH_SHORT).show()
    }
}

/**
 * 自定义Toast
 */
fun Context.toasts(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

/**
 * 自定义StartActivity
 * @param cls 即将跳转到的界面
 * @param context 当前Activity （默认获取最顶层的Activity）
 */
fun <T> toActivity(cls: Class<T>,
                   context: Context = ActivityManager.Instance().topActivity) {
    context.startActivity(Intent(context, cls))
}

fun <T> Activity.toActivity(cls: Class<T>) {
    this.startActivity(Intent(this, cls))
}

fun <T> Activity.toActivity(cls: Class<T>, bundle: Bundle) {
    this.startActivity(Intent(this, cls).putExtras(bundle))
}

/**
 * 自定义StartActivity
 * @param cls 即将跳转到的界面
 * @param context 当前Activity （默认获取最顶层的Activity）
 */
fun <T> toActivity(cls: Class<T>,
                   bundle: Bundle,
                   context: Context = ActivityManager.Instance().topActivity) {

    context.startActivity(Intent(context, cls).putExtras(bundle))
}

/**
 *
 */

/**
 * 官方dp和px转换：Android Convert Dp to Pixel - dpToPx (Kotlin)
 */
fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

fun Int.pxToDp(displayMetrics: DisplayMetrics): Int = (this / displayMetrics.density).toInt()

fun Int.dpToPx(context: Context = ActivityManager.Instance().topActivity): Float =
        this * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).toFloat()
