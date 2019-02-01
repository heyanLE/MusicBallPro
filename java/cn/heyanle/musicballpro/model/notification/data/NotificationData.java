package cn.heyanle.musicballpro.model.notification.data;

import android.content.Context;
import android.content.res.Resources;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;

import cn.heyanle.musicballpro.bean.MusicInfo;

/**
 * 通知获取音乐信息组件接口（一个App一个）
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
public abstract class NotificationData {

    //private Resources resources = null;
    protected Context context;

    /**
     * 分析通知获取MusicInfo
     * @param sbn       通知
     * @return          MusicInfo
     */
    public abstract @Nullable MusicInfo getMusicInfo(StatusBarNotification sbn);

    /**
     * 当前组件分析的App包名
     * @return          包名
     */
    public abstract String getPackageName();

    /**
     * 初始化
     * @param c     上下文对象
     */
    public void init(Context c){

         context = c;

     }

}
