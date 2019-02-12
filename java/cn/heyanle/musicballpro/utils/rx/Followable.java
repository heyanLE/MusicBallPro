package cn.heyanle.musicballpro.utils.rx;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import cn.heyanle.musicballpro.models.MainModel;
import cn.heyanle.musicballpro.utils.HeLog;

/**
 * Followable 被跟随着 （不支持多线程）
 * 绑定跟随者 发信息给跟随者 背压
 * @see Follower
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class Followable <T>{

    /**
     * 跟随者
     */
    private Follower<T> mFollower = null;

    /**
     * 缓冲列表
     */
    private List<T> mBufferList = new ArrayList<>();

    /**
     * 缓冲时间 单位MS
     */
    private int mBufferTime = 500;

    /*
    缓冲延迟结束
    发射背压延迟时候的最后一个发射请求
     */
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBufferList.size() != 0) {//如果不为空
                /*
                向Follower发送缓冲时间最后一个请求
                 */
                HeLog.i("发送信息",mBufferList.get(mBufferList.size() - 1).toString(),this);
                mFollower.onFollowableReceive(mBufferList.get(mBufferList.size() - 1));
            }
            mBufferList.clear();//清空缓冲区
        }
    };

    /**
     * 请求发送
     * 最终能否被发送取决于缓冲机制
     * @param msg       信息
     */
    public void requestSend(T msg){

        HeLog.i("请求发送信息",msg.toString(),this);

        if (mBufferList.size() == 0){//如果缓冲区为空 则进入缓冲模式
            HeLog.i("进入缓冲模式",mBufferTime + "",this);
            mHandler.postDelayed(mRunnable,mBufferTime);
        }

        mBufferList.add(msg);//将请求放入缓冲区

    }

    /**
     * 绑定跟随者
     * @param follower      跟随者
     * @return              链式调用
     */
    public Followable<T> followBy(Follower<T> follower){
        mFollower = follower;
        return this;
    }

    /**
     * 设置延迟时间
     * @param bufferTime    延迟时间
     * @return              链式调用
     */
    public Followable<T> bufferTime(int bufferTime){
        mBufferTime = bufferTime;
        return  this;
    }

    public Followable(){
        mBufferList.clear();
    }

}
