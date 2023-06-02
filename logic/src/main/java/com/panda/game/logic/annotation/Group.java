package com.panda.game.logic.annotation;

import com.panda.game.logic.common.ModuleGroups;

import java.lang.annotation.*;


/**
 * 支持service分组加载
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Group {

    ModuleGroups value();

}
