package news.dvlp.testretrofit;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import news.dvlp.testretrofit.retrofit.RetrofitClient;
import news.dvlp.testretrofit.retrofit.RetrofitService;
import news.dvlp.testretrofit.wxapi.WXLogin;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView mTv;
    private Button mBtn,mLogin,mGetUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv=findViewById(R.id.str);
        mBtn=findViewById(R.id.btnHttpStart);
        mLogin=findViewById(R.id.wxLogin);
        mGetUser=findViewById(R.id.wxGetUser);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpRequest();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXLogin();
            }
        });
        mGetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpRequest();
            }
        });
    }

    private void httpRequest(){



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.wanandroid.com")
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(new Converter.Factory() {
                    @Nullable
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        return new Converter<ResponseBody, String>() {
                            @Override
                            public String convert(ResponseBody value) throws IOException {
                                return value.string();
                            }
                        };
                    }
                })
                .build();


        WalletService service = retrofit.create(WalletService.class);
        Call<HttpBean> favourable = service.getFavourable();
        favourable.enqueue(new Callback<HttpBean>() {
            @Override
            public void onResponse(Call<HttpBean> call, Response<HttpBean> response) {
//                HttpBean message = response.body();
//                List<DataBean> data=message.getData();
                Log.e("打印返回的json数据",response.body()+"-------------");
//                mTv.setText(""+data.get(0).getActivity1() );
            }

            @Override
            public void onFailure(Call<HttpBean> call, Throwable t) {
                //请求失败的处理
                Log.e("打印返回的json数据","请求失败.............");
            }
        });

        //封装的retrofit
        String url="appid=wx6397da1a5719b713&secret=8e92089b066463369b8cc9f07a7638e8&code=081WPNyU1J8CUV0cJhyU1OqByU1WPNyT";
        Call<WXLogin> favourables=  RetrofitClient.getInstance()
                .create(RetrofitService.class)
                .getFavourable("wx6397da1a5719b713","8e92089b066463369b8cc9f07a7638e8","081WPNyU1J8CUV0cJhyU1OqByU1WPNyT","authorization_code");
        favourables.enqueue(new Callback<WXLogin>() {
            @Override
            public void onResponse(Call<WXLogin> call, Response<WXLogin> response) {
                WXLogin message = response.body();
                String data=message.getErrcode();
                Log.e("打印返回的json数据2",data+"-------------");

            }

            @Override
            public void onFailure(Call<WXLogin> call, Throwable throwable) {
                Log.e("打印返回的json数据2",throwable.getMessage()+"-------------");

            }
        });



    }



    /**
     * 登录微信
     */
    // 微信登录
    private static IWXAPI WXapi;
    private String WX_APP_ID = "wx6397da1a5719b713";
    private void WXLogin() {
        WXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        WXapi.registerApp(WX_APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        WXapi.sendReq(req);

    }



//private void reque{
//        Call<WXLogin> favourables=  RetrofitClient.getInstance()
//                .create(RetrofitService.class)
//                .getFavourable(WX_APP_ID,WX_APP_SECRET,code,"authorization_code");
//        favourables.enqueue(new Callback<WXLogin>() {
//            @Override
//            public void onResponse(Call<WXLogin> call, Response<WXLogin> response) {
//                WXLogin message = response.body();
//                String data=message.getErrcode();
//                Log.e("打印返回的json数据2",data+"-------------");
//
//            }
//
//            @Override
//            public void onFailure(Call<WXLogin> call, Throwable throwable) {
//                Log.e("打印返回的json数据2",throwable.getMessage()+"-------------");
//
//            }
//        });
//    }









}