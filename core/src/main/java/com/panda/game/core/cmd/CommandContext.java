package com.panda.game.core.cmd;

import com.panda.game.proto.PacketPb;

import java.lang.reflect.Method;

public class CommandContext {

    private Class<?> action;
    private Method method;
    private Object instance;
    private PacketPb.Pkg pkg;
    private Object[] params;

    // 执行的线程id
    private int index;
    // 创建时间
    private long createdTime = System.currentTimeMillis();
    private long beginTime;
    private long endTime;

    public Class<?> getAction() {
        return action;
    }

    public void setAction(Class<?> action) {
        this.action = action;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public PacketPb.Pkg getPkg() {
        return pkg;
    }

    public void setPkg(PacketPb.Pkg pkg) {
        this.pkg = pkg;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
