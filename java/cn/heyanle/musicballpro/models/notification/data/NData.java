package cn.heyanle.musicballpro.models.notification.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data注解 所有分析通知获取音乐信息的类都带有此注解
 * Created by HeYanLe
 * 2019/1/31 0031
 * https://github.com/heyanLE
 */
@Target(ElementType.TYPE)//类注解
@Retention(RetentionPolicy.RUNTIME)//运行时注解
public @interface NData {
}
