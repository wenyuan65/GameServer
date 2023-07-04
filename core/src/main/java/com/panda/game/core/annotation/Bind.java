package com.panda.game.core.annotation;

import com.panda.game.core.cmd.CmdBindType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Bind {

    /**
     * 绑定类型, 通过指定命令绑定在哪个线程中执行，实现相同命令、或同一个玩家/公会的命令在同一个线程中执行，以串行的方式执行存在竞争的接口，从而避免资源竞争的问题
     * @return
     */
    CmdBindType bindType() default CmdBindType.Bind_PlayerId;

    /**
     * 获取方法的第几个参数，去绑定，默认选第一个参数
     * @return
     */
    int index() default 0;

    /**
     * 绑定方法的第index个参数的相关字段，没添加注解的时候默认注解playerId
     * @return
     */
    String[] bindFields() default { "playerId" };

    /**
     * 绑定到指定下标的线程，当bindType为Bind_Group时生效<br/>
     * 比如Thread[] pools = new Thread[10], 这里指定使用pools[0]去执行方法
     * @return
     */
    int group() default 0;

}
