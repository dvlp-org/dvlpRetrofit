package news.dvlp.testretrofit.retrofit;


import android.database.Observable;

import news.dvlp.testretrofit.DataBean;
import news.dvlp.testretrofit.wxlib.WXLoginBean;
import news.dvlp.testretrofit.wxlib.WXUserBean;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by CoderLengary
 */


public interface RetrofitService {

    @FormUrlEncoded
    @POST(Api.LOGIN)
    Observable<DataBean> login(@Field("username") String username, @Field("password") String password);
    @FormUrlEncoded
    @POST(Api.REGISTER)
    Observable<DataBean> register(@Field("username") String username, @Field("password") String password, @Field("repassword") String repassword);

    @GET(Api.ARTICLE_LIST + "{page}/json")
    Observable<DataBean> getArticles(@Path("page") int page);

    @GET(Api.ARTICLE_LIST + "{page}/json")
    Observable<DataBean> getArticlesFromCatg(@Path("page") int page, @Query("cid") int cid);

    @GET(Api.CATEGORIES)
    Observable<DataBean> getCategories();

    @POST(Api.QUERY_ARTICLES + "{page}/json")
    Observable<DataBean> queryArticles(@Path("page") int page, @Query("k") String k);

    @GET(Api.HOT_KEY)
    Observable<DataBean> getHotKeys();

    @GET(Api.BANNER)
    Observable<DataBean> getBanner();

    @POST(Api.COLLECT_ARTICLE+"{id}/json")
    Observable<DataBean> collectArticle(@Path("id") int id);

    @POST(Api.CANCEL_COLLECTING_ARTICLE + "{originId}/json")
    Observable<DataBean> uncollectArticle(@Path("originId") int originId);

    @GET(Api.GET_FAVORITE_ARTICLES + "{page}/json")
    Observable<DataBean> getFavoriteArticles(@Path("page") int page);

    ///上面的是例子
    /**
     * 获取优惠信息的接口
     * @return 返回值
     */
    @GET("https://api.weixin.qq.com/sns/oauth2/access_token")//配置的get请求
    Call<WXLoginBean> getFavourable(@Query("appid") String appid, @Query("secret") String secret, @Query("code") String code, @Query("grant_type") String grant_type);


    @GET("http://www.wanandroid.com/tools/mockapi/{id}/favourable")//配置的get请求
    Call<String> getFavourableString(@Path("id") String id);

    /**
     * 获取优惠信息的接口
     * @return 返回值
     */
    @GET//配置的get请求
    Call<WXLoginBean> getLogin(@Url String url);

    /**
     * 获取微信用户信息
     * @return 返回值
     */
    @GET//配置的get请求
    Call<WXUserBean> getLoginUser(@Url String url);
}
