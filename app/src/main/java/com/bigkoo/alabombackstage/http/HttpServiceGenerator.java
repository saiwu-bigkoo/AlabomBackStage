package com.bigkoo.alabombackstage.http;

import com.bigkoo.alabombackstage.constants.HttpUrlConstant;
import com.bigkoo.alabombackstage.http.factory.GsonConverterFactory;
import com.bigkoo.alabombackstage.http.interceptor.LoggingInterceptor;
import com.bigkoo.alabombackstage.manager.AppInfoManager;
import com.bigkoo.alabombackstage.http.adapter.NullStringToEmptyAdapterFactory;
import com.bigkoo.alabombackstage.http.interceptor.HeadAuthenticator;
import com.bigkoo.alabombackstage.utils.NetWorkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Sai on 2018/3/21.
 */

public class HttpServiceGenerator {
    public static final String JSON = "application/json;charset=utf-8";
    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 45000;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 45000;
    public Retrofit retrofit;
    public HashMap<Class, Object> serviceCache = new HashMap<>();
    private OkHttpClient okHttpClient;

    private volatile static HttpServiceGenerator retrofitManager;
//    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();


    /*************************缓存设置*********************/
/*
   a1. noCache 不使用缓存，全部走网络

    2. noStore 不使用缓存，也不存储缓存

    3. onlyIfCached 只使用缓存

    4. maxAge 设置最大失效时间，失效则不使用 需要服务器配合

    5. maxStale 设置最大失效时间，失效则不使用 需要服务器配合 感觉这两个类似 还没怎么弄清楚，清楚的同学欢迎留言

    6. minFresh 设置有效时间，依旧如上

    7. FORCE_NETWORK 只走网络

    8. FORCE_CACHE 只走缓存*/

    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";


    //构造方法私有
    private HttpServiceGenerator() {
        //缓存
        File cacheFile = new File(AppInfoManager.getInstance().getContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

       /*设置拦截器：

        注意：addInterceptor和addNetworkInterceptor 需要同时设置。 这两者的区别可以参考Interceptors 拦截器。如果只是想实现在线缓存，那么可以只添加网络拦截器，如果只想实现离线缓存，可以使用只添加应用拦截器。两者都添加。

        如果在拦截器中统一配置，则所有的请求都会缓存。但是在实际开发中有些接口需要保证数据的实时性，那么我们就不能统一配置，这时可以这样：

        @Headers("Cache-Control: public, max-age=时间秒数")
        @GET("weilu/layout_mybusinesscard")
        Observable<Test> getData();*/
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(mRewriteCacheControlInterceptor)
                .addInterceptor(new HeadAuthenticator())
                .addNetworkInterceptor(new LoggingInterceptor())
                .cache(cache)
                .build();

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(HttpUrlConstant.APPURL)
                .build();
    }


    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static HttpServiceGenerator create(){
        if (retrofitManager == null) {

            synchronized (HttpServiceGenerator.class) {
                if (retrofitManager == null) {
                    retrofitManager = new HttpServiceGenerator();
                }
            }
        }

        return retrofitManager;
    }
    /**
     * 获取Service
     * @return Service
     */
    public static <T> T create(final Class<T> service){
        create();
        T cache = (T) retrofitManager.serviceCache.get(service);
        if(cache == null){
            cache = retrofitManager.retrofit.create(service);
            retrofitManager.serviceCache.put(service, cache);
        }
        return cache;
    }

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetWorkUtil.isConnected(AppInfoManager.getInstance().getContext())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetWorkUtil.isConnected(AppInfoManager.getInstance().getContext())) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        //.header("Cache-Control", "max-age=3600")
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

}


