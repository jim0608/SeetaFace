package com.mysafe.lib_base.http;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface IDownloadCallBack {
    void OnFailure(Call call, IOException e);

    void OnResponse(Call call, Response response, long mTotalLength, long mAlreadyDownLength,double percent);
}
