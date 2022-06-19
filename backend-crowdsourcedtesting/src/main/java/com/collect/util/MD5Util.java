package com.collect.util;

import org.springframework.util.DigestUtils;

public class MD5Util {
    public static String md5(String msg) {
        return DigestUtils.md5DigestAsHex((msg).getBytes());
    }

    public static String md5(String msg, String salt) {
        return DigestUtils.md5DigestAsHex((msg + salt).getBytes());
    }

    public static boolean verify(String msg, String code) {
        return code.equals(md5(msg));
    }

    public static boolean verify(String msg, String salt, String code) {
        return code.equals(md5(msg, salt));
    }
}
