package cn.heyanle.musicballpro.presenters.notification;

import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.heyanle.musicballpro.qmui.QMUIDisplayHelper;

import cn.heyanle.musicballpro.models.MainModel;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Observer;
import cn.heyanle.musicballpro.view.services.NotificationService;
import cn.heyanle.musicballpro.view.view.MusicBall;

/**
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class BallViewPresenter {

    /**
     * 上下文对象
     */
    private Context context;

    /**
     * 窗口管理相关对象
     */
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private MusicBall musicBall;

    private boolean isShow = false;

    /**
     * 交互相关变量
     */
    private float pX;//当前X坐标占坐标轴长度的百分比
    private float pY;

    //private boolean isMove = false;//是否移动模式

    private boolean isInit = false;//是否已经初始化

    /**
     * 数据更新监听
     */
    private Observer<MainModel.DataChangeInfo> onDataChangeListener = new Observer<MainModel.DataChangeInfo>() {
        @Override
        public void onReceive(MainModel.DataChangeInfo msg) {
            String what = msg.what;
            Object obj = msg.obj;

            if (!isInit){//如果还没初始化 直接Return
                return;
            }
            switch (what){
                case MainModel.BALL_BACKGROUND_SIZE://背景半透明大小
                    Integer i = (Integer) obj;
                    if (musicBall != null){
                        musicBall.setBackgroundSize(i);
                        //layoutParams.width = MainModel.getInstance().getBallSize()+MainModel.getInstance().getBallBackgroundSize();
                        //layoutParams.height = MainModel.getInstance().getBallSize()+MainModel.getInstance().getBallBackgroundSize();
                        try {
                            if (MainModel.getInstance().isOpen()) {
                                windowManager.updateViewLayout(musicBall, layoutParams);//更新View
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case MainModel.BALL_SIZE://小球大小
                    Integer ballSize = (Integer) obj;
                    if (musicBall != null){
                        musicBall.setBallSize(ballSize);

                        //layoutParams.width = MainModel.getInstance().getBallSize()+MainModel.getInstance().getBallBackgroundSize();
                        //layoutParams.height = MainModel.getInstance().getBallSize()+MainModel.getInstance().getBallBackgroundSize();
                        try {
                            if (MainModel.getInstance().isOpen()) {
                                windowManager.updateViewLayout(musicBall, layoutParams);//更新View
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case MainModel.BALL_BORDER_WIDTH://小球宽度
                    Integer border = (Integer) obj;
                    if (musicBall != null){
                        musicBall.setBallBorder(border);
                        try {
                            if (MainModel.getInstance().isOpen()) {
                                windowManager.updateViewLayout(musicBall, layoutParams);//更新View
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
            }

        }
    };

    /**
     * 初始化整个BallPresenter（每次打开总开关都会调用）
     */
    private void init(){
        /*
        Window管理器对象
         */
        windowManager = (WindowManager) context.getSystemService(Application.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        /*
        动画（渐变）
         */
        layoutParams.windowAnimations = android.R.style.Animation_Translucent;


        pX = MainModel.getInstance().getxP();
        pY = MainModel.getInstance().getyP();

        /*
        Params各种初始设置
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            HeLog.i("OverLay",this);
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            HeLog.i("type_phone",this);
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.x = (int)((getXLength() - MainModel.getInstance().getBallSize() - 2*MainModel.getInstance().getBallBackgroundSize())*pX);
        layoutParams.y = (int)((getYLength() - MainModel.getInstance().getBallSize() - 2*MainModel.getInstance().getBallBackgroundSize())*pY);

        musicBall = new MusicBall(context);
        musicBall.setBallSize(MainModel.getInstance().getBallSize());
        musicBall.setBallBorder(MainModel.getInstance().getBallBorderWidth());
        musicBall.setBackgroundSize(MainModel.getInstance().getBallBackgroundSize());

        isInit = true;

    }

    /**
     * 屏幕翻转时调用
     */
    void onConfigurationChanged() {
        layoutParams.x = (int)((getXLength()-MainModel.getInstance().getBallSize() - 2*MainModel.getInstance().getBallBackgroundSize())*pX+0.5f);
        layoutParams.y = (int)((getYLength()-MainModel.getInstance().getBallSize() - 2*MainModel.getInstance().getBallBackgroundSize())*pY+0.5f);

        try {
            if (MainModel.getInstance().isOpen()) {
                windowManager.updateViewLayout(musicBall, layoutParams);//更新View
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void setpX(float pX) {
        this.pX = pX;
        MainModel.getInstance().xP(pX).apply();
    }

    void setpY(float pY) {
        this.pY = pY;
        MainModel.getInstance().yP(pY).apply();
    }

    float getpX() {
        return pX;
    }

    float getpY() {
        return pY;
    }

    int getX(){
        return layoutParams.x;
    }

    int getY(){
        return layoutParams.y;
    }

    /**
     * 移动小球
     * @param x         X坐标
     * @param y         Y坐标
     */
    void move(int x,int y){

        layoutParams.x = x;//设置坐标
        layoutParams.y = y;

        HeLog.i("Move 移动小球",pX + "  |  " + pY,this);

        try {
            if (MainModel.getInstance().isOpen()) {
                windowManager.updateViewLayout(musicBall, layoutParams);//更新View
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void saveP(int x, int y){
        pX = (float)x/((float)(getXLength()-MainModel.getInstance().getBallSize() - 2*MainModel.getInstance().getBallBackgroundSize()));//设置百分比
        pY = (float)y/((float)(getYLength()-MainModel.getInstance().getBallSize() - 2*MainModel.getInstance().getBallBackgroundSize()));

        MainModel.getInstance().xP(pX).yP(pY).apply();//保存百分比
    }

    /**
     * 显示小球(总开关开启才会)
     */
    public void show(){
        HeLog.i("isOpen",MainModel.getInstance().isOpen()+"",this);
        HeLog.i("isShow",NotificationService.isShow+"",this);
        try {
            if (MainModel.getInstance().isOpen() && NotificationService.isShow) {
                windowManager.addView(musicBall, layoutParams);
                isShow = true;
                HeLog.i("ball","show",this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 隐藏小球
     */
    public void dismiss(){
        try {
            windowManager.removeView(musicBall);
            HeLog.i("ball","dismiss",this);

            isShow = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onDestroy(){
        dismiss();
        MainModel.getInstance().removeDataChangeListener(onDataChangeListener);
    }

    /**
     * 获取MusicBall对象
     * @return MusicBall对象
     */
    MusicBall getMusicBall(){
        return musicBall;
    }

    /**
     * 构造方法
     * @param context           上下文对象
     */
    public BallViewPresenter(Context context){
        this.context = context;
        /*
        注册数据更新监听器
         */
        MainModel.getInstance().addOnDataChangeListener(onDataChangeListener);
        musicBall = new MusicBall(context);
        init();
    }


    boolean isShow(){

        return isShow;

    }

    void alpha(){
        layoutParams.alpha = MainModel.getInstance().getAlpha() + 1;
        try {
            if (MainModel.getInstance().isOpen()) {
                windowManager.updateViewLayout(musicBall, layoutParams);//更新View
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void alphaBack(){
        layoutParams.alpha = 1;
        try {
            if (MainModel.getInstance().isOpen()) {
                windowManager.updateViewLayout(musicBall, layoutParams);//更新View
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //================================各种Get====================================

    /**
     * 获取X轴长度  利用QMUI获取 下同
     * @return          X轴长度
     */
    int getXLength(){
        return QMUIDisplayHelper.getScreenWidth(context);
    }

    int getYLength(){
        return QMUIDisplayHelper.getScreenHeight(context);
    }

    /**
     * 获取状态栏高度
     * @return          状态栏高度
     */
    int getStatusSize(){
        int[] size = new int[2];
        musicBall.getLocationOnScreen(size);
        return size[1]-layoutParams.y;
    }

}
