package cn.heyanle.musicballpro.network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 版本更新
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class CheckUpdate {

    /**
     * 获取结果
     */
    public interface CallBack{

        /**
         * 结果
         * @param version       当前最新版本
         * @param description   当前最新版本描述
         */
        void onCallBack (String version,String description);

    }

    public static void get(final CallBack callBack){

        new Thread(new Runnable() {
            @Override
            public void run() {
                String version = "";
                String description = "";

                try{
                    Document doc = Jsoup.connect("https://www.coolapk.com/apk/cn.heyanle.musicballpro")
                            .timeout(3000)
                            .post();
                    version = doc.getElementsByClass("list_app_info").get(0).text();
                    description = doc.getElementsByClass("apk_left_title_info").get(0).text();
                }catch (Exception e){
                    e.printStackTrace();
                }
                callBack.onCallBack(version,description);
            }
        }).start();

    }

}
