package cn.heyanle.musicballpro.view.services;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

import cn.heyanle.musicballpro.MainProcurator;
import cn.heyanle.musicballpro.bean.FunctionInfo;
import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.models.MainModel;
import cn.heyanle.musicballpro.models.MusicModel;
import cn.heyanle.musicballpro.models.WhiteModel;
import cn.heyanle.musicballpro.models.notification.NotificationModel;
import cn.heyanle.musicballpro.presenters.notification.BallPresenter;
import cn.heyanle.musicballpro.presenters.notification.BallViewPresenter;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Followable;
import cn.heyanle.musicballpro.utils.rx.Follower;
import cn.heyanle.musicballpro.utils.rx.Observer;

/**
 * NotificationService
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
public class NotificationService extends NotificationListenerService
        implements RemoteController.OnClientUpdateListener{

    boolean isHandler = false;
    String topPackageName = "";
    public static boolean isShow = true;

    boolean isKeyboardShow = false;

    Followable<Boolean> followable = new Followable<>();

    Observer<String> observer = new Observer<String>() {
        @Override
        public void onReceive(String msg) {
            List<String> show = new ArrayList<>();
            WhiteModel.getInstance().cloneShowWhite(show);
            if (!show.isEmpty()){
                followable.requestSend(true);
            }
        }
    };

    Observer<MainModel.DataChangeInfo> observerMain = new Observer<MainModel.DataChangeInfo>() {
        @Override
        public void onReceive(MainModel.DataChangeInfo msg) {
            if (msg.what.equals(MainModel.AVOID_KEYBOARD)){
                Boolean b = (Boolean) msg.obj;
                if (b){
                    followable.requestSend(true);
                }
            }
        }
    };

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            List<String> show = new ArrayList<>();
            WhiteModel.getInstance().cloneShowWhite(show);

            if (hasPermission() && ! show.isEmpty()) {
                UsageStatsManager m = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now-500, now);

                if (stats != null) {
                    TreeMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    //start = System.currentTimeMillis();
                    for (UsageStats usageStats : stats) {
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    //LogUtil.e(TAG, "isFirst=" + isFirst + ",mySortedMap cost:" + (System.currentTimeMillis() - start));
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {

                        NavigableSet<Long> keySet = mySortedMap.navigableKeySet();
                        Iterator iterator = keySet.descendingIterator();
                        while (iterator.hasNext()) {
                            UsageStats usageStats = mySortedMap.get(iterator.next());
                            Field mLastEventField = null;
                            if (mLastEventField == null) {
                                try {
                                    mLastEventField = UsageStats.class.getField("mLastEvent");
                                } catch (NoSuchFieldException e) {
                                    break;
                                }
                            }
                            if (mLastEventField != null) {
                                int lastEvent = 0;
                                try {
                                    lastEvent = mLastEventField.getInt(usageStats);
                                } catch (IllegalAccessException e) {
                                    break;
                                }
                                if (lastEvent == 1) {
                                    topPackageName = usageStats.getPackageName();
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (topPackageName == null) {
                            topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                        }
                    }
                }

                if (WhiteModel.getInstance().isShowWhite(topPackageName)){

                    isShow = false;
                    ballViewPresenter.dismiss();

                }else if (! isShow){
                    isShow = true;
                    if (!MusicModel.getInstance().getNowMusic().isEmpty()){
                        ballViewPresenter.show();
                    }
                }
                //HeLog.i("TopPackageName",topPackageName,this);
                //handler.postDelayed(runnable,300);

            }

            if (MainModel.getInstance().isAvoidKeyboard()){
                if (isKeyboardShow && !isKeyboardShow()){
                    //隐藏
                    isKeyboardShow = isKeyboardShow();
                    ballPresenter.moveBack();

                }
                if (!isKeyboardShow && isKeyboardShow()){
                    isKeyboardShow = isKeyboardShow();
                    //显示
                    ballPresenter.aviodKeyboard();
                }
            }

            //只有在显示白名单非空或者开启避让输入法时才循环调用
            if (!show.isEmpty() || MainModel.getInstance().isAvoidKeyboard()) {
                //HeLog.i("isShowKeyboard", isKeyboardShow() + "", this);
                handler.postDelayed(runnable, 300);
                isHandler = true;
            }else{
                isHandler = false;
            }
        }
    };

    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }


    private boolean isKeyboardShow(){
        try {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            Class clazz = imm.getClass();
            Method method = clazz.getDeclaredMethod("getInputMethodWindowVisibleHeight", null);
            method.setAccessible(true);
            int height = (Integer) method.invoke(imm, null);
            return height > 100;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }



    private BallPresenter.BallListener ballListener = new BallPresenter.BallListener() {
        @Override
        public void onUpSlide() {
            if (!MusicModel.getInstance().getNowMusic().isEmpty()){
                try {
                    MusicModel.getInstance().getNowMusic().getLast().run();
                    HeLog.i("上滑",this);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onDownSlide() {
            if (!MusicModel.getInstance().getNowMusic().isEmpty()){
                try {
                    MusicModel.getInstance().getNowMusic().getNext().run();
                    HeLog.i("下滑",this);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onClick() {
            HeLog.i("点击",this);
            if (!MusicModel.getInstance().getNowMusic().isEmpty()){
                try {
                    HeLog.i("点击",this);
                    MusicModel.getInstance().getNowMusic().getClick().run();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onHorizontalSlide() {
            if (!MusicModel.getInstance().getNowMusic().isEmpty()){
                try {
                    MusicModel.getInstance().getNowMusic().getMusicPage().run();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    };

    private BallViewPresenter ballViewPresenter;
    private BallPresenter ballPresenter;

    private MainProcurator.OnRestart restart = new MainProcurator.OnRestart() {
        @Override
        public void restart() {
            //初始化并传给监察者
            ballViewPresenter = new BallViewPresenter(NotificationService.this);
            //ballViewPresenter.init();
            ballPresenter = new BallPresenter(NotificationService.this,ballViewPresenter);
            //ballPresenter.init();
            MainProcurator.getInstance().setBallPresenter(ballPresenter);
            ballPresenter.setListener(ballListener);
        }
    };

    private NotificationModel notificationModel;


    @Override
    @SuppressWarnings("unchecked")
    public void onCreate() {
        super.onCreate();

        //初始化并传给监察者
        ballViewPresenter = new BallViewPresenter(this);
        //ballViewPresenter.init();
        ballPresenter = new BallPresenter(this,ballViewPresenter);
        //ballPresenter.init();
        MainProcurator.getInstance().setBallPresenter(ballPresenter);
        MainProcurator.getInstance().setRestart(restart);

        notificationModel = new NotificationModel(this);
        notificationModel.init();

        ballPresenter.setListener(ballListener);

        registerRemoteController();


        List<String> show = new ArrayList<>();
        WhiteModel.getInstance().cloneShowWhite(show);
        //只有在显示白名单非空或者开启避让输入法时才循环调用
        if (!show.isEmpty() || MainModel.getInstance().isAvoidKeyboard()) {
            //HeLog.i("isShowKeyboard", isKeyboardShow() + "", this);
            handler.postDelayed(runnable, 300);
            isHandler =true;
        }

        HeLog.i("notification","onCreate",this);

        Follower<Boolean> follower = new Follower<Boolean>() {
            @Override
            public void onReceive(Boolean msg) {
                super.onReceive(msg);
                if (msg && !isHandler){
                    handler.postDelayed(runnable, 300);
                }
            }
        };


        followable.followBy(follower);

        MainModel.getInstance().addOnDataChangeListener(observerMain);

        WhiteModel.getInstance().addObserver(observer);



    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        HeLog.i("onNotificationPosted",sbn.getPackageName(),this);
        notificationModel.onNotificationPosted(sbn);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        notificationModel.onNotificationRemoved(sbn);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ballPresenter.onDestroy();
        ballViewPresenter.onDestroy();
        MainProcurator.getInstance().setRestart(null);
        MainModel.getInstance().removeDataChangeListener(observerMain);
        WhiteModel.getInstance().removeObserver(observer);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ballPresenter != null) {
            ballPresenter.onConfigurationChanged();
        }
    }

    //==================================音乐API ============================

    RemoteController remoteController;
    MediaSessionManager mediaSessionManager ;

    public void registerRemoteController() {
        mediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        remoteController = new RemoteController(this, this);
        boolean registered;
        try {
            registered = ((AudioManager) getSystemService(AUDIO_SERVICE))
                    .registerRemoteController(remoteController);
        } catch (NullPointerException e) {
            registered = false;
        }
        if (registered) {
            try {
                remoteController.setArtworkConfiguration(500,500);
                remoteController.setSynchronizationMode(RemoteController.POSITION_SYNCHRONIZATION_CHECK);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClientChange(boolean clearing) {
        HeLog.i("nnnnnnnnnnn OnClientChange","clearing : "+clearing,this);
    }

    @Override
    public void onClientPlaybackStateUpdate(int state) {
        HeLog.i("nnnnnnnnnnn onClientPlaybackStateUpdate","state : "+state);
    }

    @Override
    public void onClientPlaybackStateUpdate(final int state, long stateChangeTimeMs, long currentPosMs, float speed) {
        HeLog.i("nnnnnnnnnnn onClientPlaybackStateUpdate","state : "+state,this);

        //暂停 -> 播放 3
        //播放 -> 暂停 2

        if (state == 3){
            nM.setPlaying(true);
        }else{
            nM.setPlaying(false);
        }


        List<MediaController> l = mediaSessionManager.getActiveSessions(new ComponentName(this,getClass()));
        if (!l.isEmpty()){

            MediaController controller = l.get(0);
            final String packageName = controller.getPackageName();
            if (!notificationModel.isHaveNotificationData(packageName) && !WhiteModel.getInstance().isMusicWhite(packageName)){

                nM.setClick(new FunctionInfo() {
                    @Override
                    public void run() {
                        sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                        MusicModel.getInstance().getFollowable().requestSend(MusicInfo.getEmpty());
                    }
                });

                nM.setNext(new FunctionInfo() {
                    @Override
                    public void run()  {
                        sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                    }
                });

                nM.setLast(new FunctionInfo() {
                    @Override
                    public void run()  {
                        sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                    }
                });

                nM.setMusicPage(new FunctionInfo() {
                    @Override
                    public void run(){
                        PackageManager packageManager = getPackageManager();
                        Intent intent;
                        intent = packageManager.getLaunchIntentForPackage(packageName);
                        if (intent != null){
                            startActivity(intent);
                        }
                    }
                });

                if (state == 3) {
                    MusicModel.getInstance().getFollowable().requestSend(nM);
                }else{
                    MusicModel.getInstance().getFollowable().requestSend(MusicInfo.getEmpty());
                }

            }

        }
    }

    @Override
    public void onClientTransportControlUpdate(int transportControlFlags) {
        HeLog.i("nnnnnnnnnnn onClientTransportControlUpdate","transportControlFlags : "+transportControlFlags,this);
    }

    MusicInfo nM = MusicInfo.getEmpty();


    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
        HeLog.i("editor",metadataEditor.toString(),this);


        String title = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE,"");
        String singer = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST,"");
        Bitmap bitmap = metadataEditor.

                getBitmap(RemoteController.MetadataEditor.BITMAP_KEY_ARTWORK,null);

        HeLog.i("nnnnnnnnnnn onClientMetadateUpdate",title + " | " + singer,this);

        Drawable cover = null;
        if (bitmap != null){
            cover = new BitmapDrawable(getResources(),bitmap);
        }

        nM.name(title).singer(singer).albumCover(cover);
    }

    public void sendMusicKeyEvent(int keyCode) {
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent key = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
        dispatchMediaKeyToAudioService(key);
        dispatchMediaKeyToAudioService(KeyEvent.changeAction(key, KeyEvent.ACTION_UP));
    }

    private void dispatchMediaKeyToAudioService(KeyEvent event) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager != null) {
            try {
                audioManager.dispatchMediaKeyEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
