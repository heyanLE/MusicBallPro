package cn.heyanle.musicballpro.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cn.heyanle.musicballpro.R;
import cn.heyanle.musicballpro.model.MainModel;

/**
 * 报错界面 Activity
 * layout:activity_nul
 * Created by HeYanLe
 * 2019/1/26 0026
 * https://github.com/heyanLE
 */
public class NulActivity extends AppCompatActivity {

    public static String INTENT_KEY = "heyanle_nul_intent_key";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nul);

        MainModel.getInstance().isOpen(false).xP(0).yP(0.5f).apply();

        initView();
    }

    /**
     * 初始化View
     */
    private void initView(){

        TextView tvModel = findViewById(R.id.activity_nul_tv_model);
        TextView tvAndroidVersion = findViewById(R.id.activity_nul_tv_androidVersion);
        TextView tvELog = findViewById(R.id.activity_nul_tv_eLog);

        /*
        手机型号
         */
        String model = getResources().getString(R.string.phone_model,android.os.Build.MODEL);
        tvModel.setText(model);

        /*
        安卓版本
         */
        String aVersion = getResources().getString(R.string.android_version,android.os.Build.VERSION.RELEASE);
        tvAndroidVersion.setText(aVersion);

        /*
        报错信息
         */
        Intent intent = getIntent();
        if (intent == null) return;

        String eLog = intent.getStringExtra(INTENT_KEY);

        tvELog.setText(eLog);

    }

}
