package cn.heyanle.musicballpro.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * Switch & RelativeLayout
 * 双控件绑定 Switch状态监听 监听是否代码设置
 * Created by HeYanLe
 * 2019/1/26 0026
 * https://github.com/heyanLE
 */
public class SwitchRelative extends RelativeLayout {

    /**
     * Switch状态监听
     */
    public interface OnSwitchChangeListener{

        /**
         * Switch状态监听
         * @param isPress           是否使用者触发
         * @param isChecked         触发后状态
         */
        void onSwitchChange(SwitchRelative view,boolean isPress,boolean isChecked);
    }

    private Switch aSwitch;
    private OnSwitchChangeListener listener = null;

    private boolean isPress = true;

    /**
     * 设置Switch状态 （触发的监听isPress参数为false）
     * @param isChecked             Switch状态
     */
    public void setChecked(boolean isChecked){

        isPress = false;
        aSwitch.setChecked(isChecked);
        isPress = true;

    }

    /**
     * 设置Switch状态改变监听器
     * @param listener          监听器
     */
    public void setOnSwitchChangeListener(OnSwitchChangeListener listener){
        this.listener = listener;
    }

    private void init(Context context){
        aSwitch = new Switch(context);

        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        params.addRule(CENTER_VERTICAL);//竖直居中
        params.addRule(ALIGN_PARENT_END);//水平居右

        /*
        20dp -> xx px
         */
        int marginEnd = (int)(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                20,
                context.getResources().getDisplayMetrics())+0.5f);

        params.setMarginEnd(marginEnd);//右外距

        /*
        点击RelativeLayout后 模拟点击Switch
         */
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aSwitch.performClick();
            }
        });

        /*
        当Switch状态改变 触发监听
         */
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null){
                    listener.onSwitchChange(SwitchRelative.this,isPress,isChecked);
                }
            }
        });

        addView(aSwitch,params);
    }

    public SwitchRelative(Context context) {
        super(context);
        init(context);
    }

    public SwitchRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwitchRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
