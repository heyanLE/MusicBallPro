package cn.heyanle.musicballpro.utils;

import android.util.Log;

import cn.heyanle.musicballpro.C;

/**
 * 包装原始Log的v,d,i,w,e方法
 * Created by HeYanLe
 * 2019/1/25 0025
 * https://github.com/heyanLE
 */
public class HeLog {

    /**
     * Log.v  其它类似
     * @param value         Log内容
     * @param classInit     输出Log的对象（获取类名作为Log的TAG）
     */
    public static void v(String value,Object classInit){
        if (C.IS_DEBUG) Log.v(classInit.getClass().getName(),value);
    }

    /**
     * Log.v 其它类似  最终输出为： value -> msg
     * @param value         Log的Key
     * @param msg           Log的内容
     * @param classInit     输出Log的对象（获取类名作为Log的TAG）
     */
    public static void v(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.v(classInit.getClass().getName(),value + " -> " + msg);
    }

    public static void d(String value,Object classInit){
        if (C.IS_DEBUG) Log.d(classInit.getClass().getName(),value);
    }

    public static void d(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.d(classInit.getClass().getName(),value + " -> " + msg);
    }

    public static void i(String value,Object classInit){
        if (C.IS_DEBUG) Log.i(classInit.getClass().getName(),value);
    }

    public static void i(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.i(classInit.getClass().getName(),value + " -> " + msg);
    }

    public static void w(String value,Object classInit){
        if (C.IS_DEBUG) Log.w(classInit.getClass().getName(),value);
    }

    public static void w(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.w(classInit.getClass().getName(),value + " -> " + msg);
    }

    public static void e(String value,Object classInit){
        if (C.IS_DEBUG) Log.e(classInit.getClass().getName(),value);
    }

    public static void e(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.e(classInit.getClass().getName(),value + " -> " + msg);
    }
}
