package com.mysafe.lib_base;

public class StandardConstants {

    public static String appName = "";
    //region 文件路径名相关
    /**
     * 日志文件夹名
     */
    public static String DicName_BattleLog = "BatterLog";
    /**
     * 图片文件夹名
     */
    public static String DicName_MemoryOfImage = "MemoryOfImage";
    /**
     * 下载文件夹名
     */
    public static String DicName_MemoryOfDownload = "MemoryOfDownload";
    /**
     * 配置文件夹名
     */
    public static String DiceName_MemoryOfConfig = "MemoryOfConfig";
    /**
     * 自制Sp框架Sp文件名
     */
    public static String FileName_MySafeSharePreference = "M_S_S_P.json";
    //endregion

    //region 四大组件相关
    /**
     * IntentBundleKey
     */
    public static String AFM_INTENT_BundleDataKey = "BundleData";

    //endregion

    //region 设备类型区分
    /**
     * 使用的设备类型:扫脸人员端
     */
    public static int TsugaiDeviceType_Customer = 1;
    /**
     * 使用的设备类型:配餐职工端
     */
    public static int TsugaiDeviceType_Staff = 2;
    /**
     * 使用的设备类型:公众查询点餐端
     */
    public static int TsugaiDeviceType_Public = 3;
    //endregion
}
