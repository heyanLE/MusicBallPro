package cn.heyanle.musicballpro.view.activities.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by HeYanLe
 * 2019/2/9 0009
 * https://github.com/heyanLE
 */
public class MyRelativeLayout extends RelativeLayout {



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        /*
        如果不是转屏（宽度不变）
         */
        if (oldw == w){

            int k = Math.abs(h - oldh);
            if (k>=100){
                MainActivity.keyboardSize = k;
            }

        }

    }

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
