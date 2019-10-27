package com.zyj.android.performance.test.utils;

public class LaunchTimer {
    private static long sTime;

    public static void startRecord(){
        sTime = System.currentTimeMillis();
    }

    public static void endRecord(){
        long cost = System.currentTimeMillis() - sTime;
        LogUtils.i("const " + cost);
    }
}
