package com.mysafe.lib_base.http;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Create By 张晋铭
 * on 2020/11/23
 * Describe:
 */
public class LogInterceptor implements Interceptor {
    private String TAG_INTERCEPTOR = "TAG_INTERCEPTOR";

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        RequestBody body = request.body();
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        MediaType contentType = body.contentType();
        Charset charset;
        if (contentType == null) {
            charset = UTF_8;
        } else {
            charset = contentType.charset(UTF_8);
        }
        Log.i(TAG_INTERCEPTOR, "**************** request start***************");

        Log.i(TAG_INTERCEPTOR, "BODY:"+buffer.readString(charset));
        Log.i(TAG_INTERCEPTOR, "--> END ${request.method} (${requestBody.contentLength()}-byte body)");

        Log.i(TAG_INTERCEPTOR, "header:" + request.headers());
        Log.i(TAG_INTERCEPTOR, "url:" + request.url());
        //        Log.i(TAG_INTERCEPTOR,"requestBody:"+responseBody.string());
        Log.i(TAG_INTERCEPTOR, "**************** request end ***************");
        return response;
    }

}
