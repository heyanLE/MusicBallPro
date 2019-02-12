package cn.heyanle.musicballpro.view.activities.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import cn.heyanle.musicballpro.C;
import cn.heyanle.musicballpro.R;
import cn.heyanle.musicballpro.network.CheckUpdate;
import cn.heyanle.musicballpro.presenters.MainPresenter;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.PermissionHelper;
import cn.heyanle.musicballpro.view.services.NotificationService;

/**
 * 主Activity 实现接口
 * layout:activity_main
 * @see cn.heyanle.musicballpro.presenters.MainPresenter
 * Created by HeYanLe
 * 2019/1/26 0026
 * https://github.com/heyanLE
 */
public class MainActivity extends Activity implements IMainActivity {

    public static int keyboardSize = -1;

    private MainPresenter mainPresenter;

    //请求状态码
    //public static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenter(this);

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        //Bmob.initialize(this, "f006ac8f97900d3e2601bb4161a69a49");

        CheckUpdate.get(new CheckUpdate.CallBack() {
            @Override
            public void onCallBack(final String version,final String description) {
                HeLog.i("GetInternet",version+"|"+description,this);
                try {
                    if (Float.parseFloat(version) > C.VERSION || C.IS_DEBUG) {
                        findViewById(R.id.activity_rLayout_mainSwitch).post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("检测到新版：" + version);
                                builder.setMessage("更新日志：" + description);
                                builder.setPositiveButton("立刻更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri uri = Uri.parse("https://www.coolapk.com/apk/cn.heyanle.musicballpro");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("取消", null);
                                builder.show();
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        if (PermissionHelper.notificationListenerEnable(this)) {
            PackageManager packageManager = getPackageManager();
            packageManager.setComponentEnabledSetting(new ComponentName(this, NotificationService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(new ComponentName(this, NotificationService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mainPresenter.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mainPresenter.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.onDestroy();
    }

    @Override
    public View findView(int viewId) {
        return findViewById(viewId);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mainPresenter.onBackPressed();
    }

    @Override
    public void finishMy() {
        finish();
    }
}

