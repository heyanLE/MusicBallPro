package cn.heyanle.musicballpro.presenters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.logging.Handler;

import cn.heyanle.musicballpro.R;
import cn.heyanle.musicballpro.models.MainModel;
import cn.heyanle.musicballpro.qmui.QMUIKeyboardHelper;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.PermissionHelper;
import cn.heyanle.musicballpro.view.activities.NoteActivity;
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
    private SeekBar sBallAlpha;
    private SwitchRelative rKeepEdge;               //自动贴边

    private SwitchRelative rDodge;                  //避让输入法-开关
    private RelativeLayout rSet;                    //点击记录输入法高度

    private CardView cardView;                      //记录窗口
    private TextView nowKeybardSize;                //当前输入法高度
    private SwitchRelative rIsTrun;                  //是否旋转

    private View vv;

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

        rDodge.setChecked(MainModel.getInstance().isAvoidKeyboard());//避让输入法

        rIsTrun.setChecked(MainModel.getInstance().isTurn());

    }

    /**
     * 更新拖动条
     */
    private void updateSeekBar(){

        sBallSize.setProgress(MainModel.getInstance().getBallSize() - 100);//小球大小
        sBallBackgroundSize.setProgress(MainModel.getInstance().getBallBackgroundSize());//小球背景半透明大小
        sBallBorderWidth.setProgress(MainModel.getInstance().getBallBorderWidth());//小球边框宽度
        sBallAlpha.setProgress(MainModel.getInstance().getAlpha());

        ballSize = MainModel.getInstance().getBallSize();

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
        rDodge = (SwitchRelative) iMain.findView(R.id.activity_rLayout_dodge);
        rIsTrun = (SwitchRelative) iMain.findView(R.id.activity_rLayout_round);

        sBallSize = (SeekBar) iMain.findView(R.id.activity_seek_ballSize);
        sBallBackgroundSize = (SeekBar) iMain.findView(R.id.activity_seek_alpha);
        sBallBorderWidth = (SeekBar) iMain.findView(R.id.activity_seek_border);
        sBallAlpha = (SeekBar) iMain.findView(R.id.activity_seek_lpha);

        rSet = (RelativeLayout) iMain.findView(R.id.activity_rLayout_set);
        cardView = (CardView) iMain.findView(R.id.activity_set_card) ;

        nowKeybardSize = (TextView) iMain.findView(R.id.activity_tv);

        final int i = MainModel.getInstance().getKeyboardSize();
        if (i <= 0){

            //MainModel.getInstance().keyboardSize(-1).apply();
            String ss = iMain.getContext().getResources().getString(R.string.click_to_set_up_keyboard_size);
            nowKeybardSize.setText(ss);

        }
        else{
            ///MainModel.getInstance().keyboardSize(i).apply();
            String ss = iMain.getContext().getResources().getString(R.string.keyboard_size,i);
            nowKeybardSize.setText(ss);
        }

        vv = iMain.findView(R.id.activity_set_card_v);

        final EditText e = cardView.findViewById(R.id.activity_main_edit);
        Button getN = cardView.findViewById(R.id.activity_main_set);
        Button yes = cardView.findViewById(R.id.activity_main_yes);
        Button no = cardView.findViewById(R.id.activity_main_no);

        rSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vv.setVisibility(View.VISIBLE);
                        cardView.setVisibility(View.VISIBLE);
                        int i = MainModel.getInstance().getKeyboardSize();
                        if (i <= 0){

                            //MainModel.getInstance().keyboardSize(-1).apply();
                            //String ss = iMain.getContext().getResources().getString(R.string.click_to_set_up_keyboard_size);
                            e.setText("-1");

                        }
                        else{
                            ///MainModel.getInstance().keyboardSize(i).apply();
                            String ss = i+"";
                            e.setText(ss);
                        }

                        QMUIKeyboardHelper.showKeyboard(e,false);
                    }
                }, 200);

            }
        });

        getN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String s = MainActivity.keyboardSize+"";
                        e.setText(s);
                    }
                }, 200);

            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String s = e.getText().toString();
                        int i = -1;
                        try {
                            i = Integer.parseInt(s);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (i <= 0){

                            MainModel.getInstance().keyboardSize(-1).apply();
                            String ss = iMain.getContext().getResources().getString(R.string.click_to_set_up_keyboard_size);
                            nowKeybardSize.setText(ss);

                        }
                        else{
                            MainModel.getInstance().keyboardSize(i).apply();
                            String ss = iMain.getContext().getResources().getString(R.string.keyboard_size,i);
                            nowKeybardSize.setText(ss);
                        }
                        vv.setVisibility(View.GONE);
                        cardView.setVisibility(View.GONE);
                        QMUIKeyboardHelper.hideKeyboard(e);
                    }
                }, 200);

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vv.setVisibility(View.GONE);
                        cardView.setVisibility(View.GONE);
                        QMUIKeyboardHelper.hideKeyboard(e);
                    }
                }, 200);

            }
        });

        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        QMUIKeyboardHelper.hideKeyboard(e);
                        vv.setVisibility(View.GONE);
                        cardView.setVisibility(View.GONE);
                    }
                }, 200);
            }
        });

        iMain.findView(R.id.activity_main_adjust).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        e.setText("-1");
                        MainModel.getInstance().keyboardSize(-1).apply();
                        vv.setVisibility(View.GONE);
                        cardView.setVisibility(View.GONE);
                        QMUIKeyboardHelper.hideKeyboard(e);
                    }
                },200);
            }
        });

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
                            break;
                        case R.id.activity_rLayout_dodge:
                            MainModel.getInstance().avoidKeyboard(isChecked).apply();
                            break;
                        case R.id.activity_rLayout_round:
                            MainModel.getInstance().setTurn(isChecked).apply();
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
        rDodge.setOnSwitchChangeListener(switchListener);
        rIsTrun.setOnSwitchChangeListener(switchListener);

        /*
        SeekBar配置
         */

        sBallSize.setMax(200);//滑动范围
        sBallBorderWidth.setMax(50);
        sBallBackgroundSize.setMax(50);
        sBallAlpha.setMax(254);

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
                        case R.id.activity_seek_lpha:
                            MainModel.getInstance().alpha(progress);
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
        sBallAlpha.setOnSeekBarChangeListener(seekBarListener);

        /*
        更新状态
         */
        updateSeekBar();
        updateSwitch();

        iMain.findView(R.id.activity_rLayout_author).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(iMain.getContext());
                builder.setTitle(iMain.getContext().getText(R.string.dialog_title_author));
                builder.setMessage(iMain.getContext().getText(R.string.dialog_msg_author));
                builder.setPositiveButton("确定", null);
                builder.show();
            }
        });

        iMain.findView(R.id.activity_rLayout_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(iMain.getContext());
                builder.setTitle(iMain.getContext().getText(R.string.donation));
                builder.setMessage("谢谢支持");
                builder.setNegativeButton("支付宝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("https://qr.alipay.com/fkx08279lzlulckwbawjj90");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        iMain.getContext().startActivity(intent);
                    }
                });
                builder.setPositiveButton("残忍拒绝", null);
                builder.show();
            }
        });

        iMain.findView(R.id.activity_rLayout_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(iMain.getContext(),NoteActivity.class);
                iMain.getContext().startActivity(intent);
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

    public void onBackPressed(){
        if (vv.getVisibility() == View.VISIBLE){
            vv.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        }
        else{
            iMain.finishMy();
        }
    }

}
