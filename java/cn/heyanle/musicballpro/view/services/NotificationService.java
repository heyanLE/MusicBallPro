package cn.heyanle.musicballpro.view.services;

import android.content.res.Configuration;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.model.MusicModel;
import cn.heyanle.musicballpro.model.notification.NotificationModel;
import cn.heyanle.musicballpro.presenters.service.BallPresenter;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Followable;

/**
 * NotificationService
 * @see cn.heyanle.musicballpro.presenters.service.BallPresenter
 * @see MusicModel
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
public class NotificationService extends NotificationListenerService {

    private BallPresenter presenter;
    //MusicModel musicPresenter;
    private Followable<MusicInfo> followable = null;

    private NotificationModel notificationModel;

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate() {
        super.onCreate();

        presenter = new BallPresenter(this);

        /*
        设置BallViwPresenter监听
         */
        presenter.setListener(new BallPresenter.BallListener() {
            @Override
            public void onUpSlide() {
                try{
                    MusicModel.getInstance().getNowMusic().getLast().run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onDownSlide() {
                try{
                    MusicModel.getInstance().getNowMusic().getNext().run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onClick() {
                try{
                    MusicModel.getInstance().getNowMusic().getClick().run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onHorizontalSlide() {
                try{
                    HeLog.i("HorizontalSlide",this);
                    MusicModel.getInstance().getNowMusic().getMusicPage().run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        followable = MusicModel.getInstance().getFollowable();

        notificationModel = new NotificationModel(this,followable);

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
        presenter.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged();
    }
}
