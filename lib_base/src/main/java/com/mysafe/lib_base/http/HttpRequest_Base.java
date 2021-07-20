package com.mysafe.lib_base.http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.mysafe.lib_base.expansion.EX_Json;
import com.mysafe.lib_base.expansion.EX_String;
import com.tencent.mmkv.MMKV;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpRequest_Base {
    private MMKV mmkv = MMKV.defaultMMKV();
    private OkHttpClient _Client;
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public static String _BaseRequestUrl;//地址
    public static String devicesNo;//设备的唯一序列号,用于验证设备身份
    public static String devicesType;//设备的类型,Customer = 1 Staff = 2,Public = 3

    OkHttpClient GetClient() {
        if (_Client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NotNull String s) {
//                    System.out.print("||======TAG_OkHttp=====>>|| "+s);
                    Log.i("TAG_OkHttp", "||====== OkHttp Log =====>>|| " + s);
                }
            });
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            builder.readTimeout(60, TimeUnit.SECONDS)
//                    .addInterceptor(new LogInterceptor())
                    .addInterceptor(interceptor)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
//                    .sslSocketFactory(createSSLSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
            _Client = builder.build();
        }
        return _Client;
    }

    //获取地址相关数据
    private void getUrlData() {
        if (_BaseRequestUrl == null) {
            _BaseRequestUrl = mmkv.decodeString("RequestUrl");
        }
        if (devicesNo == null) {
            devicesNo = mmkv.decodeString("DevicesNo");
        }
        if (devicesType == null) {
            devicesType = mmkv.decodeString("DevicesType");
        }
    }


    void saveAddressUrl() {
        mmkv.encode("RequestUrl", _BaseRequestUrl);
        mmkv.encode("DevicesNo", devicesNo);
        mmkv.encode("DevicesType", devicesType);
    }

    void SetHeader(@NonNull Map<String, String> dir) {
        if (!dir.isEmpty()) {
            Headers.Builder builder = new Headers.Builder();
            for (Map.Entry<String, String> entry : dir.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
//            _Header = builder.build();
        } else {
            //            _Header = new Headers.Builder().build();
        }
    }


    //region 证书相关

    /**
     *      * 生成安全套接字工厂，用于https请求的证书跳过
     *      * @return
     *      
     */
    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
        }
        return ssfFactory;
    }

    /**
     *      * 用于信任所有证书
     *      
     */
    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
    //endregion

    /**
     * POST请求
     *
     * @param requestMethod
     * @param data
     * @param callBack
     */

    public void HttpRequest_AsyncPost_Json(String requestMethod, Map<String, Object> data, final RequestCallBack callBack) {
        try {
            if (_BaseRequestUrl == null || devicesNo == null || devicesType == null) {
                getUrlData();
            }
            String requestParams = "";
            if (data != null)
                requestParams = EX_Json.toJsonString(data);
            RequestBody body = RequestBody.Companion.create(requestParams, JSON);
            final Request request = new Request.Builder()
                    .url(_BaseRequestUrl + requestMethod)
                    .addHeader("deviceNo", devicesNo)
                    .addHeader("deviceType", devicesType)
                    .post(body)
                    .build();
            Call call = GetClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (e.getMessage() == null) {
                        callBack.Failed(call, e.toString() + "");
                    } else {
                        callBack.Failed(call, e.getMessage());
                    }

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (call.isExecuted())
                        if (response.code() == 200) {
                            String body = Objects.requireNonNull(response.body()).string();
                            if (!EX_String.IsNullOrEmpty(body)) {
                                callBack.Success(EX_Json.gsonToBean(body, callBack.mType));
                            } else {
                                callBack.Success(null);
                            }
                        } else {
                            callBack.Failed(call, response.code() + ":" + response.message());
                        }

                }
            });
        } catch (Exception ex) {
            callBack.Failed(null, ex.getMessage());
        }
    }

    /**
     * Get请求
     *
     * @param requestMethod
     * @param callBack
     */
    public void HttpRequest_AsyncGet_Json(String requestMethod, final RequestCallBack callBack) {
        try {
            if (_BaseRequestUrl == null || devicesNo == null || devicesType == null) {
                getUrlData();
            }
            final Request request = new Request.Builder()
                    .url(_BaseRequestUrl + requestMethod)
                    .addHeader("deviceNo", devicesNo)
                    .addHeader("deviceType", devicesType)
                    .build();
            Call call = GetClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    callBack.Failed(call, e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (call.isExecuted())
                        if (response.code() == 200) {
                            String body = Objects.requireNonNull(response.body()).string();
                            if (!EX_String.IsNullOrEmpty(body)) {
                                callBack.Success(EX_Json.gsonToBean(body, callBack.mType));
                            } else {
                                callBack.Success("body is null");
                            }
                        } else {
                            callBack.Failed(call, response.code() + ":" + response.message());
                        }
                }
            });
        } catch (Exception ex) {
            callBack.Failed(null, ex.getMessage());
        }
    }

    /**
     * 获取HTTP文件大小
     * *注意,只能在子线程操作
     *
     * @param url 文件地址
     * @return 文件大小
     */
    protected int GetHttpFileLength(String url) {
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return 0;
        }
        HttpURLConnection urlcon = null;
        try {
            urlcon = (HttpURLConnection) u.openConnection();
        } catch (IOException e) {
            return 0;
        }
        try {
            return urlcon.getContentLength();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return 0;
    }

}
