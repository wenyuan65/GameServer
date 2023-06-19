package com.panda.game.core.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface Crontab {

    String value();

    String jobName() default "";

    int jobId() default 0;

}
