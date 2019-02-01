package cn.heyanle.musicballpro.model.notification;

import android.content.Context;
import android.service.notification.StatusBarNotification;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.heyanle.musicballpro.bean.MusicInfo;
import cn.heyanle.musicballpro.model.notification.data.NData;
import cn.heyanle.musicballpro.model.notification.data.NotificationData;
import cn.heyanle.musicballpro.utils.HeLog;
import cn.heyanle.musicballpro.utils.rx.Followable;
import dalvik.system.DexFile;

/**
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
public class NotificationModel {

    private Context context;

    private Followable<MusicInfo> followable;

    private Map<String,NotificationData> dataMap = new HashMap<>();

    private void init(){

        try{

            DexFile df = new DexFile(context.getPackageCodePath());
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = enumeration.nextElement();

                if (className.contains("cn.heyanle.musicballpro.model.notification.data")
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
        if (dataMap.containsKey(packageName)){

            MusicInfo info = dataMap.get(packageName).getMusicInfo(sbn);



            if (info != null){

                HeLog.i("getMusic",info.toString(),this);

                nowTag = sbn.getTag();//保存有效通知的TAG和id
                nowId = sbn.getId();
                followable.requestSend(info);

            }

        }

    }


    public void onNotificationRemoved(StatusBarNotification sbn) {

        String tag = sbn.getTag();
        int id = sbn.getId();
        if (tag != null) {
            if (tag.equals(nowTag) && id == nowId) {

                followable.requestSend(null);

            }
        }else if(id == nowId){
            followable.requestSend(null);
        }

    }

    public NotificationModel (Context context, Followable<MusicInfo> f){

        this.context = context;
        followable = f;

        init();

    }

}
