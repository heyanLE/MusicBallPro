package cn.heyanle.musicballpro.qmui;

import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

/**
 * Created by HeYanLe
 * 2019/2/4 0004
 * https://github.com/heyanLE
 */
public class QMUIDisplayHelper {



    /**
     * 获取 DisplayMetrics
     *
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight(Context context) {
        int screenHeight = getDisplayMetrics(context).heightPixels;
        if(QMUIDeviceHelper.isXiaomi() && xiaomiNavigationGestureEnabled(context)){
            screenHeight += getResourceNavHeight(context);
        }
        return screenHeight;
    }

    private static int getResourceNavHeight(Context context){
        // 小米4没有nav bar, 而 navigation_bar_height 有值
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return -1;
    }

    // ====================== Setting ===========================
    private static final String XIAOMI_FULLSCREEN_GESTURE = "force_fsg_nav_bar";

    public static boolean xiaomiNavigationGestureEnabled(Context context) {
        int val = Settings.Global.getInt(context.getContentResolver(), XIAOMI_FULLSCREEN_GESTURE, 0);
        return val != 0;
    }
}
