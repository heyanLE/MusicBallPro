package cn.heyanle.musicballpro.view.activities;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.List;

import cn.heyanle.musicballpro.R;
import cn.heyanle.musicballpro.models.WhiteModel;

/**
 * Created by HeYanLe
 * 2019/2/5 0005
 * https://github.com/heyanLE
 */
public class NoteActivity extends Activity {

    private ListView listViewShow ;
    private ListView listViewMusic ;

    private ImageView imgShow ;
    private ImageView imgMusic ;

    private TextView textNo ;

    private PackageNameAdapter adapterShow;
    private PackageNameAdapter adapterMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
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

        listViewShow = findViewById(R.id.activity_note_list_show);
        listViewMusic = findViewById(R.id.activity_note_list_music);

        imgShow = findViewById(R.id.activity_note_img_show);
        imgMusic = findViewById(R.id.activity_note_img_music);

        textNo = findViewById(R.id.activity_note_tv);


        List<String> music = new ArrayList<>();
        WhiteModel.getInstance().cloneMusicWhite(music);
        adapterMusic = new PackageNameAdapter(this,music);
        adapterMusic.setOnRemovePackageName(new PackageNameAdapter.OnRemovePackageName() {
            @Override
            public void onRemove(String packageName) {
                WhiteModel.getInstance().removeMusicWhite(packageName);
            }
        });

        List<String> show = new ArrayList<>();
        WhiteModel.getInstance().cloneShowWhite(show);
        adapterShow = new PackageNameAdapter(this,show);
        adapterShow.setOnRemovePackageName(new PackageNameAdapter.OnRemovePackageName() {
            @Override
            public void onRemove(String packageName) {
                WhiteModel.getInstance().removeShowWhite(packageName);
            }
        });

        listViewMusic.setAdapter(adapterMusic);
        listViewShow.setAdapter(adapterShow);

        imgShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this,ChoosePackageActivity.class);
                startActivityForResult(intent,1);
            }
        });

        imgMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this,ChoosePackageActivity.class);
                startActivityForResult(intent,2);
            }
        });

        if (hasPermission()){

            textNo.setVisibility(View.GONE);
            imgShow.setVisibility(View.VISIBLE);

        }else{

            textNo.setVisibility(View.VISIBLE);
            imgShow.setVisibility(View.GONE);

        }

        textNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NoteActivity.this,"请开启相关权限",Toast.LENGTH_SHORT).show();
                NoteActivity.this.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        if (hasPermission()){

            textNo.setVisibility(View.GONE);
            imgShow.setVisibility(View.VISIBLE);

        }else{

            textNo.setVisibility(View.VISIBLE);
            imgShow.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        if (requestCode == 1){
            String s = data.getStringExtra("PackageName");
            if ( s != null && !s.isEmpty()) {
                adapterShow.addPackage(s);
                WhiteModel.getInstance().addShowWhite(s);
            }
        }
        if (requestCode == 2){
            String s = data.getStringExtra("PackageName");
            if ( s != null && !s.isEmpty()) {
                adapterMusic.addPackage(s);
                WhiteModel.getInstance().addMusicWhite(s);
            }
        }
    }


    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;

        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WhiteModel.getInstance().apply();
    }
}

class PackageNameAdapter extends BaseAdapter{

    interface OnRemovePackageName{
        void onRemove(String packageName);
    }

    static class ItemInfo{
        String name;
        Drawable icon;
        String packageName;
    }

    static class ViewHolder{
        TextView textView;
        ImageView imageView;
    }

    private List<ItemInfo> list = new ArrayList<>();
    private Context mContext;
    private PackageManager manager;

    private OnRemovePackageName name  = null;

    public void setOnRemovePackageName(OnRemovePackageName name) {
        this.name = name;
    }

    public PackageNameAdapter(Context context, List<String> packageNames){
        mContext = context;
        manager = context.getPackageManager();
        try{
            for (String n:packageNames) {
                ApplicationInfo appInfo = manager.getApplicationInfo(n,PackageManager.GET_META_DATA);
                ItemInfo info = new ItemInfo();
                info.name = manager.getApplicationLabel(appInfo).toString();
                info.icon = manager.getApplicationIcon(n);
                info.packageName = n;
                list.add(info);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addPackage(String packageName){
        try {
            ApplicationInfo appInfo = manager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            ItemInfo info = new ItemInfo();
            info.name = manager.getApplicationLabel(appInfo).toString();
            info.icon = manager.getApplicationIcon(packageName);
            info.packageName = packageName;
            list.add(info);
            notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removePackage(String packageName){

        final int s = list.size();
        for (int i = 0 ; i < s ; i ++){

            if (list.get(i).packageName.equals(packageName)){
                list.remove(i);
                notifyDataSetChanged();
                break;
            }

        }

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_note, null);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.item_list_note_name);
            holder.imageView = convertView.findViewById(R.id.item_list_note_img);
            convertView.setTag(holder);
        }    else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(list.get(position).name);
        holder.imageView.setImageDrawable(list.get(position).icon);
        ImageView imageView = convertView.findViewById(R.id.item_list_note_close);
        imageView.setTag(list.get(position).packageName);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePackage(v.getTag().toString());
                if (name!= null){
                    name.onRemove(v.getTag().toString());
                }
            }
        });

        return convertView;
    }
}
