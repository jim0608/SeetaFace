package com.mysafe.lib_base.local_file;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import com.mysafe.lib_base.StandardConstants;
import com.mysafe.lib_base.expansion.EX_CipherText;
import com.mysafe.lib_base.expansion.EX_Json;
import com.mysafe.lib_base.expansion.EX_String;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Manager_MsSharePreference extends BaseFileManager {

    //region 单例声明
    private volatile static Manager_MsSharePreference singleton;

    private Manager_MsSharePreference() {
    }

    public static Manager_MsSharePreference GetSingleton() {
        if (singleton == null) {
            synchronized (Manager_MsSharePreference.class) {
                if (singleton == null) {
                    singleton = new Manager_MsSharePreference();
                }
            }
        }
        return singleton;
    }
//endregion

    /**
     * 初始化SP文件
     */
    public void Init(Context context) {
        this.context = context;
        File file = new File(GetSPFilePath());
        CreateFile(file);
    }


    public void PutString(String key, String value) {
        PutValue(key, value);
    }

    public String GetString(String key) {
        Object obj = GetValue(key);
        if (null == obj)
            return "";
        else
            return obj.toString();
    }

    public void PutInt(String key, int value) {
        PutValue(key, String.valueOf(value));
    }

    public int GetInt(String key) {
        Object obj = GetValue(key);
        if (null == obj)
            return 0;
        else
            return Integer.parseInt(obj.toString());
    }

    public void PutBoolean(String key, boolean bool) {
        PutValue(key, String.valueOf(bool));
    }

    public boolean GetBoolean(String key) {
        Object obj = GetValue(key);
        if (null == obj)
            return false;
        else
            return Boolean.parseBoolean(obj.toString());
    }

    public void PutDouble(String key, double value) {
        PutValue(key, String.valueOf(value));
    }

    public double GetDouble(String key) {
        Object obj = GetValue(key);
        if (null == obj)
            return 0d;
        else
            return Double.parseDouble(obj.toString());
    }


    //region 内部操作

    /**
     * 存放数据
     *
     * @param key
     * @param value
     */
    private void PutValue(String key, String value) {
        try {
            File file = new File(GetSPFilePath());
            if (!CreateFile(file))
                return;
            //获取文件存储的内容
            String readContent = ReadFileContent(file);
            String enContent;
            if (!EX_String.IsNullOrEmpty(readContent))
                enContent = EX_CipherText.Decrypt(readContent);
            else
                enContent = "";
            //存入数据
            Map<String, Object> content;
            if (!EX_String.IsNullOrEmpty(enContent))
                content = EX_Json.gsonToMaps(enContent);
            else
                content = new ArrayMap<>();

            if (content != null) {
                content.put(key, value);
                //Json序列化
                String content_After = EX_Json.toJsonString(content);
                if (!EX_String.IsNullOrEmpty(content_After)) {
                    String deContent = EX_CipherText.Encrypt(content_After);
                    //写入文件
                    boolean bo = WriteFileContent(file, deContent);
                }
            }

        } catch (Exception e) {
            Log.e(e.getClass().getSimpleName(), e.getMessage());
        }

    }

    /**
     * 获取数据
     *
     * @param key
     * @return
     */
    private Object GetValue(String key) {
        try {
            File file = new File(GetSPFilePath());
            if (!CreateFile(file))
                return null;
            //获取文件存储的内容
            String readContent = ReadFileContent(file);
            if (EX_String.IsNullOrEmpty(readContent))
                return null;
            String enContent = EX_CipherText.Decrypt(readContent);
            //存入数据
            Map<String, Object> content = EX_Json.gsonToMaps(enContent);
            if (content != null) {
                return content.get(key);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 获取文件内的内容
     *
     * @param file
     * @return
     */
    private String ReadFileContent(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
//            StringBuilder stringBuilder = new StringBuilder();
//            //读取当前文件内容
//            FileReader fileReader = new FileReader(file);
//            BufferedReader br = new BufferedReader(fileReader);
//            while (br.readLine() != null) {
//                stringBuilder.append(br.readLine());
//            }
//            fileReader.close();
//            br.close();
//            return stringBuilder.toString();
        } catch (IOException e) {
            //TODO 记录记录记录记录日志
            return "";
        }
    }

    /**
     * 往文件内部写入
     *
     * @param file
     * @param content
     * @return
     */
    private boolean WriteFileContent(File file, String content) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file, false);
            byte[] jk = content.getBytes();
            outputStream.write(content.getBytes());
            outputStream.close();
            outputStream.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String GetSPFilePath() {
        return Combine(GetPrivateAbPath(PrivateFileDir.Files), StandardConstants.DiceName_MemoryOfConfig, StandardConstants.FileName_MySafeSharePreference);
    }
    //endregion

}
