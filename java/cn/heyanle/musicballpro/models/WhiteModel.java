package cn.heyanle.musicballpro.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Observable;
import cn.heyanle.musicballpro.utils.rx.Observer;

/**
 * 白名单 数据提供者 单例模式
 * Created by HeYanLe
 * 2019/2/5 0005
 * https://github.com/heyanLE
 */
public class WhiteModel {

    private List<String> mMusicWhite = new ArrayList<>();
    private List<String> mShowWhite = new ArrayList<>();


    /**
     * SharedPreferences.editor对象
     */
    private SharedPreferences.Editor editor;

    public void cloneMusicWhite(List<String> list){
        list.addAll(mMusicWhite);
    }

    public void cloneShowWhite(List<String> list){
        list.addAll(mShowWhite);
    }

    private Observable<String> observable = new Observable<>();

    public void addObserver(Observer<String> o){
        observable.observedBy(o);
    }

    public void removeObserver(Observer<String> o){
        observable.unObservedBy(o);
    }


    /**
     * 该app是否在音乐白名单内
     * @param packageName   应用包名
     * @return              是否
     */
    public boolean isMusicWhite(String packageName){
        return mMusicWhite.contains(packageName);
    }

    public boolean isShowWhite(String packageName){
        return mShowWhite.contains(packageName);
    }


    public void addMusicWhite(String packageName){
        mMusicWhite.add(packageName);
        observable.update("");
        //apply();
    }

    public void addShowWhite(String packageName){
        mShowWhite.add(packageName);
        observable.update("");
        //apply();
    }

    public void removeMusicWhite(String packageName){
        mMusicWhite.remove(packageName);
        observable.update("");
        //apply();
    }

    public void removeShowWhite(String packageName){
        mShowWhite.remove(packageName);
        observable.update("");
        //apply();
    }


    public void apply(){

        StringBuilder builder = new StringBuilder();
        builder.append("{\"MusicWhite\":[");

        /*
        MusicWhite数组
         */
        for(String b:mMusicWhite){
            builder.append("\"").append(b).append("\",");
        }

        if (builder.substring(builder.length()-1).equals(",")){
            builder.deleteCharAt(builder.length()-1);
        }

        builder.append("],\"ShowWhite\":[");

        /*
        ShowWhite数组
         */

        for(String b:mShowWhite){
            builder.append("\"").append(b).append("\",");
        }

        if (builder.substring(builder.length()-1).equals(",")){
            builder.deleteCharAt(builder.length()-1);
        }

        /*
        收尾
         */
        builder.append("]}");

        editor.putString("WhiteJson",builder.toString());
        editor.apply();

        HeLog.i("WhiteModel apply",builder.toString(),this);


    }

    //==========================单例模式=====================================


    /**
     * 单例模式 对象变量
     */
    private static WhiteModel INSTANCE = null;

    /**
     * 获取实例
     * @return      实例
     */
    public static WhiteModel getInstance(){
        return INSTANCE;
    }

    /**
     * 初始化 由HeApp反射调用
     * @param context       上下文对象
     */
    @SuppressLint("CommitPrefEdits")
    private void init(Context context){

        INSTANCE = this;

        SharedPreferences preferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        editor = preferences.edit();

        String data = preferences.getString("WhiteJson","{\"MusicWhite\":[],\"ShowWhite\":[]}");

        HeLog.i("WhiteModel init",data,this);
        try{
            JSONObject object = new JSONObject(data);
            JSONArray musicArray = object.getJSONArray("MusicWhite");
            JSONArray showArray = object.getJSONArray("ShowWhite");

            for (int i = 0 ; i < musicArray.length() ; i ++){
                mMusicWhite.add(musicArray.getString(i));
            }

            for (int i = 0 ; i < showArray.length() ; i ++){
                mShowWhite.add(showArray.getString(i));
            }

        }catch (Exception e){

            e.printStackTrace();

        }
    }

    /**
     * 私有化构造方法
     * 由反射调用
     */
    private WhiteModel(){};

}
