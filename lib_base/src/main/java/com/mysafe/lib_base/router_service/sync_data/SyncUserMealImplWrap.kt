package com.mysafe.lib_base.router_service.sync_data

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.mysafe.lib_base.router_service.ConstancePath

/**
 * @author Create By 张晋铭
 * @Date on 2021/6/1
 * @Describe:
 */
object SyncUserMealImplWrap {
    @Autowired(name = ConstancePath.UserIdPath)
    lateinit var service: SyncUserMealService

    init {
        ARouter.getInstance().inject(this)
    }

    fun fromCustomer(str: String) {
        service.fromCustom(str)
    }

    fun fromStaff(str: String) {
        service.formStaff(str)
    }
}