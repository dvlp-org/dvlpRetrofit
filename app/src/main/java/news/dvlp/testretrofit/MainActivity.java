package news.dvlp.testretrofit;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import news.dvlp.testretrofit.observer.ObserverListenner;
import news.dvlp.testretrofit.observer.ObserversManager;
import news.dvlp.testretrofit.retrofit.RetrofitClient;
import news.dvlp.testretrofit.retrofit.RetrofitService;
import news.dvlp.testretrofit.wxlib.WXLoginBean;
import news.dvlp.testretrofit.wxlib.WXUserBean;
import news.dvlp.testretrofit.wxlib.WxShareAndLoginUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements ObserverListenner{
    private TextView mTv;
    private Button mBtn,mLogin,mGetUser;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv=findViewById(R.id.str);
        mBtn=findViewById(R.id.btnHttpStart);
        mLogin=findViewById(R.id.wxLogin);
        mGetUser=findViewById(R.id.wxGetUser);
        mImageView=findViewById(R.id.imgs);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpRequest();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WXLogin();
                WxShareAndLoginUtils.WXLogin(MainActivity.this,MainActivity.this);
            }
        });
        mGetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WxShareAndLoginUtils.WxTextShare(MainActivity.this,"微信分享", WxShareAndLoginUtils.WECHAT_MOMENT);

            }
        });

        ObserversManager.getInstance().addObserver("WXFINISH",this);
    }


    /**
     * 请求测试
     */
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
        Call<WXLoginBean> favourables=  RetrofitClient.getInstance()
                .create(RetrofitService.class)
                .getFavourable("wx6397da1a5719b713","8e92089b066463369b8cc9f07a7638e8","081WPNyU1J8CUV0cJhyU1OqByU1WPNyT","authorization_code");
        favourables.enqueue(new Callback<WXLoginBean>() {
            @Override
            public void onResponse(Call<WXLoginBean> call, Response<WXLoginBean> response) {
                WXLoginBean message = response.body();
                String data=message.getErrcode();
                Log.e("打印返回的json数据2",data+"-------------");

            }

            @Override
            public void onFailure(Call<WXLoginBean> call, Throwable throwable) {
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


    @Override
    public void onReciveMessage(String name, Object object) {
        WXUserBean user= (WXUserBean) object;
        Glide.with(this).load(user.getHeadimgurl()).into(mImageView);
        mTv.setText(
                  "昵    称："+user.getNickname() +"\n"
                 +"用户标识："+user.getOpenid()+"\n"
                 +"性    别："+user.getSex()+"\n"
                 +"省    份："+user.getProvince()+"\n"
                 +"城    市："+user.getCity()+"\n"
                 +"国    家："+user.getCountry()+"\n"
                 +"特权信息："+getStr(user.getPrivilege())+"\n"
                 +"平台标识："+user.getUnionid()
        );
    }

    private String getStr(List<String>list){
        String str="";
        for (int i = 0; i <list.size() ; i++) {
            str+=list.get(i);
        }
        return str;
    }
}
