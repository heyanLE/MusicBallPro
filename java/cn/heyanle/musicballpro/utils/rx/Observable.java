package cn.heyanle.musicballpro.utils.rx;

import java.util.ArrayList;
import java.util.List;

import cn.heyanle.musicballpro.utils.HeLog;

/**
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class Observable <T> {

    private List<Observer<T>> mObserverList = new ArrayList<>();

    public void update(T msg){

        for (Observer<T> observer : mObserverList){
            observer.onObserverReceive(msg);
            HeLog.i("O发送数据",msg.toString(),this);
        }

    }

    public void observedBy (Observer<T> observer){
        mObserverList.add(observer);
    }

    public void unObservedBy(Observer<T> observer){
        mObserverList.remove(observer);
    }

}
