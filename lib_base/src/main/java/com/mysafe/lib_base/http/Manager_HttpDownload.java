//package com.mysafe.lib_base.http;
//
//import android.media.MediaCodec;
//import android.util.Log;
//
//import com.mysafe.lib_base.expansion.EX_File;
//import com.mysafe.lib_base.expansion.EX_String;
//import com.mysafe.lib_base.sqlite.Manager_LitePal;
//import com.mysafe.lib_base.sqlite.model.Mod_DownloadTask;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.RandomAccessFile;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class Manager_HttpDownload extends HttpRequest_Base {
//
//    //region 单例声明
//    private volatile static Manager_HttpDownload singleton;
//
//    private Manager_HttpDownload() {
//    }
//
//    public static Manager_HttpDownload GetSingleton() {
//        if (singleton == null) {
//            synchronized (Manager_HttpDownload.class) {
//                if (singleton == null) {
//                    singleton = new Manager_HttpDownload();
//                }
//            }
//        }
//        return singleton;
//    }
////endregion
//
//    private static final int ByteSize = 1024;
//
//    private static final String Suffix = ".downloading";
//
//
//    class DownloadThread extends Thread {
//        Mod_DownloadTask taskInfo = null;
//        private File file;
//
//        public DownloadThread(Mod_DownloadTask taskInfo) {
//            this.taskInfo = taskInfo;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            //向数据库插入线程任务信息
//            if (!Manager_LitePal.GetSingleton().SQLite_Query_IsDownloadTaskExists(taskInfo.getDownloadUrl(), taskInfo.getId())) {
//                Manager_LitePal.GetSingleton().SQLite_Insert_DownloadTask(taskInfo, null);
//            }
//            HttpURLConnection httpURLConnection = null;
//            RandomAccessFile raf = null;
//            InputStream inputStream = null;
//            try {
//                Intent intent = new Intent(DownloadService.ACTION_UPDATA);
//
//                //设置文件写入位置 同时判断手机中是否已经有下载的相同文件
//                file = new File(DownloadService.DOWNLOAD_PATH, fileInfo.getFileName());
//
//                if (file.length() == fileInfo.getLength()){
//                    //已经有这个文件了
//                    //下载完成之后发送广播
//                    intent.putExtra("finished", 100 + "");
//                    intent.putExtra("fileId", fileInfo.getId());
//                    context.sendBroadcast(intent);
//                    //下载完成之后更新线程任务信息
//                    dao.updateThread(threadInfo.getUrl(), threadInfo.getId(), 3);
//                    return;
//                }
//
//                raf = new RandomAccessFile(file, "rwd");
//                raf.seek(file.length());
//
//                //开始下载
//                URL url = new URL(threadInfo.getUrl());
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setConnectTimeout(3000);
//                httpURLConnection.setRequestMethod("GET");
//                httpURLConnection.setRequestProperty("RANGE", "bytes=" + file.length() + "-" );
//                inputStream = httpURLConnection.getInputStream();
//                byte[] bytes = new byte[1024 * 10];
//                int len;
//                long time = System.currentTimeMillis();
//
//                while ((len = inputStream.read(bytes)) != -1) {
//                    //写入文件
//                    raf.write(bytes, 0, len);
//                    //下载进度发送广播给activity
//                    if (System.currentTimeMillis() - time > 300) {
//                        time = System.currentTimeMillis();
//                        intent.putExtra("finished", file.length() * 100 / fileInfo.getLength() + "");
//                        intent.putExtra("fileId", fileInfo.getId());
//                        context.sendBroadcast(intent);
//                    }
//                    //下载暂停保存下载进度
//                    if (isPause) {
//                        dao.updateThread(threadInfo.getUrl(), threadInfo.getId() , 2);
//                        return;
//                    }
//                }
//                //下载完成之后发送广播
//                intent.putExtra("finished", 100 + "");
//                intent.putExtra("fileId", fileInfo.getId());
//                context.sendBroadcast(intent);
//                //下载完成之后更新线程任务信息
//                dao.updateThread(threadInfo.getUrl(), threadInfo.getId(), 3);
//            } catch (Exception e) {
//                e.printStackTrace();
//                isPause = true;
//                dao.updateThread(threadInfo.getUrl(), threadInfo.getId(), 2);
//                Intent intent = new Intent(DownloadService.ACTION_ERRO);
//                intent.putExtra("fileId", fileInfo.getId());
//                context.sendBroadcast(intent);
//            } finally {
//                if (httpURLConnection != null) {
//                    httpURLConnection.disconnect();
//                }
//                if (raf != null) {
//                    try {
//                        raf.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (inputStream != null) {
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        }
//    }
//
////    public void DownloadFileForCallBack(String downloadUrl, String localFile) {
////        String localFileReal = localFile;
////        String localFileWithSuffix = localFile + Suffix;
////
////        try {
////            long startPosition = 0;
////            FileOutputStream writeSteam = null;
////            RandomAccessFile randomAccessFile = null;
////
////            if (EX_String.IsNullOrEmpty(downloadUrl))
////                //下载地址是空
////                return;
////
////            if (EX_String.IsNullOrEmpty(localFileReal))
////                //本地文件路径是空
////                return;
////
////            int remoteFileLength = GetHttpFileLength(downloadUrl);
////            if (remoteFileLength == 0)
////                //远程文件长度为0
////                return;
////
////            if (EX_File.Exists(localFileReal))
////                //本地存在待下载文件
////                return;
////
////            //本地存在下载中的文件
////            if (EX_File.Exists(localFileWithSuffix)) {
////                File existsFile = new File(localFileWithSuffix);
////                FileInputStream inputStream = new FileInputStream(existsFile);
////                writeSteam = new FileOutputStream(new File(localFileWithSuffix));
////                startPosition = inputStream.available();
////                //如果当前文件大小大于网络文件
////                if (startPosition > remoteFileLength) {
////                    writeSteam.close();
////                    //删除文件
////                    EX_File.Delete(localFileWithSuffix);
////                }
////                //如果文件大小一致
////                else if (startPosition == remoteFileLength) {
////                    DownloadFileDone(localFileReal, localFileWithSuffix);
////                    writeSteam.close();
////                } else {
////                    randomAccessFile = new RandomAccessFile(existsFile, "rwd");
////                    randomAccessFile.seek(startPosition);
////                }
////            } else {
////                File newFile = new File(localFileWithSuffix);
////                randomAccessFile = new RandomAccessFile(newFile, "rwd");
////            }
////
////            //开始下载
////            URL url = new URL(downloadUrl);
////
////
////        } catch (Exception e) {
////            Log.i(e.getLocalizedMessage(), e.getMessage());
////        }
////
////    }
//
//
//    /**
//     * 下载文件完成,改回原本名称
//     *
//     * @param localFileReal
//     * @param localFileWithSuffix
//     */
//    private void DownloadFileDone(String localFileReal, String localFileWithSuffix) {
//        try {
//            InputStream streamFrom = new FileInputStream(localFileWithSuffix);
//            OutputStream streamTo = new FileOutputStream(localFileReal);
//            byte buffer[] = new byte[1024];
//            int len;
//            while ((len = streamFrom.read(buffer)) > 0) {
//                streamTo.write(buffer, 0, len);
//            }
//            streamFrom.close();
//            streamTo.close();
//            new File(localFileWithSuffix).delete();
//        } catch (IOException e) {
//            Log.e("Exception", e.getMessage());
//        }
//
//    }
//
//
//}
