package com.mysafe.lib_base.router_service.build_config

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author Create By 张晋铭
 * @Date on 2021/5/27
 * @Describe:
 */
interface BuildConfigService : IProvider {
    fun isDebug(): Boolean

    fun getPackage(): String

    fun getVersionName(): String

    fun getVersionCode(): String
}