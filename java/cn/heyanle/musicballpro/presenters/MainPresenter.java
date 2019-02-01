package cn.heyanle.musicballpro.presenters;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.InputStream;

import cn.heyanle.musicballpro.R;
import cn.heyanle.musicballpro.model.MainModel;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.PermissionHelper;
import cn.heyanle.musicballpro.view.activities.main.IMainActivity;
import cn.heyanle.musicballpro.view.activities.main.MainActivity;
import cn.heyanle.musicballpro.view.view.SwitchRelative;

/**
 * MainPresenter 主要的主持者
 * 属于MainActivity
 * @see cn.heyanle.musicballpro.view.activities.main.MainActivity
 * Created by HeYanLe
 * 2019/1/26 0026
 * https://github.com/heyanLE
 */
public class MainPresenter {

    /**
     * MainActivity接口变量
     */
    private IMainActivity iMain;


    /**
     * View对象
     */

    private SwitchRelative rMainSwitch;             //总开关
    private RelativeLayout rRunInBackground;        //后台运行权限
    private SwitchRelative rNotificationListener;   //通知使用权
    private SwitchRelative rShowOverlay;            //悬浮球权限
    private SeekBar sBallSize;                      //小球大小
    private SeekBar sBallBackgroundSize;            //小球背景半透明大小
    private SeekBar sBallBorderWidth;               //小球边框宽度
    private SwitchRelative rKeepEdge;               //自动贴边

    /**
     * 构造方法
     * @param iMain             MainActivity的接口
     */
    public MainPresenter(IMainActivity iMain){
        this.iMain = iMain;
    }

    /**
     * 开关状态更新
     */
    private void updateSwitch(){

        rMainSwitch.setChecked(MainModel.getInstance().isOpen());//总开关

        boolean notificationEnable = PermissionHelper.notificationListenerEnable(iMain.getContext());
        rNotificationListener.setChecked(notificationEnable);//通知使用权

        boolean drawOverlayEnable = PermissionHelper.drawOverlayEnable(iMain.getContext());
        rShowOverlay.setChecked(drawOverlayEnable);//悬浮窗权限

        rKeepEdge.setChecked(MainModel.getInstance().isKeepEdge());//自动贴边

    }

    /**
     * 更新拖动条
     */
    private void updateSeekBar(){

        sBallSize.setProgress(MainModel.getInstance().getBallSize() - 100);//小球大小
        sBallBackgroundSize.setProgress(MainModel.getInstance().getBallBackgroundSize());//小球背景半透明大小
        sBallBorderWidth.setProgress(MainModel.getInstance().getBallBorderWidth());//小球边框宽度

    }

    /**
     * 是否同时拥有所有权限（悬浮窗和通知使用权）
     * @return              是否
     */
    private boolean isAllPermissionEnable(){
        return PermissionHelper.notificationListenerEnable(iMain.getContext())
                && PermissionHelper.drawOverlayEnable(iMain.getContext());
    }

    /**
     * 小球大小SeekBar滑动时候数据寄存
     */
    private int ballSize = 0;

    /**
     * Start生命周期（与MainActivity同步）
     */
    public void onStart(){

        /*
        初始化各种View
         */
        rMainSwitch = (SwitchRelative) iMain.findView(R.id.activity_rLayout_mainSwitch);
        rRunInBackground = (RelativeLayout) iMain.findView(R.id.activity_rLayout_runInBack);
        rShowOverlay = (SwitchRelative) iMain.findView(R.id.activity_rLayout_overlay);
        rNotificationListener = (SwitchRelative) iMain.findView(R.id.activity_rLayout_notification);
        rKeepEdge = (SwitchRelative) iMain.findView(R.id.activity_rLayout_line);

        sBallSize = (SeekBar) iMain.findView(R.id.activity_seek_ballSize);
        sBallBackgroundSize = (SeekBar) iMain.findView(R.id.activity_seek_alpha);
        sBallBorderWidth = (SeekBar) iMain.findView(R.id.activity_seek_border);

        /*
        各种RelativeLayout的监听
         */
        View.OnClickListener relativeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.activity_rLayout_runInBack://后台运行
                        PermissionHelper.gotoAppDetailSetting(iMain.getContext());//跳转到应用详情页
                        Toast.makeText(iMain.getContext(),//提示授予权限
                                R.string.please_allow_run_on_back,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.activity_rLayout_dodge:
                        break;
                }
                //PermissionHelper.gotoRunInBackgroundSetting(iMain.getContext());//跳转到应用详情页
            }
        };

        SwitchRelative.OnSwitchChangeListener switchListener = new SwitchRelative.OnSwitchChangeListener() {
            @Override
            public void onSwitchChange(SwitchRelative view,boolean isPress, boolean isChecked) {
                if (isPress){

                    switch (view.getId()){

                        case R.id.activity_rLayout_mainSwitch://总开关
                            if (isAllPermissionEnable()) {//如果权限已经开启
                                MainModel.getInstance().isOpen(isChecked).apply();
                            }else{
                                view.setChecked(false);//关闭
                                Toast.makeText(iMain.getContext(),//提示授予权限
                                        R.string.please_allow_permission,
                                        Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.activity_rLayout_overlay://悬浮球权限
                            if (MainModel.getInstance().isOpen()){//如果总开关为开启
                                view.setChecked(!isChecked);
                                Toast.makeText(iMain.getContext(),//提示先关闭总开关
                                        R.string.please_close_main_switch,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {//如果总开关为关闭
                                if (isChecked) {//如果点击后是开启
                                    Toast.makeText(iMain.getContext(),//提示授予权限
                                            R.string.please_allow_draw_overlay,
                                            Toast.LENGTH_SHORT).show();
                                }
                                PermissionHelper.gotoDrawOverlaySetting(iMain.getContext());//跳转到申请界面
                            }
                            break;
                        case R.id.activity_rLayout_notification://通知使用权
                            if (MainModel.getInstance().isOpen()){//如果总开关为开启
                                view.setChecked(!isChecked);
                                Toast.makeText(iMain.getContext(),//提示先关闭总开关
                                        R.string.please_close_main_switch,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {//如果总开关为关闭
                                if (isChecked) {//如果点击后是开启
                                    Toast.makeText(iMain.getContext(),//提示授予权限
                                            R.string.please_allow_notification_listener,
                                            Toast.LENGTH_SHORT).show();
                                }
                                PermissionHelper.gotoNotificationAccessSetting(iMain.getContext());//跳转到申请界面
                            }
                            break;
                        case R.id.activity_rLayout_line:
                            HeLog.i("avoidKeyboard",isChecked+"",this);
                            MainModel.getInstance().keepEdge(isChecked).apply();
                    }

                }
            }
        };


        /*
        点击事件绑定
         */

        rRunInBackground.setOnClickListener(relativeListener);

        rMainSwitch.setOnSwitchChangeListener(switchListener);
        rNotificationListener.setOnSwitchChangeListener(switchListener);
        rShowOverlay.setOnSwitchChangeListener(switchListener);
        rKeepEdge.setOnSwitchChangeListener(switchListener);

        /*
        SeekBar配置
         */

        sBallSize.setMax(200);//滑动范围
        sBallBorderWidth.setMax(50);
        sBallBackgroundSize.setMax(50);

        /*
        SeekBar监听
         */
        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            /**
             * 位置改变
             * @param seekBar           SeekBar对象
             * @param progress          滑动位置
             * @param fromUser          是否使用者滑动
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    switch (seekBar.getId()) {

                        case R.id.activity_seek_ballSize://小球大小
                            ballSize = progress + 100;//保存到寄存器 结束滑动时候更新和保存
                            break;
                        case R.id.activity_seek_alpha://背景半透明大小 直接更新 但结束滑动时保存
                            MainModel.getInstance().ballBackgroundSize(progress);
                            break;
                        case R.id.activity_seek_border://小球边框宽度 直接更新 但结束滑动时保存
                            MainModel.getInstance().ballBorderWidth(progress);
                            break;


                    }
                }
            }

            /**
             * 开始滑动
             * @param seekBar           SeekBar对象
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            /**
             * 结束滑动
             * @param seekBar           SeekBar对象
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //更新小球大小 + 保存： 小球大小 背景半透明大小  小球边框宽度
                //if (MainModel.getInstance() == null) HeLog.i("null",this);
                MainModel.getInstance().ballSize(ballSize).apply();

            }
        };

        sBallSize.setOnSeekBarChangeListener(seekBarListener);
        sBallBackgroundSize.setOnSeekBarChangeListener(seekBarListener);
        sBallBorderWidth.setOnSeekBarChangeListener(seekBarListener);

        /*
        更新状态
         */
        updateSeekBar();
        updateSwitch();

        iMain.findView(R.id.activity_rLayout_author).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(iMain.getContext());
                builder.setTitle(iMain.getContext().getText(R.string.dialog_title_author));
                builder.setMessage(iMain.getContext().getText(R.string.dialog_msg_author));
                builder.setPositiveButton("确定", null);
                builder.show();
            }
        });

        iMain.findView(R.id.activity_rLayout_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(iMain.getContext());
                builder.setTitle(iMain.getContext().getText(R.string.donation));
                builder.setItems(new String[]{"支付宝","微信"},new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            //支付宝
                            Uri uri = Uri.parse("https://qr.alipay.com/fkx09847iklbas7bqyv9w0f?t=1548940425674");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            iMain.getContext().startActivity(intent);
                            Toast.makeText(iMain.getContext(), "谢谢支持！", Toast.LENGTH_SHORT).show();
                        }else{
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(iMain.getContext());
                            builder.setTitle(iMain.getContext().getText(R.string.savePng));
                            final ImageView imageView = new ImageView(iMain.getContext());
                            imageView.setImageResource(R.drawable.weixin);
                            builder.setView(imageView);
                            builder.setPositiveButton("确定", null);
                            builder.setNegativeButton("保存到相册", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String[] PERMISSIONS_STORAGE = {
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE};

                                    int REQUEST_PERMISSION_CODE = 1;

                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                        if (ActivityCompat.checkSelfPermission(iMain.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            Toast.makeText(iMain.getContext(),"请授予读写权限",Toast.LENGTH_SHORT).show();
                                            ActivityCompat.requestPermissions((Activity) iMain.getContext(), PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                                        }else{
                                            Resources r = iMain.getContext().getResources();
                                            Bitmap bmp=BitmapFactory.decodeResource(r, R.drawable.weixin);
                                            //Bitmap newb = Bitmap.createBitmap( 300, 300, Bitmap.Config.ARGB_8888 );
                                            MediaStore.Images.Media.insertImage(iMain.getContext().getContentResolver(),bmp, "title", "description");
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");

                                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setComponent(cmp);
                                                iMain.getContext().startActivity(intent);

                                                Toast.makeText(iMain.getContext(), "请扫码支付！谢谢支持！", Toast.LENGTH_SHORT).show();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                Toast.makeText(iMain.getContext(), "打开微信失败，请手动打开扫码支付，谢谢支持！", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            });
                            builder.show();
                        }
                    }
                });
                builder.setPositiveButton("残忍拒绝", null);
                builder.show();
            }
        });

    }

    /**
     * Restart生命周期（与MainActivity同步）
     */
    public void onRestart(){
        /*
        各种更新
         */
        updateSeekBar();
        updateSwitch();
    }

    /**
     * Destroy生命周期（与MainActivity同步）
     */
    public void onDestroy(){}

}
