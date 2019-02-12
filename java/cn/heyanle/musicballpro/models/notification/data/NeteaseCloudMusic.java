package cn.heyanle.musicballpro.models.notification.data;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.service.notification.StatusBarNotification;
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
 * 网易云音乐
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
@NData
public class NeteaseCloudMusic extends NotificationData {

    //private Resources resources = null;
    //private Context context;

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

        //如果是系统样式
        if (notification.extras.containsKey(Notification.EXTRA_MEDIA_SESSION)){

            try {
                resources = context.getPackageManager().getResourcesForApplication(getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            //获取封面并给
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notification.getLargeIcon() != null)
                    albumCover=notification.getLargeIcon().loadDrawable(context);
            }


            String text = notification.extras.getString("android.Text");
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
                if (resources != null) {
                    try {
                        //获取到图标名字来判断是暂停还是播放
                        /*
                        note_btn_loved 我喜欢 开 note_btn_love 我喜欢 关
                        note_btn_pre 上一首
                        note_btn_pause_ms 暂停 和  note_btn_play_ms 播放
                        note_btn_next 下一首
                        note_btn_lyc_mc 歌词 关 和 note_btn_lyced_ms 歌词 开
                         */
                        //HeLog.i("Icon", resources.getResourceEntryName(action.getIcon()), this);
                        if (getResourceEntryName(resources,action).equals("note_btn_pause_ms")) {
                            FunctionInfo click = new FunctionInfo() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                }
                            };
                            info.setClick(click);
                            isPlaying = true;

                        }
                        if (getResourceEntryName(resources,action).equals("note_btn_play_ms")) {
                            FunctionInfo f = new FunctionInfo() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                }
                            };
                            info.setClick(f);
                            isPlaying = false;
                        }
                        if (getResourceEntryName(resources,action).equals("note_btn_next")) {
                            FunctionInfo f = new FunctionInfo() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                }
                            };
                            info.setNext(f);
                        }
                        if (getResourceEntryName(resources,action).equals("note_btn_pre")) {
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
        else if (notification.tickerText != null
                && notification.tickerText.toString().equals("网易云音乐正在播放")){

            try {
                resources = context.getPackageManager().getResourcesForApplication(getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (resources == null){
                return null;
            }

            //获得ViewRoot
            ViewGroup notificationRoot = (ViewGroup) notification.bigContentView.apply(context
                    , new FrameLayout(context));



            //得到布局文件
            RelativeLayout relativeLayout = (RelativeLayout) notificationRoot.getChildAt(0);

            //遍历布局文件 通过id找相应的view
            for (int i = 0 ; i < relativeLayout.getChildCount() ; i ++){
                final View view =  relativeLayout.getChildAt(i);
                //HeLog.i("View Id",resources.getResourceEntryName(view.getId()),this);
                String viewId = resources.getResourceEntryName(view.getId());

                //对应id
                switch (viewId){
                    case "playNotificationText":
                        LinearLayout linearLayout1 = (LinearLayout) view;
                        for (int ii = 0 ; ii < linearLayout1.getChildCount() ; ii ++) {
                            TextView tv = (TextView) linearLayout1.getChildAt(ii);
                            String Id =  resources.getResourceEntryName(tv.getId());
                            if (Id.equals("notifyTitle")) name = tv.getText().toString();
                            if (Id.equals("notifyText")) {
                                String text = tv.getText().toString();
                                //if (text == null) text = "";

                                String[] texts = text.split(" - ");
                                if (texts.length==2){
                                    singer = texts[0];
                                    //album = texts[1];

                                }
                            }
                        }
                        break;
                    case "notifyAlbumCover"://封面view 直接设置封面
                        //ballView.setImageDrawable(((ImageView)view).getDrawable());
                        albumCover = ((ImageView)view).getDrawable();
                        break;
                    case "playNotificationBtns"://按钮布局
                        //遍历按钮
                        LinearLayout linearLayout = (LinearLayout) view;
                        for (int ii = 0 ; ii < linearLayout.getChildCount() ; ii ++){
                            final ImageView imageView = (ImageView) linearLayout.getChildAt(ii);
                            //HeLog.i("Button Id",resources.getResourceEntryName(imageView.getId()),this);
                            String buttonId = resources.getResourceEntryName(imageView.getId());
                            switch (buttonId){
                                case "playNotificationStar":
                                    break;
                                case "playNotificationPre":
                                    FunctionInfo f = new FunctionInfo() {
                                        @Override
                                        public void run() throws Exception {
                                            imageView.performClick();
                                        }
                                    };
                                    info.setLast(f);
                                    break;
                                case "playNotificationToggle":
                                    //这里通过拿到View的图片资源Bitmap 判断中间像素点颜色来判断是否播放
                                    Bitmap mp = drawableToBitmap(imageView.getDrawable());
                                    int color = mp.getPixel(mp.getWidth()/2,mp.getHeight()/2);
                                    FunctionInfo f1 = new FunctionInfo() {
                                        @Override
                                        public void run() throws Exception {
                                            imageView.performClick();
                                        }
                                    };
                                    info.setClick(f1);
                                    isPlaying = color == 0;
                                    break;
                                case "playNotificationNext":
                                    FunctionInfo f2 = new FunctionInfo() {
                                        @Override
                                        public void run() throws Exception {
                                            imageView.performClick();
                                        }
                                    };
                                    info.setNext(f2);
                                    break;
                                case "playNotificationLyric":
                                    break;
                            }
                        }
                        break;
                }
            }
            return info.name(name).singer(singer).setPlaying(isPlaying).albumCover(albumCover);
        }
        return null;
        //return null;
    }



    public NeteaseCloudMusic(){}

    @Override
    public String getPackageName() {
        return "com.netease.cloudmusic";
    }
}
