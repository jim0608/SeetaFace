package com.mysafe.lib_base.util.log;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 保存日志服务
 * Created by 左克飞 on 17/8/3 下午3:25.
 * FilePath App:com.flueky.framework.android
 */

public class SaveLogService extends Service {

    private String logFoldPath;//日志文件夹路径
    private String logFilePath;//日志文件路径
    public static final String KILL_LOGCAT_ACTION = "com.mysafe.action.kill_logcat";
    public static final String START_LOGCAT_ACTION = "com.mysafe.action.start_logcat";

    private ProcessInfo appProInfo;//应用进程信息
    private ProcessInfo logcatInfo;//日志进程信息
    private SaveLogReceiver saveLogReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logFoldPath = getExternalFilesDir("log").toString();
        saveLogReceiver = new SaveLogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(KILL_LOGCAT_ACTION);
        filter.addAction(START_LOGCAT_ACTION);
        registerReceiver(saveLogReceiver, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        appProInfo = new ProcessInfo(getProcessInfoStr(getPackageName()).get(0));
        //清除日志缓存
        //开始保存日志
        startSaveLog();
        clearLogCache();
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(saveLogReceiver);
    }

    /**
     * 校验日志目录文件夹
     *
     * @return
     */
    private boolean checkLogFolder() {
        File file = new File(logFoldPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 根据名称获取线程信息
     *
     * @param name
     * @return 返回线程信息的字符串格式
     */
    private ArrayList<String> getProcessInfoStr(String name) {

        ArrayList<String> strs = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec("ps");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.endsWith(name)) {
                    strs.add(line);
                }
            }
            br.close();
            isr.close();
            is.close();

            br = null;
            isr = null;
            is = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return strs;
    }


    /**
     * 获取保存日志的进程信息
     *
     * @param logcatProcessInfos
     * @return
     */
    private ProcessInfo getLogcatProInfo(ArrayList<String> logcatProcessInfos) {
        if (logcatProcessInfos == null) {
            return null;
        }
        for (String infoStr : logcatProcessInfos) {
            ProcessInfo info = new ProcessInfo(infoStr);
            if (info.user.equals(appProInfo.user)) {
                return info;
            }
        }
        return null;
    }

    /**
     * 删除logcat进程，退出保存日志
     */
    private void killLogcat() {
        if (logcatInfo == null) {
            return;
        }

        try {
            Runtime.getRuntime().exec("kill " + logcatInfo.pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除日志缓存
     */
    private void clearLogCache() {
        try {
            Runtime.getRuntime().exec("su");
            //清除日志缓存
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始保存日志
     */
    private void startSaveLog() {
        try {
	        if (checkLogFolder()) {
                logFilePath = logFoldPath + "/HH-" + new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()) + ".log";
            }
            Runtime.getRuntime().exec("logcat -f " + logFilePath);
            logcatInfo = getLogcatProInfo(getProcessInfoStr("logcat"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进程信息，PS命令获取
     */
    class ProcessInfo {
        private String user;
        private String pid;
        private String ppid;
        private String vsize;
        private String rss;
        private String wchan;
        private String pc;
        private String name;

        public ProcessInfo(String info) {
            if (info == null) {
                return;
            }
            ArrayList<String> item = new ArrayList<String>();
            for (int i = 0; i < info.length(); i++) {
                if (info.charAt(i) == ' ') {
                    continue;
                }
                int j = i + 1;
                while (j < info.length() && info.charAt(j++) != ' ') ;
                item.add(info.substring(i, j - 1));
                i = j - 1;
            }

            user = item.get(0);
            pid = item.get(1);
            ppid = item.get(2);
            vsize = item.get(3);
            rss = item.get(4);
            wchan = item.get(5);
            pc = item.get(6);
            name = item.get(8);
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append(user).
                    append(" ").
                    append(pid).
                    append(" ").
                    append(ppid).
                    append(" ").
                    append(vsize).
                    append(" ").
                    append(rss).
                    append(" ").
                    append(wchan).
                    append(" ").
                    append(pc).
                    append(" ").
                    append(name);

            return buffer.toString();
        }
    }

    /**
     * 保存日志操作广播
     */
    class SaveLogReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(KILL_LOGCAT_ACTION)) {
                killLogcat();
            } else if (intent.getAction().equals(START_LOGCAT_ACTION)) {
                clearLogCache();
                startSaveLog();
            }
        }
    }

}
