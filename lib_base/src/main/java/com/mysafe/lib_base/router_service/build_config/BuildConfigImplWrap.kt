package com.mysafe.lib_base.router_service.build_config

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.mysafe.lib_base.router_service.ConstancePath

/**
 * @author Create By 张晋铭
 * @Date on 2021/5/27
 * @Describe:
 */
object BuildConfigImplWrap {
    private val TAG = "TAG_BuildConfigImplWrap"

    @Autowired(name = ConstancePath.BuildConfigPath)
    lateinit var service: BuildConfigService

    init {
        ARouter.getInstance().inject(this)
    }

    fun isDebug() = service.isDebug()


    fun getPackage() = service.getPackage()

    fun getVersionName() = service.getVersionName()

    fun getVersionCode() = service.getVersionCode()
}