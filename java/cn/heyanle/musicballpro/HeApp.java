package cn.heyanle.musicballpro;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.model.MainModel;
import cn.heyanle.musicballpro.model.MusicModel;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Followable;
import cn.heyanle.musicballpro.view.activities.NulActivity;
import dalvik.system.DexFile;

/**
 * 自定义Application
 * 自定义异常监听 MainModel的初始化
 * Created by HeYanLe
 * 2019/1/25 0025
 * https://github.com/heyanLE
 */
public class HeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        MainModel初始化 （反射）
         */
        try {
            Class<MainModel> c = MainModel.class;
            Constructor<MainModel> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            MainModel ma = constructor.newInstance();
            ma.init(this);
        }catch (InvocationTargetException |InstantiationException | IllegalAccessException | NoSuchMethodException e ){
            e.printStackTrace();
        }

        /*
        反射初始化MusicModel
         */
        try {
            Class<MusicModel> c = MusicModel.class;
            Constructor<MusicModel> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            MusicModel musicModel = constructor.newInstance();
            Method method = c.getDeclaredMethod("onStart");
            method.setAccessible(true);
            method.invoke(musicModel);

        }catch (Exception e ){
            e.printStackTrace();
        }

        /*
        CrashHandler初始化
         */
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));

        /*
        初始化Bugly
         */
        CrashReport.initCrashReport(getApplicationContext(), "9d759c1335", C.IS_DEBUG);

    }
}

class CrashHandler implements Thread.UncaughtExceptionHandler{

    private Context mContext ;

    CrashHandler(Context context){
        mContext = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        /*
        打印报错信息
         */
        StringWriter stringWriter = new StringWriter();
        try{
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            Throwable th = e.getCause();
            while (th != null){
                th.printStackTrace(printWriter);
                th = th.getCause();
            }
        }catch (Exception e1){
            e1.printStackTrace();
        }

        /*
        Log输出（只有DeBug模式才会输出）
         */
        HeLog.e("ThreadCrash",stringWriter.toString(),this);

        /*
        启动崩溃界面Activity
         */
        Intent intent = new Intent();
        intent.setClass(mContext,NulActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NulActivity.INTENT_KEY,stringWriter.toString());
        mContext.startActivity(intent);

    }
}
