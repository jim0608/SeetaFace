package com.mysafe.lib_base.http;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;

public abstract class RequestCallBack<Gene> {

    public Type mType;

    public RequestCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        //通过反射得到泛型参数
        //Type是 Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class)
            throw new RuntimeException("Missing type parameter.");
        //ParameterizedType参数化类型，即泛型
        ParameterizedType parameterized = (ParameterizedType) superclass;
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        //将Java 中的Type实现,转化为自己内部的数据实现,得到gson解析需要的泛型
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public abstract void Success(Gene result) throws IOException;

    public abstract void Failed(Call call, String e);
}
