package cn.heyanle.musicballpro.utils.rx;

/**
 * 跟随者
 *
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
public interface Follower <T> {

    void onReceive(T what);

}
