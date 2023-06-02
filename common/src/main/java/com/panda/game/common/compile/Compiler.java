package com.panda.game.common.compile;

public interface Compiler {

    Class<?> compile(String name, String sourceCode) throws Throwable;

}
