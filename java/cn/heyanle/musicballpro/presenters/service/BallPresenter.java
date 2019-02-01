package cn.heyanle.musicballpro.presenters.service;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.model.MainModel;
import cn.heyanle.musicballpro.model.MusicModel;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.view.view.MusicBall;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * BallPresenter
 * 小球交互
 * Created by HeYanLe
 * 2019/1/30 0030
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
    }

    /**
     * 数据更新监听
     */
    private MainModel.OnDataChangeListener onDataChangeListener = new MainModel.OnDataChangeListener() {
        @Override
        public void onDataChange(String what, Object obj) {
            switch (what){
                case MainModel.IS_OPEN://总开关
                    Boolean b = (Boolean) obj;
                    if (b){//开启
                        if (MusicModel.getInstance().getNowMusic()!= null){

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
    private MusicModel.OnMusicChangeListener onMusicChangeListener = new MusicModel.OnMusicChangeListener() {
        @Override
        public void onMusicChange(MusicInfo musicInfo) {
            if (musicInfo == null) {
                musicBall.stopTurn();
                presenter.dismiss();
            } else {
                HeLog.i("ballMusic", musicInfo.toString(), this);
                presenter.show();//显示小球
                musicBall.setImgDrawable(musicInfo.getAlbumCover());//设置封面

                if (musicInfo.isPlaying()) {//播放
                    musicBall.startTurn();//开始旋转
                } else {
                    musicBall.pauseTurn();//暂停旋转
                }

            }
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
                            moveBack(x,y);
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
                            moveBack(x,y);
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
                            moveBack(x,y);
                            if (listener != null){
                                listener.onHorizontalSlide();
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        musicBall.isNotPress();
                        if (MusicModel.getInstance().getNowMusic().isPlaying()){
                            musicBall.startTurn();
                        }

                        if (!isMove && !isListener) break;

                        handler.removeCallbacks(r);
                        if (!isMove){
                            moveBack(x,y);
                        }else if(isKeepEdge){
                            moveToEdge();
                            isMove = false;
                        }else{
                            isMove = false;
                        }
                        if (!isMove && ! isOut){
                            //点击
                            if (listener != null){
                                listener.onClick();
                            }

                        }
                        break;

                }
                return false;
            }
        });


        MusicModel.getInstance().addOnMusicChangeListener(onMusicChangeListener);


    }

    private void moveBack(int xx,int yy){

        final float deltaX = (float) xx - (float) oldX;
        final float deltaY = (float) yy - (float) oldY;

        musicBall.isNotPress();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(20);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float f = (Float) animation.getAnimatedValue();
                x -= deltaX*f;
                y -= deltaY*f;
                presenter.move(x,y);
            }
        });
        //valueAnimator.start();

        presenter.move(oldX,oldY);

    }

    /**
     * 贴边
     */
    private void moveToEdge(){

        ValueAnimator valueAnimator ;

        if (x >= (presenter.getXLength()-MainModel.getInstance().getBallSize()-MainModel.getInstance().getBallBackgroundSize())/2){
            valueAnimator = ValueAnimator.ofInt(x,presenter.getXLength()-MainModel.getInstance().getBallSize()-2*MainModel.getInstance().getBallBackgroundSize());
            presenter.setpX(1);
        }else{
            valueAnimator = ValueAnimator.ofInt(x,0);
            presenter.setpX(0);
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

    public BallPresenter (Context context){

        presenter = new BallViewPresenter(context);
        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        MainModel.getInstance().addOnDataChangeListener(onDataChangeListener);

        presenter.init();
        init();


    }

    /**
     * 屏幕翻转时调用
     */
    public void onConfigurationChanged() {
        presenter.onConfigurationChanged();
        x = (int)(presenter.getXLength()*presenter.getpX()+0.5f);
        y = (int)(presenter.getYLength()*presenter.getpY()+0.5f);
        if (isKeepEdge){
            moveToEdge();
        }
    }


    /**
     * 当Service销毁时候调用
     */
    public void onDestroy(){
        presenter.onDestroy();
        MainModel.getInstance().removeDataChangeListener(onDataChangeListener);
        MusicModel.getInstance().removeMusicChangeListener(onMusicChangeListener);
    }

}
