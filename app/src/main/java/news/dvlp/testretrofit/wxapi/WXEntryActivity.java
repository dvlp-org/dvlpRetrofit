package news.dvlp.testretrofit.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.net.URLEncoder;

import news.dvlp.testretrofit.observer.ObserversManager;
import news.dvlp.testretrofit.retrofit.RetrofitClient;
import news.dvlp.testretrofit.retrofit.RetrofitService;
import news.dvlp.testretrofit.wxlib.WXLoginBean;
import news.dvlp.testretrofit.wxlib.WXUserBean;
import news.dvlp.testretrofit.wxlib.WxCallBack;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liubaigang on 2018/8/7.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    public int WX_LOGIN = 1;
    private IWXAPI api;
    private BaseResp resp = null;
    private String WX_APP_ID = "wx6397da1a5719b713";
    // 获取第一步的code后，请求以下链接获取access_token
    private String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    // 获取用户个人信息
    private String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
    private String WX_APP_SECRET = "f284e074788df336ae9cd3195aa97ca6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        Toast.makeText(this, "    // 微信发送请求到第三方应用时，会回调到该方法\n", Toast.LENGTH_LONG).show();

        finish();
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {

        String result = "";
        if (resp != null) {
            resp = resp;
        }
        WxCallBack callBack=new WxCallBack();
        //微信登录为getType为1，分享为0
        if (resp.getType() == WX_LOGIN) {
            callBack.setCallType(1);
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = "发送成功";
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    String code = ((SendAuth.Resp) resp).code;


                    //将你前面得到的AppID、AppSecret、code，拼接成URL 获取access_token等等的信息(微信)
                    String get_access_token = getCodeRequest(code);
                    Log.e("打印返回的json数据", get_access_token + "-------------get_access_token");
                    authPerssion(get_access_token);

                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = "发送取消";
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = "发送被拒绝";
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    finish();
                    break;
                default:
                    result = "发送返回";
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        } else {
            callBack.setCallType(0);
            //分享成功回调
            System.out.println("------------分享回调------------");
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    //分享成功
                    result = "分享成功";
                    Toast.makeText(WXEntryActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    //分享取消
                    result = "分享取消";
                    Toast.makeText(WXEntryActivity.this, "分享取消", Toast.LENGTH_LONG).show();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    //分享拒绝
                    result = "分享拒绝";
                    Toast.makeText(WXEntryActivity.this, "分享拒绝", Toast.LENGTH_LONG).show();
                    break;
                default:
                    result = "分享返回";
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    break;
            }
        }
        callBack.setCode(resp.errCode);
        callBack.setMes(result);
        ObserversManager.getInstance().sendMessage("WXFINISH_CALLBACK",callBack);
        finish();
    }


    /**
     * 获取授权信息
     *
     * @param get_access_token 请求的全地址
     */
    private void authPerssion(String get_access_token) {
        RetrofitClient.getHttpClient();
        Call<WXLoginBean> favourables = RetrofitClient.getInstance()
                .create(RetrofitService.class)
                .getLogin(get_access_token);
        favourables.enqueue(new Callback<WXLoginBean>() {
            @Override
            public void onResponse(Call<WXLoginBean> call, Response<WXLoginBean> response) {
                WXLoginBean message = response.body();

                Log.e("获取微信授权信息", message.getAccess_token() + "-------------" + message.getOpenid());

                //拼接Url
                String get_user_info_url = getUserInfo(message.getAccess_token(), message.getOpenid());
                //请求微信登录信息
                getUserInfo(get_user_info_url);
            }

            @Override
            public void onFailure(Call<WXLoginBean> call, Throwable throwable) {
                Log.e("打印返回的json数据2", throwable.getMessage() + "-------------");

            }
        });
    }


    /**
     * 通过拼接的用户信息url获取用户信息
     *
     * @param
     */
    private void getUserInfo(String user_info_url) {
        Call<WXUserBean> favourables = RetrofitClient.getInstance()
                .create(RetrofitService.class)
                .getLoginUser(user_info_url);
        favourables.enqueue(new Callback<WXUserBean>() {
            @Override
            public void onResponse(Call<WXUserBean> call, Response<WXUserBean> response) {
                WXUserBean message = response.body();

                Log.e("获取用户信息", message.getNickname() + "-------------" + message.getHeadimgurl());

                ObserversManager.getInstance().sendMessage("WXFINISH", message);
            }

            @Override
            public void onFailure(Call<WXUserBean> call, Throwable throwable) {
                Log.e("打印返回的json数据2", throwable.getMessage() + "-------------");

            }
        });
//
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    /**
     * 获取access_token的URL（微信）
     *
     * @param code 授权时，微信回调给的
     * @return URL
     */
    private String getCodeRequest(String code) {
        String result = null;
        GetCodeRequest = GetCodeRequest.replace("APPID",
                urlEnodeUTF8(WX_APP_ID));
        GetCodeRequest = GetCodeRequest.replace("SECRET",
                urlEnodeUTF8(WX_APP_SECRET));
        GetCodeRequest = GetCodeRequest.replace("CODE", urlEnodeUTF8(code));
        result = GetCodeRequest;
        return result;
    }


    /**
     * 获取用户个人信息的URL（微信）
     *
     * @param access_token 获取access_token时给的
     * @param openid       获取access_token时给的
     * @return URL
     */
    private String getUserInfo(String access_token, String openid) {
        String result = null;
        GetUserInfo = GetUserInfo.replace("ACCESS_TOKEN",
                urlEnodeUTF8(access_token));
        GetUserInfo = GetUserInfo.replace("OPENID", urlEnodeUTF8(openid));
        result = GetUserInfo;
        return result;
    }

    private String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
