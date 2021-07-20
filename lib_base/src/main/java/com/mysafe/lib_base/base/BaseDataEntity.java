package com.mysafe.lib_base.base;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author Create By 张晋铭
 * @Date on 2021/3/12
 * @Describe:
 */
public class BaseDataEntity implements Parcelable {
    private String baseUrl;
    private String deviceNo;
    private String platformTitle;
    private ActivateConfigBean activateConfig;
    private VersionConfigBean versionConfig;

    protected BaseDataEntity(Parcel in) {
        baseUrl = in.readString();
        deviceNo = in.readString();
        activateConfig = in.readParcelable(ActivateConfigBean.class.getClassLoader());
        versionConfig = in.readParcelable(VersionConfigBean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(baseUrl);
        dest.writeString(deviceNo);
        dest.writeParcelable(activateConfig, flags);
        dest.writeParcelable(versionConfig, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseDataEntity> CREATOR = new Creator<BaseDataEntity>() {
        @Override
        public BaseDataEntity createFromParcel(Parcel in) {
            return new BaseDataEntity(in);
        }

        @Override
        public BaseDataEntity[] newArray(int size) {
            return new BaseDataEntity[size];
        }
    };

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getPlatformTitle() {
        return platformTitle;
    }

    public void setPlatformTitle(String platformTitle) {
        this.platformTitle = platformTitle;
    }

    public ActivateConfigBean getActivateConfig() {
        return activateConfig;
    }

    public void setActivateConfig(ActivateConfigBean activateConfig) {
        this.activateConfig = activateConfig;
    }

    public VersionConfigBean getVersionConfig() {
        return versionConfig;
    }

    public void setVersionConfig(VersionConfigBean versionConfig) {
        this.versionConfig = versionConfig;
    }
}
