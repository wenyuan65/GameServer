package com.panda.game.core.cmd.bind;

import com.panda.game.core.cmd.Binder;

import java.lang.reflect.Method;
import java.util.Objects;

public class FieldsBinder implements Binder {

    private int index;
    private Method[] methods;

    public FieldsBinder(int index, Method[] methods) {
        this.index = index;
        this.methods = methods;
    }

    @Override
    public int calcBindIndex(long playerId, int cmd, Object[] params) {
        Object param = params[index];

        Object[] bindValues = new Object[methods.length];
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            try {
                bindValues[i] = method.invoke(param);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        return Objects.hash(bindValues);
    }
}
