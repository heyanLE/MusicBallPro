package cn.heyanle.musicballpro.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.heyanle.musicballpro.R;

/**
 * Created by HeYanLe
 * 2019/2/5 0005
 * https://github.com/heyanLE
 */
public class ChoosePackageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_package);

        Toolbar toolbar = findViewById(R.id.activity_choose_toolbar);
        setActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        if (getActionBar()!=null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
            getActionBar().setHomeButtonEnabled(true); //设置返回键可用
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {


                final List<Map<String,Object>> list = new ArrayList<>();

                try {

                    Intent intent = new Intent(Intent.ACTION_MAIN,null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);

                    PackageManager manager = getPackageManager();

                    List<ResolveInfo> packageInfos = manager.queryIntentActivities(intent,0);

                   // List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
                    for (ResolveInfo pInfo : packageInfos) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("packageName", pInfo.activityInfo.packageName);
                        ;
                        String name = pInfo.loadLabel(manager).toString();
                        map.put("name",name);

                        Drawable d = pInfo.loadIcon(manager);
                        map.put("img",d);

                        list.add(map);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String[] from = new String[]{"name","img"};
                int[] to = new int[]{R.id.item_list_note_name,R.id.item_list_note_img};
                final SimpleAdapter simpleAdapter = new SimpleAdapter(ChoosePackageActivity.this
                        ,list
                        ,R.layout.item_list_choose
                        ,from
                        ,to);

                final ListView listView = findViewById(R.id.activity_choose_list);

                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue(View view, Object data, String textRepresentation) {
                                if(view instanceof ImageView && data instanceof Drawable){
                                    ImageView iv = (ImageView)view;
                                    iv.setImageDrawable((Drawable) data);
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        });
                        listView.setAdapter(simpleAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    Intent intent = new Intent();
                                    intent.putExtra("PackageName", list.get(position).get("packageName").toString());
                                    setResult(1,intent);
                                    ChoosePackageActivity.this.finish();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }) ;


            }
        }).start();

    }
}
