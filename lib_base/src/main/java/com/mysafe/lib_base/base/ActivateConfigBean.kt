package com.mysafe.lib_base.base

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Create By 张晋铭
 * @Date on 2021/3/12
 * @Describe:
 */
class ActivateConfigBean() : Parcelable {
    //虹软activeKey
    var activate_CODE: String = ""
    //虹软appId
    var activate_APPID: String = ""
    //虹软sdkKey
    var activate_SDKKEY: String = ""

    constructor(parcel: Parcel) : this() {
        activate_APPID = parcel.readString()?:""
        activate_SDKKEY = parcel.readString()?:""
        activate_CODE = parcel.readString()?:""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(activate_APPID)
        parcel.writeString(activate_SDKKEY)
        parcel.writeString(activate_CODE)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivateConfigBean> {
        override fun createFromParcel(parcel: Parcel): ActivateConfigBean {
            return ActivateConfigBean(parcel)
        }

        override fun newArray(size: Int): Array<ActivateConfigBean?> {
            return arrayOfNulls(size)
        }
    }
}