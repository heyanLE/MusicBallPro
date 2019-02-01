package cn.heyanle.musicballpro.model;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Followable;
import cn.heyanle.musicballpro.utils.rx.Follower;

/**
 * MusicModel 单例模式
 * Created by HeYanLe
 * 2019/1/30 0030
 * https://github.com/heyanLE
 */
public class MusicModel {

    public interface OnMusicChangeListener{

        void onMusicChange(MusicInfo musicInfo);

    }

    /**
     * 音乐改变监听器
     */
    private List<OnMusicChangeListener> listeners = new ArrayList<>();


    private Followable<MusicInfo> followable = new Followable<>();

    /**
     * Follower对象 更新MusicInfo
     */
    private Follower<MusicInfo> musicInfoFollower = new Follower<MusicInfo>() {
        @Override
        public void onReceive(MusicInfo what) {
            nowMusic = what;
            if (what!= null)
            HeLog.i("newMusic",nowMusic.toString(),this);
            for(OnMusicChangeListener listener:listeners){
                listener.onMusicChange(what);
                HeLog.i("MusicListener",listener.toString(),this);
            }
        }
    };

    /**
     * 当前音乐
     */
    private MusicInfo nowMusic = new MusicInfo().setPlaying(false);

    public MusicInfo getNowMusic() {
        return nowMusic;
    }

    public void addOnMusicChangeListener(OnMusicChangeListener listener){

        listeners.add(listener);
        HeLog.i("listener.add",listener.toString(),this);
    }

    public void removeMusicChangeListener(OnMusicChangeListener listener){
        listeners.remove(listener);
    }

    public Followable<MusicInfo> getFollowable() {
        return followable;
    }

    //以下为单例模式相关写法 =======================================================

    //private Context context;

    /**
     * OnStart（通过反射调用）
     */
    @SuppressLint("CommitPrefEdits")
    private void onStart(){

        /*
        如果为INSTANCE为Null 开始初始化
         */
        if (INSTANCE == null) {
            //this.context = context;
            HeLog.i("MusicModel","init",this);
            INSTANCE = this;


            followable.delay(800).followedBy(musicInfoFollower);

        }

    }

    /**
     * 获取单例对象
     * @return          单例对象
     */
    public static MusicModel getInstance(){
        return INSTANCE;
    }

    /**
     * 单例对象保存
     */
    @SuppressLint("StaticFieldLeak")
    private static MusicModel INSTANCE = null;

    /**
     * 私有化构造方法，防止外部实例化
     */
    private MusicModel(){}

}
