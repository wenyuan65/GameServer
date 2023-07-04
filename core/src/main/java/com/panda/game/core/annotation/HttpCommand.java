package com.panda.game.core.annotation;

import com.panda.game.core.cmd.CmdBindType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface HttpCommand {

	String value();

}
