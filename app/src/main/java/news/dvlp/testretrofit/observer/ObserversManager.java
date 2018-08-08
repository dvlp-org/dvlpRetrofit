package news.dvlp.testretrofit.observer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by liubaigang on 2018/6/13.
 * <p>
 * 具体目标管理类
 */
public class ObserversManager {
    private HashMap<String, Subject> observers = new HashMap<>();
    private static ObserversManager INSTANCE;





    /**
     * 实例
     */
    public static ObserversManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ObserversManager();
        return INSTANCE;
    }

    /**
     * 添加观察者
     */
    public void addObserver(String name, ObserverListenner observer) {
        if (null == findList(name)) {
            Subject subject = new Subject();
            subject.addObserver(observer);
            observers.put(name, subject);
        } else {
            Subject subject = findList(name);
            subject.addObserver(observer);
        }


    }

    /**
     * 移除观察者
     */
    public void removeObserver(String name, ObserverListenner listenner) {
        Subject subject = findList(name);
        if (null != subject) {
            subject.deleteObserver(listenner);
        }
    }
    /**
     * 移除当前观察者所有观察行为
     */
    public void removeObserverAnyWhere(ObserverListenner listenner){
        Iterator iterator=observers.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry= (Map.Entry) iterator.next();
            Subject subject= (Subject) entry.getValue();
            subject.deleteObserver(listenner);
        }
    }


    /**
     * 查询多观察类别
     */
    public Subject findList(String name) {
        return observers.get(name);
    }


    /**
     * 发送消息
     */
    public void sendMessage(String name, Object object) {
        Subject subject = findList(name);
        if (null != subject) {
            subject.notifyObservers(name, object);
        }
    }
}


















