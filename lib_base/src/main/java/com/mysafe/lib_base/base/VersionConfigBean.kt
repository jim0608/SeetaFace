package com.mysafe.lib_base.base

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Create By 张晋铭
 * @Date on 2021/3/12
 * @Describe:
 */
class VersionConfigBean() : Parcelable {
    var version_Code: String = ""
    var version_DownloadUrl: String = ""
    var version_CodeEx: String = ""
    var version_DownloadUrlEx: String = ""

    constructor(parcel: Parcel) : this() {
        version_Code = parcel.readString()?:""
        version_DownloadUrl = parcel.readString()?:""
        version_CodeEx = parcel.readString()?:""
        version_DownloadUrlEx = parcel.readString()?:""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(version_Code)
        parcel.writeString(version_DownloadUrl)
        parcel.writeString(version_CodeEx)
        parcel.writeString(version_DownloadUrlEx)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VersionConfigBean> {
        override fun createFromParcel(parcel: Parcel): VersionConfigBean {
            return VersionConfigBean(parcel)
        }

        override fun newArray(size: Int): Array<VersionConfigBean?> {
            return arrayOfNulls(size)
        }
    }
}