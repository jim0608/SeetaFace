package com.mysafe.lib_base.local_file;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.Objects;

class BaseFileManager {

    Context context;

    protected enum PrivateFileDir {
        Cache,
        Files,
    }

    String GetPrivateAbPath(PrivateFileDir dir) {
        String path = "";
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        switch (dir) {
            case Cache: {
                if (externalStorageAvailable)
                    path = Objects.requireNonNull(context.getExternalCacheDir()).getAbsolutePath();
                else
                    path = Objects.requireNonNull(context.getCacheDir()).getAbsolutePath();
            }
            break;
            case Files: {
                if (externalStorageAvailable) {
                    File jk = context.getExternalFilesDir(null);
                    String pathss = jk.getAbsolutePath();
                    path = Objects.requireNonNull(context.getExternalFilesDir("")).getAbsolutePath();

                } else
                    path = Objects.requireNonNull(context.getFilesDir()).getAbsolutePath();
            }
            break;
        }
        return path;
    }


    /**
     * 创建文件
     *
     * @param file 文件
     * @return 是否成功
     */
    boolean CreateFile(File file) {
        boolean boolReturn = true;
        try {
            if (file.exists()) {
                if (!file.isFile())
                    if (!file.createNewFile())
                        boolReturn = false;
            } else {
                if (!file.getParentFile().exists()) {
                    if (file.getParentFile().mkdirs())
                        if (!file.createNewFile())
                            boolReturn = false;
                        else
                            boolReturn = false;
                } else if (!file.createNewFile())
                    boolReturn = false;
            }

        } catch (Exception e) {
            boolReturn = false;
        }
        return boolReturn;
    }

    /**
     * 拼装路径
     *
     * @param paths 路径集合
     * @return 拼接好的路径
     */
    static String Combine(String... paths) {
        StringBuilder path = new StringBuilder();
        for (String item : paths) {
            if (path.toString().equals(""))
                path = new StringBuilder(item);
            else
                path.append(File.separator).append(item);
        }
        return path.toString();
    }

}
