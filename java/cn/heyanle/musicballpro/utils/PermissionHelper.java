package cn.heyanle.musicballpro.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIDeviceHelper;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import cn.heyanle.musicballpro.R;

/**
 * 权限帮助者 静态类
 * 悬浮球权限  通知使用权  跳转到应用详情界面
 * Created by HeYanLe
 * 2019/1/26 0026
 * https://github.com/heyanLE
 */
public class PermissionHelper {

    /**
     * 是否拥有悬浮窗权限
     * @param context           上下文对象
     * @return                  是否拥有
     */
    public static boolean drawOverlayEnable(Context context){

        final int version = Build.VERSION.SDK_INT;//获取SDK版本
        boolean enable;
        if (version >= 23){ //如果为安卓M以上 直接调用系统api
            enable = Settings.canDrawOverlays(context);
        }else{ //如果为安卓5 调用QMUI权限检测
            enable = QMUIDeviceHelper.isFloatWindowOpAllowed(context);
        }
        return enable;

    }

    /**
     * 检测是否有通知使用权
     * @param context               上下文对象
     * @return                      有无通知使用权
     */
    public static boolean notificationListenerEnable(Context context) {
        boolean enable = false;
        String packageName = context.getPackageName();
        String flat= Settings.Secure.getString(context.getContentResolver(),"enabled_notification_listeners");
        if (flat != null) {
            enable= flat.contains(packageName);
        }
        return enable;
    }

    /**
     * 跳转到授予悬浮球权限
     * @param context           上下文对象
     */
    public static void gotoDrawOverlaySetting(Context context){

        final int version = Build.VERSION.SDK_INT;//获取SDK版本
        if (version >= 23){ //如果为安卓M以上 直接调用系统api
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            context.startActivity(intent);
        }else{ //如果为安卓5 直接启动App应用详情页
            gotoAppDetailSetting(context);
        }
        //Toast.makeText(context, R.string.please_allow_draw_overlay,Toast.LENGTH_SHORT).show();


    }

    /**
     * 跳转到授予通知使用权权限
     * @param context               上下文对象
     */
    public static void gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch(ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings","com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        //Toast.makeText(context, R.string.please_allow_notification_listener,Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转到应用详情界面
     * @param context           上下文对象
     */
    public static void gotoAppDetailSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

}
