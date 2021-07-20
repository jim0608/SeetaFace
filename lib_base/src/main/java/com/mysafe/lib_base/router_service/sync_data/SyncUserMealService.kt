package com.mysafe.lib_base.router_service.sync_data

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author Create By 张晋铭
 * @Date on 2021/6/16
 * @Describe:
 */
interface SyncUserMealService : IProvider {
    fun fromCustom(faceId: String)
    fun formStaff(content: String)
}