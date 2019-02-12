package cn.heyanle.musicballpro.utils.rx;

import cn.heyanle.musicballpro.MainProcurator;

/**
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public abstract class Observer <T> {

    /**
     * 接受信息时候调用
     * @param msg       信息
     */
    public abstract void onReceive(T msg);

    /**
     * 接收信息时候由Followable调用
     * 通知监察者
     * @param msg       信息
     */
    final void onObserverReceive(T msg){

        //MainProcurator.getInstance().work();
        onReceive(msg);
        MainProcurator.getInstance().work();

    }

}
