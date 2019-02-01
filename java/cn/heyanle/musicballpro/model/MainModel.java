package cn.heyanle.musicballpro.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.view.view.SwitchRelative;

/**
 * 主要数据内容提供器 链式调用 单例模式
 * 利用SharedPreferences进行数据储存
 * 获取数据 读取数据 数据改变监听
 * Created by HeYanLe
 * 2019/1/25 0025
 * https://github.com/heyanLE
 */
public class MainModel {

    /**
     * 数据改变监听器接口
     */
    public interface OnDataChangeListener{

        /**
         * 数据改变时调用
         * @param what          改变的数据Key
         * @param obj           改变后的数据
         */
        void onDataChange(String what,Object obj);

    }

    /**
     * 各种数据的Key值
     */
    public final static String BALL_SIZE = "ball_size";                      //小球大小
    public final static String BALL_BORDER_WIDTH = "ball_border_width";      //小球边框宽度
    public final static String BALL_BACKGROUND_SIZE = "ball_background_size";//小球背景半透明大小

    public final static String KEEP_EDGE = "keep_edge";                      //自动贴边
    public final static String AVOID_KEYBOARD = "avoid_keyboard";            //避让输入法

    public final static String IS_OPEN = "is_open";                          //开关

    public final static String XP = "xP";                                    //小球X坐标占X轴长度百分比 下同
    public final static String YP = "xP";

    /**
     * 数据改变监听器列表
     */
    private List<OnDataChangeListener> listeners = new ArrayList<>();


    /**
     * SharedPreferences.editor对象
     */
    private SharedPreferences.Editor editor;

    /**
     * 数据储存变量
     * 与上面Key一一对应
     */
    private int ballSize;
    private int ballBackgroundSize;
    private int ballBorderWidth;

    private boolean isOpen;
    private boolean isKeepEdge;
    private boolean isAvoidKeyboard;

    private float xP;
    private float yP;

    /**
     * 提交改变储存申请
     */
    public void apply(){
        editor.apply();
        HeLog.i("Apply",this.toString(),this);
    }

    /**
     * 添加数据改变监听器
     * @param listener 数据改变监听器对象
     */
    public void addOnDataChangeListener(@NonNull OnDataChangeListener listener){
        listeners.add(listener);
    }

    /**
     * 移除数据改变监听器
     * @param listener 数据改变监听器
     */
    public void removeDataChangeListener(@NonNull OnDataChangeListener listener){
        listeners.remove(listener);
    }

    /**
     * 通知监听器更新
     * @param what          改变的数据的Key
     * @param obj           改变的数据
     */
    private void notifyListeners(String what,Object obj){
        for (OnDataChangeListener listener : listeners){
            listener.onDataChange(what,obj);
        }
    }

    /**
     * 获取小球大小 （其它类似）
     * @return          小球大小
     */
    public int getBallSize() {
        return ballSize;
    }

    /**
     * 设置小球大小 （其它类似）
     * @param ballSize          小球大小
     * @return                  本身对象（链式操作）
     */
    public MainModel ballSize(int ballSize) {
        this.ballSize = ballSize;
        notifyListeners(BALL_SIZE,ballSize);
        editor.putInt(BALL_SIZE,ballSize);
        return this;
    }

    public int getBallBackgroundSize() {
        return ballBackgroundSize;
    }

    public MainModel ballBackgroundSize(int ballBackgroundSize) {
        this.ballBackgroundSize = ballBackgroundSize;
        notifyListeners(BALL_BACKGROUND_SIZE,ballBackgroundSize);
        editor.putInt(BALL_BACKGROUND_SIZE,ballBackgroundSize);
        return this;
    }

    public int getBallBorderWidth() {
        return ballBorderWidth;
    }

    public MainModel ballBorderWidth(int ballBorderWidth) {
        this.ballBorderWidth = ballBorderWidth;
        notifyListeners(BALL_BORDER_WIDTH,ballBorderWidth);
        editor.putInt(BALL_BORDER_WIDTH,ballBorderWidth);
        return this;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public MainModel isOpen(boolean open) {
        isOpen = open;
        notifyListeners(IS_OPEN,open);
        editor.putBoolean(IS_OPEN,isOpen);
        return this;
    }

    public boolean isKeepEdge() {
        return isKeepEdge;
    }

    public MainModel keepEdge(boolean keepEdge) {
        isKeepEdge = keepEdge;
        notifyListeners(KEEP_EDGE,keepEdge);
        editor.putBoolean(KEEP_EDGE,isKeepEdge);
        return this;
    }

    public boolean isAvoidKeyboard() {
        return isAvoidKeyboard;
    }

    public MainModel avoidKeyboard(boolean avoidKeyboard) {
        isAvoidKeyboard = avoidKeyboard;
        notifyListeners(AVOID_KEYBOARD,avoidKeyboard);
        editor.putBoolean(AVOID_KEYBOARD,isAvoidKeyboard);
        return this;
    }

    public float getxP() {
        return xP;
    }

    public MainModel xP(float xP) {
        this.xP = xP;
        notifyListeners(XP,xP);
        editor.putFloat(XP,xP);
        return this;
    }

    public float getyP() {
        return yP;
    }

    public MainModel yP(float yP) {
        this.yP = yP;
        notifyListeners(YP,yP);
        editor.putFloat(YP,yP);
        return this;
    }

    @NonNull
    @Override
    public String toString(){
        return "MainModel\nBallSize -> " + ballSize +
                "\nBallBackgroundSize -> " + ballBackgroundSize +
                "\nBallBorderWidth -> " + ballBorderWidth +
                "\nIsOpen -> " + isOpen +
                "\nIsKeepEdge -> " + isKeepEdge +
                "\nIsAvoidKeyboard ->" + isAvoidKeyboard +
                "\nXP -> " + xP + "\nYP -> " + yP;
    }

    //以下为单例模式相关写法 =======================================================

    /**
     * 初始化（通过反射调用）
     * @param context           上下文对象（一般为Application对象）
     */
    @SuppressLint("CommitPrefEdits")
    public void init(Context context){

        /*
        如果为INSTANCE为Null 开始初始化
         */
        if (INSTANCE == null) {
            HeLog.i("MainModel","init",this);
            //this.context = context;
            INSTANCE = this;
            SharedPreferences preferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
            editor = preferences.edit();

            ballSize = preferences.getInt(BALL_SIZE,200);
            ballBackgroundSize = preferences.getInt(BALL_BACKGROUND_SIZE,20);
            ballBorderWidth = preferences.getInt(BALL_BORDER_WIDTH,20);
            isAvoidKeyboard = preferences.getBoolean(AVOID_KEYBOARD,false);
            isKeepEdge = preferences.getBoolean(KEEP_EDGE,false);
            isOpen = preferences.getBoolean(IS_OPEN,false);

            xP = preferences.getFloat(XP,0f);
            yP = preferences.getFloat(YP,0.5f);

        }

    }

    /**
     * 获取单例对象
     * @return          单例对象
     */
    public static MainModel getInstance(){
        return INSTANCE;
    }

    /**
     * 单例对象保存
     */
    @SuppressLint("StaticFieldLeak")
    private static MainModel INSTANCE = null;

    /**
     * 私有化构造方法，防止外部实例化
     */
    private MainModel(){}


}
