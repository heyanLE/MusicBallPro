package cn.heyanle.musicballpro.view.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import cn.heyanle.musicballpro.R;
import cn.heyanle.musicballpro.models.MainModel;
import cn.heyanle.musicballpro.models.MusicModel;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * BallView 自定义View
 * 圆形ImageView  旋转  设置边框和大小 背景半透明大小
 * 需要设置本View的Height和Width设置为wrap_content
 * Created by HeYanLe
 * 2019/1/30 0030
 * https://github.com/heyanLE
 */
public class MusicBall extends RelativeLayout {

    //private Context context;

    /**
     * 旋转状态   0 -> 停止 ； 1 -> 暂停 ； 2 -> 旋转
     */
    private int isTurn = 0;

    private CircleImageView circleImageView;
    private ObjectAnimator objectAnimator;

    private int ballSize;

    private int backSize;

    //==================================旋转相关=====================================

    /**
     * 旋转复位 停止旋转+设置状态标识
     */
    public void reset(){
        objectAnimator.end();
        isTurn = 0;
    }

    /**
     * 开始旋转
     */
    public void startTurn(){
        if (!MainModel.getInstance().isTurn()){
            objectAnimator.end();
            return;
        }
        if (isTurn == 1){//如果是暂停状态
            objectAnimator.resume(); //继续旋转
            isTurn = 2;
        }else if(isTurn == 0){//如果停止
            objectAnimator.start(); //开始旋转
            isTurn = 2;
        }
    }

    /**
     * 停止旋转
     */
    public void stopTurn(){
        if (!MainModel.getInstance().isTurn()){
            objectAnimator.end();
            return;
        }
        objectAnimator.end();
        isTurn = 0;
    }

    /**
     * 暂停旋转
     */
    public void pauseTurn(){
        if (!MainModel.getInstance().isTurn()){
            objectAnimator.end();
            return;
        }
        objectAnimator.pause();
        isTurn = 1;
    }

    //==========================小球设置相关=======================================

    /**
     * 设置小球大小
     * @param ballSize          小球大小
     */
    public void setBallSize(int ballSize){
        circleImageView.getLayoutParams().width = ballSize;
        circleImageView.getLayoutParams().height = ballSize;
        this.ballSize = ballSize;
        updateViewLayout(circleImageView,circleImageView.getLayoutParams());
    }

    /**
     * 设置小球边框宽度
     * @param borderSize        边框宽度
     */
    public void setBallBorder(int borderSize){
        circleImageView.setBorderWidth(borderSize);
    }

    /**
     * 设置小球显示图片 下同
     * @param bitmap            Bitmap对象
     */

    public void setImgBitmap(Bitmap bitmap){
        circleImageView.setImageBitmap(bitmap);
    }

    public void setImgDrawable(Drawable drawable){
        circleImageView.setImageDrawable(drawable);
    }

    /**
     * 设置背景半透明宽度
     * @param backgroundSize    背景半透明宽度
     */
    public void setBackgroundSize(int backgroundSize){
        setPadding(backgroundSize,backgroundSize,backgroundSize,backgroundSize);
        backSize = backgroundSize;
    }

    /**
     * 按下状态 （内部小球变小10个像素点）
     */
    public void isPress(){
        circleImageView.getLayoutParams().width = ballSize - 20;
        circleImageView.getLayoutParams().height = ballSize - 20;
        setPadding(backSize+10,backSize+10,backSize+10,backSize+10);
        updateViewLayout(circleImageView,circleImageView.getLayoutParams());
    }

    /**
     * 从按下状态恢复
     */
    public void isNotPress(){
        setBallSize(ballSize);
        setPadding(backSize,backSize,backSize,backSize);
    }

    //============================================================================

    public MusicBall(Context context) {
        super(context);
        //this.context = context;

        circleImageView = new CircleImageView(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            circleImageView.setBorderColor(context.getColor(android.R.color.black));
        }else{
            circleImageView.setBorderColor(context.getResources().getColor(android.R.color.black));
        }


        setBackgroundResource(R.drawable.white);//背景半透明
        setGravity(Gravity.CENTER);
        setPadding(10,10,10,10);//背景半透明宽度（内边距）

        /*
        设置旋转ObjectAnimator
         */
        objectAnimator = ObjectAnimator.ofFloat(circleImageView,"rotation",0f,360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);

        /*
        添加ImageView 默认大小200
         */
        LayoutParams params = new LayoutParams(200,200);
        addView(circleImageView,params);

        ballSize = 200;
        backSize = 10;

    }
}
