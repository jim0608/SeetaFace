package com.mysafe.lib_base.expansion;

public class EX_String {
    public static boolean IsNullOrEmpty(String value) {
        if (null == value) return true;
        if ("".equals(value)) return true;
        if (" ".equals(value)) return true;
        if ("null".equals(value)) return true;
        if (0 == value.length()) return true;
        if (0 == value.trim().length()) return true;
        return false;
    }

}
