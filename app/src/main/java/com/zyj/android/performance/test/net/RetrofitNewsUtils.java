package com.zyj.android.performance.test.net;

import com.zyj.android.performance.test.PerformanceApp;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * Retrofit是一个对网络请求框架(比如okhttp)进行封装过的框架
 */
public class RetrofitNewsUtils {
    private static final APIService API_SERVICE;

    public static APIService getApiService() {
        return API_SERVICE;
    }

    public static final String HTTP_SPORTSNBA_QQ_COM = "http://sportsnba.qq.com/";

    static {
        // 自定义OkHttpClient请求客户端
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Cache cache = new Cache(PerformanceApp.getApplication().getCacheDir(),10*1024*1024);
        client.
                eventListenerFactory(OkHttpEventListener.FACTORY).
                dns(OkHttpDNS.getIns(PerformanceApp.getApplication())).
                addInterceptor(new NoNetInterceptor()).
                addInterceptor(logging);

        final Retrofit RETROFIT = new Retrofit.Builder()
                .baseUrl(HTTP_SPORTSNBA_QQ_COM)
                .addConverterFactory(FastJsonConverterFactory.create())
                .client(client.build())
                .build();
        // 创建网络请求接口实例,调用实例的方法会返回一个Call对象,通过Call对象执行同步或者异步请求
        API_SERVICE = RETROFIT.create(APIService.class);
    }


}
