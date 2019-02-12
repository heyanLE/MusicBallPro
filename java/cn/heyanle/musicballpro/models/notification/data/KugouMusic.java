package cn.heyanle.musicballpro.models.notification.data;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.heyanle.musicballpro.bean.FunctionInfo;
import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.utils.HeLog;

/**
 * Created by HeYanLe
 * 2019/2/9 0009
 * https://github.com/heyanLE
 */
@NData
public class KugouMusic extends NotificationData{

    @Override
    public MusicInfo getMusicInfo(StatusBarNotification sbn) {

        String name = "";
        String singer = "";
        String album = "";
        boolean isPlaying = false;
        Drawable albumCover = null;

        MusicInfo info = new MusicInfo();

        final Notification notification = sbn.getNotification();

        FunctionInfo musicPage = new FunctionInfo() {
            @Override
            public void run() throws Exception {
                HeLog.i("send",this);
                notification.contentIntent.send();
            }
        };

        info.setMusicPage(musicPage);

        Resources resources = null;

        try {
            resources = context.getPackageManager().getResourcesForApplication(getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //获得ViewRoot
        ViewGroup notificationRoot = (ViewGroup) notification.bigContentView.apply(context
                , new FrameLayout(context));

        notificationRoot.getChildCount();

        try{

            ImageView iv = null;


            for(int i = 0 ; i < notificationRoot.getChildCount() ; i ++){

                View v = notificationRoot.getChildAt(i);
                if (v instanceof ImageView){

                    if (iv == null){
                        iv = (ImageView) v;
                    }else if(((ImageView)(v)).getHeight() > iv.getHeight()){
                        iv = (ImageView) v;
                    }
                    continue;
                }

                if (v instanceof LinearLayout){
                    LinearLayout l = (LinearLayout) ((LinearLayout)v).getChildAt(1);
                    name = ((TextView)l.getChildAt(0)).getText().toString();
                    singer = ((TextView)l.getChildAt(1)).getText().toString();

                    LinearLayout ll = (LinearLayout) ((LinearLayout)v).getChildAt(3);
                    final ImageButton ivLast = (ImageButton) ll.getChildAt(3);
                    final ImageButton ivPlay = (ImageButton) ll.getChildAt(5);
                    final ImageButton ivNext = (ImageButton) ll.getChildAt(7);

                    if (resources != null)
                    HeLog.i("play id name",resources.getResourceEntryName(ivPlay.getId()),this);

                    Bitmap mp = drawableToBitmap(ivPlay.getDrawable());
                    //int color = mp.getPixel(mp.getWidth()-1,mp.getHeight()/4);

                    isPlaying = true;
                    for (int i1 = 1 ; i1 < mp.getHeight() ; i1 ++){
                        if (mp.getPixel(mp.getWidth()/2,i1) != 0){
                            isPlaying = false;
                            break;
                        }
                    }

                    info.setLast(new FunctionInfo() {
                        @Override
                        public void run() throws Exception {
                            ivLast.performClick();
                        }
                    });
                    info.setClick(new FunctionInfo() {
                        @Override
                        public void run() throws Exception {
                            ivPlay.performClick();
                        }
                    });
                    info.setNext(new FunctionInfo() {
                        @Override
                        public void run() throws Exception {
                            ivNext.performClick();
                        }
                    });
                    //continue;
                }

            }

            if (iv != null) {
                albumCover = iv.getDrawable();
            }

            return info.name(name).singer(singer).setPlaying(isPlaying).albumCover(albumCover);

        }catch (Exception e){
            e.printStackTrace();
        }

        try{

            LinearLayout l = null;

            for (int i = 0 ; i < 2 ; i++){
                if(((LinearLayout)(notificationRoot.getChildAt(i))).getChildCount()==2){
                    l = (LinearLayout)((notificationRoot).getChildAt(i));
                }else{
                    LinearLayout l1 = (LinearLayout)((notificationRoot).getChildAt(i));
                    ImageView iv = (ImageView) l1.getChildAt(0);
                    albumCover = iv.getDrawable();
                }
            }
            if (l != null) {
                LinearLayout l1 = (LinearLayout) l.getChildAt(0);
                LinearLayout l2 = (LinearLayout) l.getChildAt(1);

                //l1.setOrientation();

                //albumCover = ((ImageView)l1.getChildAt(0)).getBackground();

                LinearLayout l3 = (LinearLayout) l1.getChildAt(1);

                name = ((TextView)((LinearLayout)l3.getChildAt(1)).getChildAt(0)).getText().toString();
                singer = ((TextView)((LinearLayout)l3.getChildAt(2)).getChildAt(0)).getText().toString();


                final ImageButton ibLast = (ImageButton) l2.getChildAt(1);
                final ImageButton ibPlay = (ImageButton) l2.getChildAt(2);
                final ImageButton ibNext = (ImageButton) l2.getChildAt(3);

                info.setLast(new FunctionInfo() {
                    @Override
                    public void run() throws Exception {
                        ibLast.performClick();
                    }
                });
                info.setClick(new FunctionInfo() {
                    @Override
                    public void run() throws Exception {
                        ibPlay.performClick();
                    }
                });
                info.setNext(new FunctionInfo() {
                    @Override
                    public void run() throws Exception {
                        ibNext.performClick();
                    }
                });

                Bitmap mp = drawableToBitmap(ibPlay.getDrawable());
                //int color = mp.getPixel(mp.getWidth()-1,mp.getHeight()/4);

                isPlaying = true;
                for (int i1 = 1 ; i1 < mp.getHeight() ; i1 ++){
                    if (mp.getPixel(mp.getWidth()/2,i1) != 0){
                        isPlaying = false;
                        break;
                    }
                }

                return info.name(name).singer(singer).setPlaying(isPlaying).albumCover(albumCover);

            }



            //LinearLayout l = ((FrameLayout)notificationRoot.getChildAt(1))


        }catch (Exception e){
            e.printStackTrace();
        }



        return null;
    }

    @Override
    public String getPackageName() {
        return "com.kugou.android";
    }
}
