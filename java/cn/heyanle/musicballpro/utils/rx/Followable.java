package cn.heyanle.musicballpro.utils.rx;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Rx组件 被跟随者(不支持多线程)
 * 绑定跟随者 更新信息 背压 链式调用
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
public class Followable <T> {

    private Follower<T> f = null;

    private int backPressureDelay = 500;//背压延迟时间 单位ms
    private List<T> backPressureTemporary = new ArrayList<>();//发送事件暂存器（为了支持背压）

    private boolean isDelay = false;//当前是否为背压延迟状态

    /*
    背压延迟结束
    发射背压延迟时候的最后一个发射请求
     */
    private Handler handler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (backPressureTemporary.size()!=0) {
                f.onReceive(backPressureTemporary.get(backPressureTemporary.size() - 1));
            }
            backPressureTemporary.clear();
            isDelay = false;
        }
    };

    /**
     * 绑定跟随者
     * @param f     跟随者
     * @return      链式调用
     */
    public Followable<T> followedBy(Follower<T> f){
        this.f = f;
        return this;
    }

    /**
     * 请求发射（最终能否被发射取决于背压机制）
     * @param msg   发射的信息
     */
    public void requestSend(T msg){
        if (! isDelay ){

            isDelay = true;
            handler.postDelayed(r,backPressureDelay);

        }
        backPressureTemporary.add(msg);

    }

    /**
     * 设置背压延迟时间
     * @param delayTime     背压延迟时间
     * @return              链式调用
     */
    public Followable<T> delay(int delayTime){
        backPressureDelay = delayTime;
        return this;
    }



}
