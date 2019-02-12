package cn.heyanle.musicballpro.models.notification.data;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
    public abstract MusicInfo getMusicInfo(StatusBarNotification sbn);

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

    static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    static String getResourceEntryName(Resources resources, Notification.Action action){

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
            return resources.getResourceEntryName(action.icon);
        }else{
            return resources.getResourceEntryName(action.getIcon().getResId());
        }

    }

}
