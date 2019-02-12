package cn.heyanle.musicballpro.models.notification.data;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.heyanle.musicballpro.bean.FunctionInfo;
import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.utils.HeLog;

/**
 * QQ音乐
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
@NData
public class TencentQQMusic extends NotificationData {

    @Nullable
    @Override
    public MusicInfo getMusicInfo(StatusBarNotification sbn) {
        String name = "";
        String singer = "";
        String album = "";
        boolean isPlaying = false;
        Drawable albumCover = null;

        MusicInfo info = new MusicInfo();

        Resources resources = null;
        final Notification notification = sbn.getNotification();

        FunctionInfo longTouch = new FunctionInfo() {
            @Override
            public void run() throws Exception {
                notification.contentIntent.send();
            }
        };

        info.setMusicPage(longTouch);

        //如果是系统样式
        if (notification.extras.containsKey("android.template")&&notification.extras.getString("android.template").contains("android.app.Notification$MediaStyle")){

            try {
                resources = context.getPackageManager().getResourcesForApplication(getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            //获取封面并给
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notification.getLargeIcon() != null)
                    albumCover = notification.getLargeIcon().loadDrawable(context);
            }


            String text = notification.extras.getString("android.text");
            if (text == null) text = "";

            String[] texts = text.split(" - ");
            if (texts.length==2){
                singer = texts[0];
                album = texts[1];

            }

            name = notification.tickerText.toString();

            //获取按钮Action
            int actionCount = notification.actions.length;
            for (int i = 0; i < actionCount; i++) {

                final Notification.Action action = notification.actions[i];
                //Debug.i("QQMusicViewId",resources.getResourceEntryName(action.getIcon()),this);
                if (resources != null) {
                    try {
                        //获取到图标名字来判断是暂停还是播放

                        //Debug.i("Icon",resources.getResourceEntryName(action.getIcon()),this);
                        if (getResourceEntryName(resources,action).equals("player_notification_pause")){
                            FunctionInfo click = new FunctionInfo() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                }
                            };
                            info.setClick(click);
                            isPlaying = true;

                        }
                        if (getResourceEntryName(resources,action).equals("player_notification_play")){
                            FunctionInfo f = new FunctionInfo() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                }
                            };
                            info.setClick(f);
                            isPlaying = false;
                        }
                        if (getResourceEntryName(resources,action).equals("player_notification_next")){
                            FunctionInfo f = new FunctionInfo() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                }
                            };
                            info.setNext(f);
                        }
                        if (getResourceEntryName(resources,action).equals("player_notification_pre")){
                            FunctionInfo f = new FunctionInfo() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                }
                            };
                            info.setLast(f);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return info.name(name).singer(singer).setPlaying(isPlaying).albumCover(albumCover);
        }
        //如果是普通样式
        else{

            try {
                resources = context.getPackageManager().getResourcesForApplication(getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            try {

                //获得ViewRoot
                ViewGroup notificationRoot = (ViewGroup) notification.bigContentView.apply(context
                        , new FrameLayout(context));

                for (int i = 0 ; i < notificationRoot.getChildCount() ; i ++){
                    //按钮布局
                    if (notificationRoot.getChildAt(i) instanceof LinearLayout){

                        LinearLayout linearLayout = (LinearLayout) notificationRoot.getChildAt(i);
                        //上一首
                        final View v1 = linearLayout.getChildAt(1);
                        FunctionInfo fL = new FunctionInfo() {
                            @Override
                            public void run() throws Exception {
                                v1.performClick();
                            }
                        };
                        info.setLast(fL);

                        //播放按钮
                        final ImageView v2 = (ImageView) linearLayout.getChildAt(2);
                        //这里通过拿到View的图片资源Bitmap 判断中间像素点颜色来判断是否播放
                        Bitmap mp = drawableToBitmap(v2.getDrawable());
                        int color = mp.getPixel(mp.getWidth()/2,mp.getHeight()/2);
                        FunctionInfo f1 = new FunctionInfo() {
                            @Override
                            public void run() throws Exception {
                                v2.performClick();
                            }
                        };
                        info.setClick(f1);
                        if (color == 0){
                            isPlaying = true;

                        }else{
                            isPlaying = false;
                        }

                        //下一首按钮
                        final View v3 = linearLayout.getChildAt(3);
                        FunctionInfo fN = new FunctionInfo() {
                            @Override
                            public void run() throws Exception {
                                v3.performClick();
                            }
                        };
                        info.setNext(fN);

                    }

                    else if(notificationRoot.getChildAt(i) instanceof RelativeLayout){

                        RelativeLayout relativeLayout = (RelativeLayout) notificationRoot.getChildAt(i);

                        //封面布局
                        if (relativeLayout.getChildCount() == 1){
                            albumCover = ((ImageView)(relativeLayout.getChildAt(0))).getDrawable();
                        }

                        //信息布局
                        if (relativeLayout.getChildCount() == 3){
                            //albumCover = ((ImageView)(relativeLayout.getChildAt(0))).getDrawable();
                            for (int ii = 0 ; ii < relativeLayout.getChildCount() ; ii ++){

                                if (relativeLayout.getChildAt(ii) instanceof TextView){

                                    TextView t = (TextView) relativeLayout.getChildAt(ii);
                                    int inn = t.getCurrentTextColor();
                                    //黑色字体 为歌曲名称
                                    if (inn == -15263977){
                                        name = t.getText().toString();
                                    }
                                    //灰色名称 为歌手名字
                                    if (inn == -1979711488){
                                        singer = t.getText().toString();
                                    }


                                }

                            }
                        }

                    }

                }

                return info.name(name).singer(singer).setPlaying(isPlaying).albumCover(albumCover);


            }catch (Exception e){
                e.printStackTrace();
            }

        }
        //return null;
        return null;
    }

    public TencentQQMusic(){}

    @Override
    public String getPackageName() {
        return "com.tencent.qqmusic";
    }
}
