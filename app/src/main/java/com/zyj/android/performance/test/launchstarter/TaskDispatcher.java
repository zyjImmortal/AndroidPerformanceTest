package com.zyj.android.performance.test.launchstarter;

import android.content.Context;

import com.zyj.android.performance.test.utils.Utils;

/**
 * 启动器调用类
 */
public class TaskDispatcher {

    private static Context sContext;
    private static boolean sHasInit;
    private static boolean sIsMainProcess;

    private TaskDispatcher() {

    }

    public static void init(Context context) {
        if (context != null) {
            sContext = context;
            sHasInit = true;
            sIsMainProcess = Utils.isMainProcess(sContext);
        }
    }
}
