package cn.heyanle.musicballpro;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.umeng.commonsdk.UMConfigure;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import cn.heyanle.musicballpro.models.MainModel;
import cn.heyanle.musicballpro.models.MusicModel;
import cn.heyanle.musicballpro.models.WhiteModel;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.view.activities.NulActivity;

/**
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class HeApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();


        try{

            /*
            初始化MainModel 单例模式
             */

            Class<MainModel> mC = MainModel.class;
            Constructor<MainModel> cMC = mC.getDeclaredConstructor();
            cMC.setAccessible(true);
            Method method = mC.getDeclaredMethod("init", Context.class);
            method.setAccessible(true);
            MainModel m = cMC.newInstance();
            method.invoke(m,this);


        }catch (Exception e){
            e.printStackTrace();
        }

        try{

            /*
            初始化MusicModel 单例模式
             */

            Class<MusicModel> mC = MusicModel.class;
            Constructor<MusicModel> cMC = mC.getDeclaredConstructor();
            cMC.setAccessible(true);
            Method method = mC.getDeclaredMethod("init");
            method.setAccessible(true);
            MusicModel m = cMC.newInstance();
            method.invoke(m);


        }catch (Exception e){
            e.printStackTrace();
        }

        try{

            /*
            初始化WhiteModel 单例模式
             */

            Class<WhiteModel> mC = WhiteModel.class;
            Constructor<WhiteModel> cMC = mC.getDeclaredConstructor();
            cMC.setAccessible(true);
            Method method = mC.getDeclaredMethod("init", Context.class);
            method.setAccessible(true);
            WhiteModel m = cMC.newInstance();
            method.invoke(m,this);


        }catch (Exception e){
            e.printStackTrace();
        }


        try{

            /*
            初始化MainProcurator 单例模式
             */

            Class<MainProcurator> mC = MainProcurator.class;
            Constructor<MainProcurator> cMC = mC.getDeclaredConstructor();
            cMC.setAccessible(true);
            //Method method = mC.getDeclaredMethod("init");
            cMC.newInstance();


        }catch (Exception e){
            e.printStackTrace();
        }

		//这部分appid码不公开
        UMConfigure.init(this, "","", UMConfigure.DEVICE_TYPE_PHONE, null);

        /*
        CrashHandler初始化
         */
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));


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
