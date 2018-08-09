package news.dvlp.testretrofit.wxlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import news.dvlp.testretrofit.R;
import news.dvlp.testretrofit.observer.ObserverListenner;
import news.dvlp.testretrofit.observer.ObserversManager;

import static android.content.ContentValues.TAG;

/**
 * Created by liubaigang on 2018/8/8.
 */

public class WxShareAndLoginUtils {
    public static int WECHAT_FRIEND = 0;  //分享好友
    public static int WECHAT_MOMENT = 1;  //分享朋友圈
    private static IWXAPI iwxapi;
    private static String appId="wx6397da1a5719b713";
    private static WXMediaMessage weixiMsg;

    private static Context mContext;
    public static ObserverListenner observerListenner;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    doShareToCirlceForHandler();
                    break;
                case 0:
                    doShareToFriendsForHandler();
                    break;
            }

        }
    };

    public static IWXAPI getWXAPI(Context context){
        if (iwxapi == null){
            //通过WXAPIFactory创建IWAPI实例
            iwxapi = WXAPIFactory.createWXAPI(context, appId, false);
            //将应用的appid注册到微信
            iwxapi.registerApp(appId);
        }
        return iwxapi;
    }

    public static void setWxSharedFinishListenner(ObserverListenner listenner){
        observerListenner=listenner;
        ObserversManager.getInstance().addObserver("WXFINISH_CALLBACK",listenner);
    }
    /**
     * 微信登录
     */
    public static void WXLogin(Context context, ObserverListenner listenner){
        ObserversManager.getInstance().addObserver("WXFINISH",listenner);
//        iwxapi = WXAPIFactory.createWXAPI(context, appId, true);
//        iwxapi.registerApp(appId);mContext
        mContext=context;
        if (!judgeCanGo(context)){
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        iwxapi.sendReq(req);
    }

    /**
     * 分享文本至朋友圈
     * @param text  文本内容
     * @param judge 类型选择 好友-WECHAT_FRIEND 朋友圈-WECHAT_MOMENT
     */
    public static void WxTextShare(Context context,String text,int judge){
        mContext=context;
        if (!judgeCanGo(context)){
            return;
        }
        //初始化WXTextObject对象，填写对应分享的文本内容
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;
        //初始化WXMediaMessage消息对象，
        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = textObject;
        message.description = text;
        //构建一个Req请求对象
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());   //transaction用于标识请求
        req.message = message;
        req.scene = judge;      //分享类型 好友==0 朋友圈==1
        //发送请求
        iwxapi.sendReq(req);
    }

    /**
     *  分享图片
     * @param imgId 图片bitmap,建议别超过32k
     * @param judge 类型选择 好友-WECHAT_FRIEND 朋友圈-WECHAT_MOMENT
     */
    public static void WxBitmapShare(Context context, int imgId,int judge){
        mContext=context;

        if (!judgeCanGo(context)){
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgId);

        WXImageObject wxImageObject = new WXImageObject(bitmap);
        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = wxImageObject;

        Bitmap thunmpBmp = Bitmap.createScaledBitmap(bitmap,50,50,true);
        bitmap.recycle();
        message.thumbData = Util.bmpToByteArray(thunmpBmp,true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
        req.scene = judge;

        iwxapi.sendReq(req);

    }
    /**
     *  分享图片
     * @param imgUrl 图片bitmap,建议别超过32k
     * @param judge 类型选择 好友-WECHAT_FRIEND 朋友圈-WECHAT_MOMENT
     */
    public static void WxBitmapShare(Context context, final String imgUrl, final int judge){
        mContext=context;

        if (!judgeCanGo(context)){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WXWebpageObject webpage = new WXWebpageObject();

                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(imgUrl).openStream());
                    WXImageObject wxImageObject = new WXImageObject(bitmap);
                    weixiMsg = new WXMediaMessage(webpage);
                    weixiMsg.mediaObject = wxImageObject;

                    Bitmap thunmpBmp = Bitmap.createScaledBitmap(bitmap,50,50,true);
                    bitmap.recycle();
                    weixiMsg.thumbData = Util.bmpToByteArray(thunmpBmp,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(judge);
            }
        }).start();


    }

    /**
     * 网页分享 ：图片来源于本地
     * @param url  地址
     * @param title 标题
     * @param desc  描述
     * @param  imgId 图片资源
     * @param judge  类型选择 好友-WECHAT_FRIEND 朋友圈-WECHAT_MOMENT
     */
    public static void WxUrlShare(Context context, String title, String desc, String url, int imgId, int judge) {
        mContext=context;
        if (!judgeCanGo(context)){
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "" + url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "" + title;
        msg.description = "" + desc;
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),  imgId);
        msg.thumbData = Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = judge;
        iwxapi.sendReq(req);

    }
    /**
     * 网页分享 ：图片来源于网络
     * @param url  地址
     * @param title 标题
     * @param desc  描述
     * @param imgUrl  图片网络地址
     * @param judge  类型选择 好友-WECHAT_FRIEND 朋友圈-WECHAT_MOMENT
     */
    public static void WxUrlShare(Context context, String title, String desc, String url, final String imgUrl, final int judge) {

        mContext=context;
        if (!judgeCanGo(context)){
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        weixiMsg = new WXMediaMessage(webpage);
        webpage.webpageUrl = "" + url;
        weixiMsg.title = "" + title;
        weixiMsg.description = "" + desc;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap thumb = BitmapFactory.decodeStream(new URL(imgUrl).openStream());
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 120, 150, true);
                    thumb.recycle();
                    weixiMsg.thumbData = bitmap2Bytes(thumbBmp, 32);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(judge);
            }
        }).start();
    }


//    public static void share(Context context,String title,String titlle_detail,String imgUrl,final int judge)throws MalformedURLException {
//
//        if (!judgeCanGo(context)){
//            return;
//        }
//        Log.e(TAG,"share##########################");
//
//        Log.e(TAG,imgUrl);
//
//        WXWebpageObject webpage =new WXWebpageObject();
//
//        webpage.webpageUrl="https://fir.im/5et2";
//
//        WXMediaMessage msg =new WXMediaMessage(webpage);
//
//        msg.title= title;
//
//        msg.description= titlle_detail;
//
//
////加载本地图片
//
////        Bitmap thumb = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.zhuye_tip);
//
////        msg.setThumbImage(thumb);
//
////        thumb.recycle();
//
//
//
//
//
////加载网络图片********
//
////注意下方的压缩
//
//        try{
//
//            Bitmap thumb = BitmapFactory.decodeStream(new URL(imgUrl).openStream());
//
////注意下面的这句压缩，120，150是长宽。
//
////一定要压缩，不然会分享失败
//
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb,120,150,true);
//
////Bitmap回收
//
//            thumb.recycle();
//
//            msg.thumbData= Util.bmpToByteArray(thumbBmp,true);
//
////            msg.setThumbImage(thumb);
//
//        }catch(IOException e) {
//
//            e.printStackTrace();
//
//        }
//
//        Log.e(TAG,msg.title);
//
//        Log.e(TAG,msg.description);
//
////构造Req
//
//        SendMessageToWX.Req req =new SendMessageToWX.Req();
//
//        req.transaction=String.valueOf(System.currentTimeMillis());
//
//        req.message= msg;
//
//        Log.e(TAG,judge+"");
//
//
//
//        req.scene= judge;
//
//
//
//
//
//        iwxapi.sendReq(req);//发送到微信
//
//        Log.e(TAG,"share###### END ####################");
//
//    }


    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb
     *
     * @param bitmap
     * @return http://blog.csdn.net/mq2856992713/article/details/52901525
     */
    private static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }
    /**
     * 网页分享(官方的的)
     * @param url  地址
     * @param title 标题
     * @param description  描述
     * @param judge  类型选择 好友-WECHAT_FRIEND 朋友圈-WECHAT_MOMENT
     */
    public static void WxUrlShareGf(Context context,String url,String title,String description,String imgUrl,int judge){
        mContext=context;

        if (!judgeCanGo(context)){
            return;
        }
        Bitmap bitmap = getBitMBitmap(imgUrl);
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = url;

        WXMediaMessage wxMediaMessage = new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.title = title;
        wxMediaMessage.description = description;
        Bitmap thunmpBmp = Bitmap.createScaledBitmap(bitmap,50,50,true);
        bitmap.recycle();
        wxMediaMessage.thumbData = Util.bmpToByteArray(thunmpBmp,true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;
        req.scene = judge;

        iwxapi.sendReq(req);
    }


    /**
     * 判断是否微信支持
     * @param context
     * @return
     */
    private static boolean judgeCanGo(Context context){
        getWXAPI(context);
        if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(context, "请先安装微信应用", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!iwxapi.isWXAppSupportAPI()) {
            Toast.makeText(context, "请先更新微信应用", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /**
     * 图片url转bitmap
     */
    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void doShareToCirlceForHandler() {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = weixiMsg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        iwxapi.sendReq(req);
    }
    private static void doShareToFriendsForHandler() {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = weixiMsg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        iwxapi.sendReq(req);
    }
    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
