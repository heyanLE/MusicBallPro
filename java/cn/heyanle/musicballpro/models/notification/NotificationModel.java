package cn.heyanle.musicballpro.models.notification;

import android.content.Context;
import android.service.notification.StatusBarNotification;

import org.jsoup.safety.Whitelist;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.models.MusicModel;
import cn.heyanle.musicballpro.models.WhiteModel;
import cn.heyanle.musicballpro.models.notification.data.NData;
import cn.heyanle.musicballpro.models.notification.data.NotificationData;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Followable;
import dalvik.system.DexFile;

/**
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class NotificationModel {

    private Context context;

    //private Followable<MusicInfo> followable;

    private Map<String,NotificationData> dataMap = new HashMap<>();

    public void init(){

        HeLog.i("Notification","init",this);

        try{

            DexFile df = new DexFile(context.getPackageCodePath());
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = enumeration.nextElement();
                HeLog.i(className,this);
                if (className.contains("cn.heyanle.musicballpro.models.notification.data")
                        && !className.contains("$")) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    Class c = Class.forName(className);
                    if (c.isAnnotationPresent(NData.class)){//是否含有注解
                        try {//嵌套Try 防止一个组件错误导致全部无法加载
                            HeLog.i(className,this);
                            NotificationData notificationData = (NotificationData) c.newInstance();//实例化
                            notificationData.init(context);//初始化
                            dataMap.put(notificationData.getPackageName(), notificationData);//添加
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String nowTag;
    private int nowId;

    public void onNotificationPosted(StatusBarNotification sbn) {

        String packageName = sbn.getPackageName();
        if (dataMap.containsKey(packageName) && ! WhiteModel.getInstance().isMusicWhite(packageName)){

            MusicInfo info = dataMap.get(packageName).getMusicInfo(sbn);



            if (info != null){

                HeLog.i("getMusic",info.toString(),this);

                nowTag = sbn.getTag();//保存有效通知的TAG和id
                nowId = sbn.getId();
                MusicModel.getInstance().getFollowable().requestSend(info);

            }

        }

    }


    public void onNotificationRemoved(StatusBarNotification sbn) {

        String tag = sbn.getTag();
        int id = sbn.getId();
        if (tag != null) {
            if (tag.equals(nowTag) && id == nowId) {

                MusicModel.getInstance().getFollowable().requestSend(new MusicInfo());

            }
        }else if(nowTag == null && id == nowId){
            MusicModel.getInstance().getFollowable().requestSend(new MusicInfo());
        }

    }

    public boolean isHaveNotificationData(String packageName){
        return dataMap.containsKey(packageName);
    }

    public NotificationModel(Context context){
        this.context = context;
    }

}
