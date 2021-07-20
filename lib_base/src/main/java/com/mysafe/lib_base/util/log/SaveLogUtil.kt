package com.mysafe.lib_base.util.log

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Create By 张晋铭
 * @Date on 2021/3/16
 * @Describe:
 */
class SaveLogUtil {
    private val TAG = "TAG_SaveLogUtil"
    private var logFoldPath //日志文件夹路径
            : String? = null
    private var logFilePath //日志文件路径
            : String? = null
    var logSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    private var appProInfo //应用进程信息
            : ProcessInfo? = null
    private var logcatInfo //日志进程信息
            : ProcessInfo? = null

    /**
     * 校验日志目录文件夹
     *
     * @return
     */
    private fun checkLogFolder(): Boolean {
        val file = File(logFoldPath)
        return if (!file.exists()) {
            file.mkdirs()
        } else true
    }

    /**
     * 根据名称获取线程信息
     *
     * @param name
     * @return 返回线程信息的字符串格式
     */
    private fun getProcessInfoStr(name: String): ArrayList<String?> {
        val strs = ArrayList<String?>()
        try {
            val process = Runtime.getRuntime().exec("ps")
            var `is` = process.inputStream
            var isr: InputStreamReader? = InputStreamReader(`is`)
            var br: BufferedReader? = BufferedReader(isr)
            var line: String? = null
            while (br!!.readLine().also { line = it } != null) {
                if (line!!.endsWith(name)) {
                    strs.add(line)
                }
            }
            br.close()
            isr!!.close()
            `is`!!.close()
            br = null
            isr = null
            `is` = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return strs
    }

    /**
     * 获取保存日志的进程信息
     *
     * @param logcatProcessInfos
     * @return
     */
    private fun getLogcatProInfo(logcatProcessInfos: ArrayList<String?>?): ProcessInfo? {
        if (logcatProcessInfos == null) {
            return null
        }
        for (infoStr in logcatProcessInfos) {
            val info = ProcessInfo(infoStr)
            if (info.user == appProInfo!!.user) {
                return info
            }
        }
        return null
    }

    /**
     * 删除logcat进程，退出保存日志
     */
    fun killLogcat() {
        if (logcatInfo == null) {
            return
        }
        try {
            Runtime.getRuntime().exec("kill " + logcatInfo!!.pid)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 清除日志缓存
     */
    fun clearLogCache() {
        try {
            Runtime.getRuntime().exec("su")
            //清除日志缓存
            Runtime.getRuntime().exec("logcat -c")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 开始保存日志
     */
    fun startSaveLog(path: String, packageName: String) {
        appProInfo = ProcessInfo(getProcessInfoStr(packageName)[0])
        logFoldPath = path
        try {
            if (checkLogFolder()) {
                logFilePath = logFoldPath + "/" + SimpleDateFormat("MMdd_HH:mm:ss.SSS").format(Calendar.getInstance().time) + ".log"
            }
            Runtime.getRuntime().exec("logcat -f $logFilePath")
            Log.i(TAG, "startSaveLog: 输出完成")
            logcatInfo = getLogcatProInfo(getProcessInfoStr("logcat"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveLogFile(path: String, packageName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.i("TAG_TOPVIEW", "saveLog: save")
            startSaveLog(path, packageName)
            Log.i("TAG_TOPVIEW", "saveLog: clear")
            clearLogCache()
            Log.i("TAG_TOPVIEW", "saveLog: kill")
            killLogcat()
            Log.i("TAG_TOPVIEW", "saveLog: over")
        }
    }

    companion object {
        const val KILL_LOGCAT_ACTION = "com.mysafe.action.kill_logcat"
        const val START_LOGCAT_ACTION = "com.mysafe.action.start_logcat"

        /**
         * 进程信息，PS命令获取
         */
        @JvmStatic
        var instance: SaveLogUtil? = null
            get() {
                if (field == null) {
                    synchronized(SaveLogUtil::class.java) {
                        if (field == null) {
                            field = SaveLogUtil()
                        }
                    }
                }
                return field
            }
    }
}