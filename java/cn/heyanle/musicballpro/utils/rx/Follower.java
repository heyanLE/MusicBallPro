package cn.heyanle.musicballpro.utils.rx;

import cn.heyanle.musicballpro.MainProcurator;
import cn.heyanle.musicballpro.utils.HeLog;

/**
 * Follower 跟随者
 * @see Followable
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public abstract class Follower <T> {

    /**
     * 接受信息时候调用
     * @param msg       信息
     */
    public void onReceive(T msg){
        HeLog.i("接收信息",msg.toString(),this);
    }

    /**
     * 接收信息时候由Followable调用
     * 通知监察者
     * @param msg       信息
     */
    final void onFollowableReceive(T msg){

        //HeLog.i("接收信息",msg.toString(),this);
        onReceive(msg);
        MainProcurator.getInstance().work();

    }

}
