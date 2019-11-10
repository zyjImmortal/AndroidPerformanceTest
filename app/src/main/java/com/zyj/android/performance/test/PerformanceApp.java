package com.zyj.android.performance.test;

import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.zyj.android.performance.test.utils.LaunchTimer;
import com.zyj.android.performance.test.utils.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.jpush.android.api.JPushInterface;

public class PerformanceApp extends Application {
    private static Application mApplication;
    private String mDeviceId;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));

    // 可以设置一个初始数值，在数值大于0之前让调用await()方法的线程堵塞住，数值为0是则会放开所有阻塞住的线程
    // 如果主线程要使用子线程的东西，所以需要主线程要等待某个特定的子线程执行完后再执行，可以通过CountDownLatch实现

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public static Application getApplication() {
        return mApplication;
    }

    public void setDeviceId(String deviceId) {
        this.mDeviceId = deviceId;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            // 一些处理
        }
    };

    /**
     * 设置context，继承至ContextWrapper的方法，是个包装类，包装操作context各种方法
     * attachBaseContext是Android应用程序启动最开始的地方
     * Context提供了关于应用环境全局信息的接口。它是一个抽象类，它的执行被Android系统所提供。
     * 它允许获取以应用为特征的资源和类型，是一个统领一些资源（应用程序环境变量等）的上下文。
     * 就是说，它描述一个应用程序环境的信息（即上下文）；
     * 是一个抽象类，Android提供了该抽象类的具体实现类；
     * 通过它我们可以获取应用程序的资源和类（包括应用级别操作，如启动Activity，发广播，接受Intent等）
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LaunchTimer.startRecord();
        MultiDex.install(this); // 兼容5.0以下机型
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
//        Debug.startMethodTracing("app");
        LaunchTimer.startRecord();


        ExecutorService service = Executors.newFixedThreadPool(CORE_POOL_SIZE);

        service.submit(new Runnable() {
            @Override
            public void run() {
                initBugly();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                initAMap();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                initFresco();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                initJPush();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                initStetho();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                initUmeng();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                initWeex();
                countDownLatch.countDown();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                getDeviceId();
            }
        });

//        initAMap();
//        initBugly();
//        initFresco();
//        initJPush();
//        initStetho();
//        initUmeng();
//        initWeex();
//        getDeviceId();
//        Debug.stopMethodTracing();
        try {
            // await()检测自己有没有被满足，如果没有被满足就会阻塞线程，
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LaunchTimer.endRecord("performance app cost");
    }

    private void initStetho() {
        Handler handler = new Handler(Looper.getMainLooper());
        Stetho.initializeWithDefaults(this);
    }

    private void initWeex() {
        InitConfig config = new InitConfig.Builder().build();
        WXSDKEngine.initialize(this, config);
    }

    private void initJPush() {
        JPushInterface.init(this);
        JPushInterface.setAlias(this, 0, mDeviceId);
    }

    private void initFresco() {
        Fresco.initialize(this);
    }

    private void initAMap() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    private void initUmeng() {
        UMConfigure.init(this, "58edcfeb310c93091c000be2", "umeng",
                UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
    }

    private void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), "8fed1867-875a-4b67-8509-c2f2a3193432", false);
    }

//    public String getStringFromAssets(String fileName) {
//        String Result = "";
//        InputStreamReader inputReader = null;
//        BufferedReader bufReader = null;
//        try {
//            inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
//            bufReader = new BufferedReader(inputReader);
//            String line = "";
//            while ((line = bufReader.readLine()) != null) {
//                Result += line;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (inputReader != null) {
//                try {
//                    inputReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (bufReader != null) {
//                try {
//                    bufReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return Result;
//    }
}
