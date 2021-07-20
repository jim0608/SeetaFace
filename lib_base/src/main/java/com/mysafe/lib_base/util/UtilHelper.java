package com.mysafe.lib_base.util;


import android.util.Base64;

public class UtilHelper {
    //C#base64字符串转Java byte[]
    public static byte[] base64String2ByteFun(String base64Str) {
        byte[] byteArray = Base64.decode(base64Str, android.util.Base64.DEFAULT);
        return byteArray;
    }

}