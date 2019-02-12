package cn.heyanle.musicballpro.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.lang.reflect.Method;

import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Observable;
import cn.heyanle.musicballpro.utils.rx.Observer;

/**
 * 数据 内容提供器 MainModel
 * 链式调用 单例模式
 * 利用SharedPreferences进行数据储存
 * 获取数据 读取数据 数据改变监听
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class MainModel {

    /**
     * 数据改变实体类
     */
    public static class DataChangeInfo{

        public String what;
        public Object obj;

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
    public final static String YP = "yP";

    public final static String KEYBOARD_SIZE = "keyboard_size";              //输入法高度

    public final static String IS_TURN = "is_turn";                          //旋转
    public final static String ALPHA = "ball_alpha";                         //闲置淡化透明度

    public final static String IS_WALLPAPER_OPEN = "is_wallpaper_open";

    /**
     * 数据改变监听
     */
    private Observable<DataChangeInfo> mObservable;


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
    private int alpha;

    private boolean isOpen;
    private boolean isKeepEdge;
    private boolean isAvoidKeyboard;

    private float xP;
    private float yP;

    private int keyboardHeight;

    private boolean isTurn;

    private boolean isWallpaperOpen;

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

    //===========================数据获取 更改================================

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

    public MainModel keyboardSize(int size){
        keyboardHeight = size;
        notifyListeners(KEYBOARD_SIZE,size);
        return this;
    }

    public int getKeyboardSize(){
        return keyboardHeight;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public MainModel setTurn(boolean turn) {
        isTurn = turn;
        notifyListeners(IS_TURN,turn);
        editor.putBoolean(IS_TURN,turn);
        return this;
    }

    public int getAlpha() {
        return alpha;
    }

    public MainModel alpha(int alpha) {
        this.alpha = alpha;
        notifyListeners(ALPHA,alpha);
        editor.putInt(ALPHA,alpha);
        return this;
    }

    public boolean isWallpaperOpen() {
        return isWallpaperOpen;
    }

    public MainModel setWallpaperOpen(boolean wallpaperOpen) {
        isWallpaperOpen = wallpaperOpen;
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
                "\nIsAvoidKeyboard -> " + isAvoidKeyboard +
                "\nXP -> " + xP + "\nYP -> " + yP +
                "\nKeyBoardSize -> " + keyboardHeight +
                "\nisTurn -> " + isTurn +
                "\nAlpha -> " + alpha;
    }

    //==============================单例================================


    /**
     * 单例模式 对象变量
     */
    private static MainModel INSTANCE = null;

    /**
     * 获取实例
     * @return      实例
     */
    public static MainModel getInstance(){
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

        isTurn = preferences.getBoolean(IS_TURN,true);

        keyboardHeight = preferences.getInt(KEYBOARD_SIZE,-1);

        alpha = preferences.getInt(ALPHA,1);

        isWallpaperOpen = preferences.getBoolean(IS_WALLPAPER_OPEN,false);

        HeLog.i("MainModel","init",this);
        HeLog.i(toString(),this);

        mObservable = new Observable<>();

        INSTANCE = this;

    }

    /**
     * 私有化构造方法
     * 由反射调用
     */
    private MainModel(){};

}
