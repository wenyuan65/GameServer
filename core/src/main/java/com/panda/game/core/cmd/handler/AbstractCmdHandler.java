package com.panda.game.core.cmd.handler;

import com.panda.game.core.annotation.Bind;
import com.panda.game.core.cmd.*;
import com.panda.game.core.cmd.bind.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public abstract class AbstractCmdHandler<T> implements CmdHandler<T> {

    protected Class<?> action;
    protected Method method;
    protected Object instance;

    protected Binder binder;

    public AbstractCmdHandler() {}

    /**
     * 初始化
     * @param clazz
     * @param method
     * @param bind
     */
    public void init(Class<?> clazz, Method method, Bind bind) {
        this.action = clazz;
        this.method = method;

        initInjectors(method);
        initInstance();
        initBinder(bind);
    }

    /**
     * 生成参数的注入类
     * @param method
     */
    protected abstract void initInjectors(Method method);

    /**
     * 绑定实例
     */
    protected void initBinder(Bind bind) {
        if (bind == null) {
            binder = new PlayerIdBinder();
            return;
        }

        switch(bind.bindType()) {
            case Bind_PlayerId:
                binder = new PlayerIdBinder();
                break;
            case Bind_Fields:
                binder = parseFieldsBinder(action, method, bind);
                break;
            case Bind_Cmd:
                binder = new CmdBinder();
                break;
            case Bind_Group:
                binder = new GroupBinder(bind.group());
                break;
            case Random:
                binder = new RandomBinder();
                break;
            default: throw new RuntimeException("unsupported bind type");
        }
    }

    /**
     * 生成实例对象
     */
    protected void initInstance() {
        try {
            Constructor<?> constructor = action.getConstructor();
            this.instance = constructor.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    protected Binder parseFieldsBinder(Class<?> clazz, Method method, Bind bind) {
        int index = bind.index();
        String[] bindFieldNames = bind.bindFields();
        Parameter[] parameters = method.getParameters();
        if (index < 0 || index >= parameters.length) {
            throw new RuntimeException(clazz.getName() + "." + method.getName() + "()方法参数数量与index值不匹配,实际数量" + parameters.length + ", index:"+ index);
        }

        Parameter parameter = parameters[index];
        Class<?> parameterType = parameter.getType();

        Method[] bindFieldsMethod = new Method[bindFieldNames.length];
        for (int i = 0; i < bindFieldNames.length; i++) {
            String fieldName = bindFieldNames[i];
            try {
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method getMethod = parameterType.getDeclaredMethod(getMethodName);

                getMethod.setAccessible(true);
                bindFieldsMethod[i] = getMethod;
            } catch (Throwable e) {
                throw new RuntimeException(parameterType.getName() + "类中找不到字段" + fieldName);
            }
        }

        return new FieldsBinder(index, bindFieldsMethod);
    }

    public Class<?> getAction() {
        return action;
    }

    public Method getMethod() {
        return method;
    }
}
