package cn.heyanle.musicballpro.view.activities.main;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import java.util.Enumeration;

import cn.heyanle.musicballpro.R;
import cn.heyanle.musicballpro.presenters.MainPresenter;
import cn.heyanle.musicballpro.utils.HeLog;
import dalvik.system.DexFile;

/**
 * 主Activity 实现接口
 * layout:activity_main
 * @see cn.heyanle.musicballpro.presenters.MainPresenter
 * Created by HeYanLe
 * 2019/1/26 0026
 * https://github.com/heyanLE
 */
public class MainActivity extends AppCompatActivity implements IMainActivity {

    private MainPresenter mainPresenter;

    //请求状态码
    //public static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenter(this);

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


}
