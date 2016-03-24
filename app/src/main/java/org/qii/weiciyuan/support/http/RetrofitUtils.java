package org.qii.weiciyuan.support.http;

import org.qii.weiciyuan.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * FIXME
 *
 * @author feicien (ithcheng@gmail.com)
 * @since 2016-03-22 19:24
 */
public class RetrofitUtils {

    public static WeiBoService createWeiBoService() {
        return initRetrofit("https://api.weibo.com/2/").create(WeiBoService.class);
    }

    private static Retrofit initRetrofit(String baseUrl) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }
}
