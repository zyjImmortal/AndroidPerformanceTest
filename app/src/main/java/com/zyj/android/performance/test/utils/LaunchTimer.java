package com.zyj.android.performance.test.utils;

public class LaunchTimer {
    private static long sTime;

    public static void startRecord() {
        sTime = System.currentTimeMillis();
    }

    public static void endRecord() {
        endRecord("");
    }

    public static void endRecord(String msg) {
        long cost = System.currentTimeMillis() - sTime;
        LogUtils.i(msg + "  const " + cost);
    }
}
