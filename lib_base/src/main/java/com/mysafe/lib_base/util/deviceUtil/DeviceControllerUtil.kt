package com.mysafe.lib_base.util.deviceUtil

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log

/**
 * @author Create By 张晋铭
 * @Date on 2021/4/20
 * @Describe:
 */
object DeviceControllerUtil {
    private val TAG = "TAG_DeviceController"

    /**
     * 清除缓存
     */
    fun clearAppData(packageName:String){
        Runtime.getRuntime().exec("su")
        Log.i(TAG, "clearAppData: $packageName")
        Runtime.getRuntime().exec("pm clear $packageName")
    }

    /**
     * 重启设备
     */
    fun restartDevice(){
        Runtime.getRuntime().exec("su")
        Runtime.getRuntime().exec("reboot")
    }


    @SuppressLint("MissingPermission")
    private fun getSerialNumber(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Build.getSerial()
        } else {
            Build.SERIAL
        }
    }

}