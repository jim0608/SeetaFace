package com.mysafe.lib_base.local_file;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mysafe.lib_base.StandardConstants;

import java.io.File;

public class Manager_FilePathProvider extends BaseFileManager {

    //region 单例声明
    @SuppressLint("StaticFieldLeak")
    private volatile static Manager_FilePathProvider singleton;

    private Manager_FilePathProvider() {
    }

    public static Manager_FilePathProvider GetSingleton() {
        if (singleton == null) {
            synchronized (Manager_FilePathProvider.class) {
                if (singleton == null) {
                    singleton = new Manager_FilePathProvider();
                }
            }
        }
        return singleton;
    }
//endregion

    public void Init(Context context) {
        this.context = context;
    }

    /**
     * 获取下载文件夹的文件地址(附带文件名,可直接建立文件对象)
     *
     * @param fileName 文件名
     * @return
     */
    public String GetDownloadFilePath(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists())
                if (!file.mkdirs())
                    return "";
            return Combine(GetPrivateAbPath(PrivateFileDir.Files), StandardConstants.DicName_MemoryOfDownload, fileName);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取下载文件夹的文件夹地址
     *
     * @return
     */
    public String GetDownloadFilePath() {
        try {
            File file = new File(Combine(GetPrivateAbPath(PrivateFileDir.Files), StandardConstants.DicName_MemoryOfDownload));
            if (!file.exists())
                if (!file.mkdir())
                    return "";
            return file.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }
}
