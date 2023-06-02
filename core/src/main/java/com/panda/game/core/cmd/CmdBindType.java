package com.panda.game.core.cmd;

public enum CmdBindType {
    // 绑定playerId
    Bind_PlayerId,
    // 绑定命令id
    Bind_Cmd,
    // 根据index和bind指定绑定字段
    Bind_Fields,
    // 绑定指定group
    Bind_Group,
    // 随机
    Random,
}
