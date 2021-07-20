package com.mysafe.lib_base.expansion;

import java.io.File;

public class EX_File {
    public static boolean Exists(String path) {
        File file = new File(path);
        if (file.length() == 0)
            return false;
        return file.exists();
    }

    public static boolean Delete(String path) {
        File file = new File(path);
        file.deleteOnExit();
        return true;
    }

}
