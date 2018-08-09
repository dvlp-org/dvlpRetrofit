package news.dvlp.testretrofit.wxlib;

/**
 * Created by liubaigang on 2018/8/9.
 */

public class WxCallBack {
    private int code;//微信状态值
    private String mes;//回调说明信息
    private int callType;//分享0，登录1

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }
}
