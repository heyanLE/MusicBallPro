package cn.heyanle.musicballpro.models;

import java.util.ArrayList;
import java.util.List;

import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Followable;
import cn.heyanle.musicballpro.utils.rx.Follower;
import cn.heyanle.musicballpro.utils.rx.Observable;
import cn.heyanle.musicballpro.utils.rx.Observer;

/**
 * 音乐内容提供器 MusicModel 单例模式
 * 保存当前音乐信息MusicInfo
 * 监听音乐信息更新
 * 通过Followable从上级（通知内容提供器和音乐API）接受音乐信息更新
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class MusicModel {

    /**
     * 当前音乐信息
     */
    private MusicInfo mMusicInfo = MusicInfo.getEmpty();

    /**
     * 音乐改变监听
     */
    private Observable<MusicInfo> mObservable;

    /**
     * 让上级通过该被跟随者更新音乐状态
     */
    private Followable<MusicInfo> mFollowable;

    /**
     * 接受上级发来多更新状态
     */

    private Follower<MusicInfo> mFollower = new Follower<MusicInfo>() {

        @Override
        public void onReceive(MusicInfo musicInfo){
            super.onReceive(musicInfo);
            HeLog.i("GetMusiciiiiiiiiiiiiiii",mMusicInfo.toString(),this);
            if (musicInfo == null){
                HeLog.i("GetMusic","null",this);
                mMusicInfo = MusicInfo.getEmpty();
            }

            mMusicInfo = musicInfo;
            mObservable.update(mMusicInfo);

        }

    };

    /**
     * 添加监听器
     * @param listener      监听器
     */
    public void addOnMusicChangeListener(Observer<MusicInfo> listener){
        mObservable.observedBy(listener);
    }

    /**
     * 移除监听器
     * @param listener      监听器
     */
    public void removeOnMusicChangeListener(Observer<MusicInfo> listener){
        mObservable.unObservedBy(listener);
    }

    /**
     * 获取Followable对象
     * @return      Followable
     */
    public Followable<MusicInfo> getFollowable(){
        return mFollowable;
    }

    public MusicInfo getNowMusic(){

        return mMusicInfo;

    }

    //=====================单例模式=========================================

    private static MusicModel INSTANCE = null;

    public static MusicModel getInstance(){
        return INSTANCE;
    }

    /**
     * 初始化MusicModel
     * 通过反射调用
     */
    private void init(){

        //mListeners.clear();//清空监听器
        mMusicInfo = MusicInfo.getEmpty();//空音乐
        mFollowable = new Followable<MusicInfo>().bufferTime(500).followBy(mFollower);//设置Rx
        mObservable = new Observable<>();
        HeLog.i("MusicModel","init",this);
        INSTANCE = this;


    }

    /**
     * 私有化构造方法
     * 通过反射调用
     */
    private MusicModel(){}

}
