package news.dvlp.testretrofit.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubaigang on 2018/6/13.
 *
 * 目标类
 */
public class Subject {


    List<ObserverListenner> observers = new ArrayList<ObserverListenner>();


    /**
     * 构建一个新的{@code Observable} 对象
     */
    public Subject() {
    }


    /**
     * 添加指定的观察者的观察人士。如果它已经
     * 注册,不添加一次。
     *
     * @param observer 添加观察者
     */
    public void addObserver(ObserverListenner observer) {
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        synchronized (this) {
            if (!observers.contains(observer))
                observers.add(observer);
        }
    }


    /**
     * 返回观察者注册数量
     *
     * @return 观察者数量.
     */
    public int countObservers() {
        return observers.size();
    }

    /**
     * 返回观察者集合
     *
     * @return observers.
     */
    public List<ObserverListenner> observerList() {
        return observers;
    }

    /**
     * 从列表中删除指定的观察者的观察员
     *
     * @param observer
     */
    public synchronized void deleteObserver(ObserverListenner observer) {
        observers.remove(observer);
    }

    /**
     * 从列表中移除所有观察家的观察员
     */
    public synchronized void deleteObservers() {
        observers.clear();
    }


    /**
     * 发送指定类别通知
     *
     * @param data
     *
     */
    @SuppressWarnings("unchecked")
    public void notifyObservers(String name, Object data) {


            for (ObserverListenner observer : observers) {
                observer.onReciveMessage(name, data);
            }
        }



}


