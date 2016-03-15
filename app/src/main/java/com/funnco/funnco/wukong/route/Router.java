package com.funnco.funnco.wukong.route;


import com.funnco.funnco.wukong.base.DefaultViewHolderCreator;
import com.funnco.funnco.wukong.viewholder.ViewHolder;
import com.funnco.funnco.wukong.viewholder.ViewHolderCreator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zijunlzj on 14/11/25.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Router {

    /**
     *目标ViewHolder,用于关联当前修饰的Domain对象,按照
     * @return
     */
    Class<? extends ViewHolder>[] value();

    /**
     * ViewHolder 关联对象的生成器
     */
   public Class<? extends ViewHolderCreator> generate() default DefaultViewHolderCreator.class;


}
