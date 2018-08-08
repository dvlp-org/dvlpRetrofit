package news.dvlp.testretrofit.observer;

/**
 * Created by liubaigang on 2018/6/13.
 *
 * 观察者接口
 */
public interface ObserverListenner {
    void onReciveMessage(String name, Object object);
}
