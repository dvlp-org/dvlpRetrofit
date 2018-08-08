package news.dvlp.testretrofit;

import news.dvlp.testretrofit.wxlib.WXLoginBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by liubaigang on 2018/6/28.
 */

public interface WalletService {
    /**
     * 获取优惠信息的接口
     * @return 返回值
     */
    @GET("/tools/mockapi/3191/favourable")//配置的get请求
    Call<HttpBean> getFavourable();

    /**
     * 获取优惠信息的接口
     * @return 返回值
     */
    @GET("{url}")//配置的get请求
    Call<WXLoginBean> getLogin(@Path("url") String id);
}
