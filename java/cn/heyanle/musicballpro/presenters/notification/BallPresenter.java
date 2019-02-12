package cn.heyanle.musicballpro.presenters.notification;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Method;

import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.models.MainModel;
import cn.heyanle.musicballpro.models.MusicModel;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Observer;
import cn.heyanle.musicballpro.view.view.MusicBall;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 *
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class BallPresenter {

    private Vibrator vibrator;

    /**
     * BallViewPresenter对象
     */
    private BallViewPresenter presenter;

    private boolean isKeepEdge = false;

    /**
     * 交互相关变量
     */
    private int oldX;
    private int oldY;
    private int x;
    private int y;
    private float touchX;
    private float touchY;
    private boolean isMove = false;
    private boolean isOut = false;

    private MusicBall musicBall;

    private Handler handlerr = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            presenter.alpha();
        }
    };

    /**
     * 小球交互监听器
     */
    public interface BallListener{

        void onUpSlide();//上滑
        void onDownSlide();//下滑
        //void onLongTouch();//长按
        void onClick();//短按
        void onHorizontalSlide();//水平滑

    }

    private BallListener listener = null;

    public void setListener(BallListener listener) {
        this.listener = listener;
        HeLog.i("SetListener",this.listener.toString(),this);
    }

    /**
     * 数据更新监听
     */
    private Observer<MainModel.DataChangeInfo> onDataChangeListener = new Observer<MainModel.DataChangeInfo>() {
        @Override
        public void onReceive(MainModel.DataChangeInfo msg) {
            String what = msg.what;
            Object obj = msg.obj;

            switch (what){
                case MainModel.IS_OPEN://总开关
                    Boolean b = (Boolean) obj;
                    if (b){//开启
                        if (!MusicModel.getInstance().getNowMusic().isEmpty()){

                            //HeLog.i("ballMusic", musicInfo.toString(), this);
                            presenter.show();//显示小球
                            musicBall.setImgDrawable(MusicModel.getInstance().getNowMusic().getAlbumCover());//设置封面

                            if (MusicModel.getInstance().getNowMusic().isPlaying()) {//播放
                                musicBall.startTurn();//开始旋转
                            } else {
                                musicBall.pauseTurn();//暂停旋转
                            }

                        }
                    }else{//关闭
                        presenter.dismiss();//隐藏小球
                    }
                    break;
                case MainModel.KEEP_EDGE://自动贴边
                    Boolean b1 = (Boolean) obj;
                    isKeepEdge = b1;
                    if (b1){
                        moveToEdge();
                    }
                    break;
            }
        }
    };

    /*
        音乐更新监听
         */
    private Observer<MusicInfo> onMusicChangeListener = new Observer<MusicInfo>() {
        @Override
        public void onReceive(MusicInfo musicInfo) {
            refresh(musicInfo);
        }
    };

    private Handler handler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            //长按
            isMove = true;
            isListener = false;
            musicBall.isNotPress();
            vibrator.vibrate(30);
        }
    };

    private boolean isListener = false;

    public void aviodKeyboard(){

        final ValueAnimator valueAnimator;

        if (MainModel.getInstance().getKeyboardSize() <= 0 &&
                presenter.getYLength() - presenter.getStatusSize() -
                        (oldY + MainModel.getInstance().getBallSize()
                                + 2*MainModel.getInstance().getBallBackgroundSize())
                        < getKeyboardSize()
                ){
            valueAnimator = ValueAnimator.ofInt(oldY,presenter.getYLength() - presenter.getStatusSize() -
                    (getKeyboardSize() + MainModel.getInstance().getBallSize()
                            + 2*MainModel.getInstance().getBallBackgroundSize()));
            //presenter.setpX(1f);
        }else if (presenter.getYLength() - presenter.getStatusSize() -
                (oldY + MainModel.getInstance().getBallSize()
                        + 2*MainModel.getInstance().getBallBackgroundSize())
                < MainModel.getInstance().getKeyboardSize()){
            valueAnimator = ValueAnimator.ofInt(oldY,presenter.getYLength() - presenter.getStatusSize() -
                    (MainModel.getInstance().getKeyboardSize() + MainModel.getInstance().getBallSize()
                            + 2*MainModel.getInstance().getBallBackgroundSize()));
            //presenter.setpX(0f);
        }else{
            return;
        }

        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer i = (Integer) animation.getAnimatedValue();
                presenter.move(oldX,i);
            }
        });
        valueAnimator.start();

    }


    /**
     * 初始化
     */
    @SuppressLint("ClickableViewAccessibility")
    private void init(){

        isKeepEdge = MainModel.getInstance().isKeepEdge();

        musicBall = presenter.getMusicBall();
        musicBall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        handlerr.removeCallbacks(runnable);
                        presenter.alphaBack();
                        isOut = false;
                        oldX = (int)(event.getRawX() - event.getX()+0.5f);
                        oldY = (int)(event.getRawY() - event.getY() - presenter.getStatusSize()+0.5f);
                        touchX = event.getX();
                        touchY = event.getY();
                        musicBall.isPress();
                        musicBall.stopTurn();
                        handler.postDelayed(r,500);
                        isListener = true;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (!isMove && !isListener) break;
                        /*
                        最大XY范围
                         */
                        int maxX = presenter.getXLength()-MainModel.getInstance().getBallSize()-2*MainModel.getInstance().getBallBackgroundSize();
                        int maxY = presenter.getYLength()-2*(MainModel.getInstance().getBallSize()+2*MainModel.getInstance().getBallBackgroundSize()) - 50;
                        /*
                        获取移动坐标
                         */
                        x = (int)(event.getRawX() - touchX + 0.5f);
                        y = (int)(event.getRawY() - touchY - presenter.getStatusSize() + 0.5f);
                        /*
                        不能超出屏幕
                         */
                        x = x<0?0:x;
                        x = x>maxX?maxX:x;
                        if (isMove) {
                            y = y > maxY ? maxY : y;
                            y = y<MainModel.getInstance().getBallSize()+2*MainModel.getInstance().getBallBackgroundSize()+50?MainModel.getInstance().getBallSize()+2*MainModel.getInstance().getBallBackgroundSize()+50:y;
                        }else{
                            y = y > (presenter.getYLength()-MainModel.getInstance().getBallSize()-2*MainModel.getInstance().getBallBackgroundSize())?presenter.getYLength()-MainModel.getInstance().getBallSize()-2*MainModel.getInstance().getBallBackgroundSize():y;
                            y = y < 0 ? 0:y;
                        }
                        /*
                        如果移动了 则取消长按callBack
                         */
                        if (Math.abs(x-oldX) >= 16 || Math.abs(y-oldY) >= 16){
                            handler.removeCallbacks(r);
                            isOut = true;
                        }

                        /*
                        如果监听状态 + 移动状态 可移动
                         */
                        if (isListener || isMove){

                            presenter.move(x,y);
                            //moveBack();

                        }

                        /*
                        如果不是移动模式 向下滑了
                         */
                        if (isListener && !isMove && y - oldY >= MainModel.getInstance().getBallSize()){
                            isListener = false;//不可监听状态
                            musicBall.isNotPress();
                            vibrator.vibrate(10);
                            moveBack();
                            if (listener != null){
                                listener.onDownSlide();
                            }
                        }

                        /*
                        如果不是移动模式 向上滑了
                         */
                        if (isListener && !isMove && y - oldY <= - MainModel.getInstance().getBallSize()){
                            isListener = false;
                            musicBall.isNotPress();
                            moveBack();
                            vibrator.vibrate(10);
                            if (listener != null){
                                listener.onUpSlide();
                            }
                        }

                        /*
                        如果不是移动模式 向左右滑了
                         */
                        if (isListener && !isMove && Math.abs(x - oldX) >= MainModel.getInstance().getBallSize()){
                            isListener = false;
                            musicBall.isNotPress();
                            vibrator.vibrate(10);
                            moveBack();
                            if (listener != null){
                                listener.onHorizontalSlide();
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        musicBall.isNotPress();
                        handlerr.postDelayed(runnable,3000);
                        if (MusicModel.getInstance().getNowMusic().isPlaying()){
                            musicBall.startTurn();
                        }

                        if (isMove){
                            oldX = x;
                            oldY = y;
                            presenter.saveP(x,y);
                        }

                        if (!isMove && !isListener) break;

                        handler.removeCallbacks(r);
                        if (!isMove && ! isOut){
                            HeLog.i("点击iiii",this);
                            //点击
                            if (listener != null){
                                listener.onClick();
                                HeLog.i("点击iiii",this);
                            }

                        }
                        if (!isMove){
                            moveBack();
                        }else if(isKeepEdge){
                            moveToEdge();
                            isMove = false;
                        }else{
                            isMove = false;
                        }
                        break;

                }
                return false;
            }
        });


        MusicModel.getInstance().addOnMusicChangeListener(onMusicChangeListener);


    }

    public void moveBack(){

        musicBall.isNotPress();
        //valueAnimator.start();

        presenter.move(oldX,oldY);
        presenter.saveP(oldX,oldY);

    }

    /**
     * 贴边
     */
    private void moveToEdge(){

        final ValueAnimator valueAnimator ;

        if (x >= (presenter.getXLength()-MainModel.getInstance().getBallSize()-MainModel.getInstance().getBallBackgroundSize())/2){
            valueAnimator = ValueAnimator.ofInt(x,presenter.getXLength()-MainModel.getInstance().getBallSize()-2*MainModel.getInstance().getBallBackgroundSize());
            presenter.setpX(1f);
            oldX = presenter.getXLength() - MainModel.getInstance().getBallSize() - 2*MainModel.getInstance().getBallBackgroundSize();
        }else{
            valueAnimator = ValueAnimator.ofInt(x,0);
            presenter.setpX(0f);
            oldX = 0;
        }

        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer i = (Integer) animation.getAnimatedValue();
                presenter.move(i,y);
                x = i;
            }
        });
        valueAnimator.start();

    }

    public BallPresenter (Context context,BallViewPresenter ballViewPresenter){

        presenter = ballViewPresenter;
        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        MainModel.getInstance().addOnDataChangeListener(onDataChangeListener);
        init();
        imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        oldX = presenter.getX();
        oldY = presenter.getY();
    }

    /**
     * 屏幕翻转时调用
     */
    public void onConfigurationChanged() {
        presenter.onConfigurationChanged();
        x = (int)((presenter.getXLength()-MainModel.getInstance().getBallSize()- 2*MainModel.getInstance().getBallBackgroundSize())*presenter.getpX()+0.5f);
        y = (int)((presenter.getYLength()-MainModel.getInstance().getBallSize()- 2*MainModel.getInstance().getBallBackgroundSize())*presenter.getpY()+0.5f);
        if (isKeepEdge){
            moveToEdge();
        }
    }


    /**
     * 当Service销毁时候调用
     */
    public void onDestroy(){
        //presenter.onDestroy();
        MainModel.getInstance().removeDataChangeListener(onDataChangeListener);
        MusicModel.getInstance().removeOnMusicChangeListener(onMusicChangeListener);
    }

    public boolean isShow(){

        return presenter.isShow();

    }

    private void refresh(MusicInfo musicInfo){

        //MusicInfo musicInfo = MusicModel.getInstance().getNowMusic();

        if (musicInfo.isEmpty()) {
            musicBall.stopTurn();
            presenter.dismiss();
        } else {
            HeLog.i("ballMusic", musicInfo.toString(), this);
            presenter.show();//显示小球
            handlerr.removeCallbacks(runnable);
            handlerr.postDelayed(runnable,3000);
            musicBall.setImgDrawable(musicInfo.getAlbumCover());//设置封面

            if (musicInfo.isPlaying()) {//播放
                musicBall.startTurn();//开始旋转
            } else {
                musicBall.pauseTurn();//暂停旋转
            }

        }

    }

    public void refresh(){

        MusicInfo musicInfo = MusicModel.getInstance().getNowMusic();

        if (musicInfo.isEmpty()) {
            musicBall.stopTurn();
            presenter.dismiss();
        } else {
            HeLog.i("ballMusic", musicInfo.toString(), this);
            presenter.show();//显示小球
            handlerr.removeCallbacks(runnable);
            handlerr.postDelayed(runnable,3000);
            musicBall.setImgDrawable(musicInfo.getAlbumCover());//设置封面

            if (musicInfo.isPlaying()) {//播放
                musicBall.startTurn();//开始旋转
            } else {
                musicBall.pauseTurn();//暂停旋转
            }

        }

    }
    InputMethodManager imm;

    private int getKeyboardSize(){
        try {
            Class clazz = imm.getClass();
            Method method = clazz.getDeclaredMethod("getInputMethodWindowVisibleHeight", null);
            method.setAccessible(true);
            int height = (Integer) method.invoke(imm, null);
            return height;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;

    }

}
