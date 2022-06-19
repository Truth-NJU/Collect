package com.collect.util;

import com.collect.po.enums.UserIdentity;

public class DataCheck {

    public static boolean StringCheck(String t, int len) {
        return t == null || t.length() <= 0 || t.length() > len;
    }

    public static boolean EmailCheck(String t) {
        return StringCheck(t, 28) || !t.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    public static boolean EnumCheck(UserIdentity userIdentity) {
        return userIdentity == null;
    }

    public static boolean INTCheck(Integer target, int max) {
        return target == null || target < 0 || target > max;
    }

    public static boolean DateCheck(Long date) {
        return date == null || date <= System.currentTimeMillis();
    }
}
