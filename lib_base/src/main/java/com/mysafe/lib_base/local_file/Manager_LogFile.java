package com.mysafe.lib_base.local_file;

import android.content.Context;
import android.text.format.DateFormat;

import com.mysafe.lib_base.StandardConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Manager_LogFile extends BaseFileManager {
    //region 单例声明
    private volatile static Manager_LogFile singleton;

    private Manager_LogFile() {
    }

    public static Manager_LogFile GetSingleton() {
        if (singleton == null) {
            synchronized (Manager_LogFile.class) {
                if (singleton == null) {
                    singleton = new Manager_LogFile();
                }
            }
        }
        return singleton;
    }
//endregion

    private Lock lock = new ReentrantLock(true);

    public void Init(Context context) {
        this.context = context;
    }

    //region
    public static int LogLevel_Info = 0;
    public static int LogLevel_Warning = 1;
    public static int LogLevel_Error = 2;
    public static int LogLevel_Bug = 3;
    public static int LogLevel_Exception = 4;
    //endregion


    /**
     * 记录日志
     *
     * @param logLevel     日志等级
     * @param title        标题
     * @param content      内容
     * @param isNeedUpload 是否需要同步上传到云端
     */
    public void Log(int logLevel, String title, String content, boolean isNeedUpload) {
        WriteLog(logLevel, title, content, isNeedUpload);
    }

    /**
     * 记录日志
     *
     * @param logLevel 日志等级
     * @param title    标题
     * @param content  内容
     */
    public void Log(int logLevel, String title, String content) {
        WriteLog(logLevel, title, content, false);
    }

    private void WriteLog(int logLevel, String title, String content, boolean isNeedUpload) {
        lock.lock();
        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(DateFormat.format("yyyy-MM-dd", calendar));
        sb.append(".log");
        try {
            File file = new File(Combine(GetPrivateAbPath(PrivateFileDir.Files), StandardConstants.DicName_BattleLog, sb.toString()));
            //文件不存在则创建
            if (!CreateFile(file))
                return;
//            FileOutputStream out = new FileOutputStream(file);
            sb.delete(0, sb.length());//清除日志文件名的StringBuilder
            FileWriter write = new FileWriter(file, true);

            String logLevelStr = logLevel == 0 ? "Info" : logLevel == 1 ? "Warning" : logLevel == 2 ? "Error" : logLevel == 3 ? "Bug" : logLevel == 4 ? "Exception" : "None";
            sb.append(logLevelStr);
            sb.append("\t");
            sb.append(calendar.get(Calendar.HOUR_OF_DAY));
            sb.append(":");
            sb.append(calendar.get(Calendar.MINUTE));
            sb.append(":");
            sb.append(calendar.get(Calendar.SECOND));
            sb.append("\n");
            sb.append(title);
            sb.append(":");
            sb.append("\t");
            sb.append(content);
            sb.append("\n");

            write.write(sb.toString());
            write.close();

            if (isNeedUpload) {//TODO 做云端上传操作

            }
        } catch (
                IOException e) {
            android.util.Log.e("IOerr", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            android.util.Log.e("EXerr", e.getMessage());
            e.printStackTrace();
        } finally {
            lock.unlock(); //解锁
        }
    }

}
