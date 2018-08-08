package news.dvlp.testretrofit.retrofit;


import java.io.IOException;

import news.dvlp.testretrofit.App;
import news.dvlp.testretrofit.retrofit.Cookies.CookieManger;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CoderLengary
 */


public class RetrofitClient {
    private RetrofitClient() {
    }

    private static class ClientHolder{

        public static String mApi=Api.API_BASE;

        private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieManger(App.getContext()))
                .build();


        private static Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mApi)
                .client(okHttpClient)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static Retrofit getInstance(){
        return ClientHolder.retrofit;
    }

    public static OkHttpClient getHttpClientO(){
        return ClientHolder.okHttpClient;
    }
}
