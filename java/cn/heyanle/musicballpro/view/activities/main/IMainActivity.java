package cn.heyanle.musicballpro.view.activities.main;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

/**
 * MainActivity 接口
 * @see MainActivity
 * @see cn.heyanle.musicballpro.presenters.MainPresenter
 * Created by HeYanLe
 * 2019/1/26 0026
 * https://github.com/heyanLE
 */
public interface IMainActivity {

    /**
     * 通过Id找到View
     * @param viewId            ViewId
     * @return                  View
     */
    View findView(@IdRes int viewId);

    /**
     * 获得上下文对象
     * @return          上下文对象
     */
    Context getContext();


}
