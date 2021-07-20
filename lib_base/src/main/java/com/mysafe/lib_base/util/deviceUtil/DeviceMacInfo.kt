package com.mysafe.lib_base.util.deviceUtil

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import java.io.IOException
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.net.NetworkInterface
import java.util.*

/**
 * @author Create By 张晋铭
 * @Date on 2021/5/25
 * @Describe:获取mac地址
 */
object DeviceMacInfo {
    private val TAG = "TAG_DeviceMacInfo"

    /**
     * 判断是否是mac地址
     */
    fun isMac(mac: String): Boolean {
        val trueMacAddress = Regex("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}")
        return mac.matches(trueMacAddress)
    }
    /**
     * 获取mac地址（适配所有Android版本）
     * @return
     */
    fun getMac(context: Context? = null): String {
        var mac: String = ""
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && context != null) {
            mac = getMacDefault(context)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacAddress()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware()
        }
        return mac
    }

    /**
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     * @return
     */
    private fun getMacFromHardware(): String {
        try {
            val all: ArrayList<NetworkInterface>? =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            if (all != null) {
                for (nif in all) {
                    if (!nif.name.equals("wlan0", ignoreCase = true))
                        continue;
                    val macBytes = nif.hardwareAddress ?: return ""
                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }
                    if (!TextUtils.isEmpty(res1)) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }


    /**
     * Android 6.0-Android 7.0 获取mac地址
     */
    private fun getMacAddress(): String {
        var macSerial = ""
        var str = ""
        try {
            val pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address")
            val ir = InputStreamReader(pp.inputStream)
            val input = LineNumberReader(ir)
            while (null != str) {
                str = input.readLine()
                if (str != null) {
                    macSerial = str.trim { it <= ' ' } //去空格
                    break
                }
            }
        } catch (ex: IOException) {
            // 赋予默认值
            ex.printStackTrace()
        }
        return macSerial
    }


    /**
     * Android 6.0 之前（不包括6.0）获取mac地址
     * 必须的权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * @param context * @return
     */
    @SuppressLint("HardwareIds")
    private fun getMacDefault(context: Context?): String {
        var mac = ""
        if (context == null) {
            return mac
        }
        val wifi: WifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var info: WifiInfo? = null
        try {
            info = wifi.getConnectionInfo()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (info == null) {
            return ""
        }
        mac = info.macAddress
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH)
        }
        return mac
    }
}