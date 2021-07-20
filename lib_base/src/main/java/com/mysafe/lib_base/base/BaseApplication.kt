package com.mysafe.lib_base.base

import android.app.Application
import xcrash.ICrashCallback
import xcrash.XCrash

open class BaseApplication : Application() {
    //endregion
    override fun onCreate() {
        super.onCreate()
    }

    /**
     * 初始化崩溃捕捉工具
     */
    fun initCrashTools(versionName: String) {
        val initParameters = XCrash.InitParameters()
        //需要将日志输出的路径，自行编辑
        initParameters.setLogDir(getCrashDir())
        //引用com.blankj:utilcode库中的获取APP的VersionName
        initParameters.setAppVersion(versionName)
        //捕获异常后的回调函数
        val crashCallback = ICrashCallback { logPath: String?, emergency: String? -> }
        //java异常的捕获
        initParameters.setJavaCallback(crashCallback)

        //anr的捕获
        initParameters.setAnrCallback(crashCallback)
        //native异常的捕获
        initParameters.setNativeCallback(crashCallback)
        //初始化xCrash
        XCrash.init(this, initParameters)
    }

    private fun getCrashDir(): String {
        return applicationContext.getExternalFilesDir("crashLog").toString()
    }

    companion object {
        //region 单例
        private var singleton: BaseApplication? = null
        fun GetSingleton(): BaseApplication? {
            if (singleton == null) singleton = BaseApplication()
            return singleton
        }
    }
}