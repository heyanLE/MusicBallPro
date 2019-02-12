package cn.heyanle.musicballpro.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.File;

import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Observable;
import cn.heyanle.musicballpro.utils.rx.Observer;

/**
 * 悬浮壁纸的内容提供者
 * Created by HeYanLe
 * 2019/2/10 0010
 * https://github.com/heyanLE
 */
public class FloatWallpaperModel {

    //private boolean is_open = false;

    private Drawable drawable = null;

    private int alpha = 20;

    private SharedPreferences.Editor editor;


    private static String DRAWABLE_PATH;

    /**
     * 数据改变实体类
     */
    public static class DataChangeInfo{

        public String what;
        public Object obj;

    }

    /**
     * 数据改变监听
     */
    private Observable<DataChangeInfo> mObservable;

    /**
     * 添加数据改变监听器
     * @param listener 数据改变监听器对象
     */
    public void addOnDataChangeListener(@NonNull Observer<DataChangeInfo> listener){
        mObservable.observedBy(listener);
    }

    /**
     * 移除数据改变监听器
     * @param listener 数据改变监听器
     */
    public void removeDataChangeListener(@NonNull Observer<DataChangeInfo> listener){
        mObservable.unObservedBy(listener);
    }

    /**
     * 通知监听更新
     * @param what          改变的数据的Key
     * @param obj           改变的数据
     */
    private void notifyListeners(String what,Object obj){
        DataChangeInfo info = new DataChangeInfo();
        info.what = what;
        info.obj = obj;
        mObservable.update(info);
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        notifyListeners("d",drawable);
        this.drawable = drawable;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
        notifyListeners("a",alpha);
        editor.putInt("alpha",alpha);
        editor.apply();
    }

    //==============================单例================================


    /**
     * 单例模式 对象变量
     */
    private static FloatWallpaperModel INSTANCE = null;

    /**
     * 获取实例
     * @return      实例
     */
    public static FloatWallpaperModel getInstance(){
        return INSTANCE;
    }

    /**
     * 初始化 由HeApp反射调用
     * @param context       上下文对象
     */
    @SuppressLint("CommitPrefEdits")
    private void init(Context context){
        //this.context = context;
        //HeApp.mainModel = this;
        SharedPreferences preferences = context.getSharedPreferences("wallpaper",Context.MODE_PRIVATE);
        editor = preferences.edit();


        File f = context.getExternalFilesDir(null);

        if (f != null) {
            DRAWABLE_PATH = f.getAbsolutePath() + "/FloatWallpaper.png";

            if (new File(DRAWABLE_PATH).isFile()){

                drawable = Drawable.createFromPath(DRAWABLE_PATH);

            }

        }
        alpha = preferences.getInt("alpha",255);

        INSTANCE = this;

    }

    /**
     * 私有化构造方法
     * 由反射调用
     */
    private FloatWallpaperModel(){}

}
