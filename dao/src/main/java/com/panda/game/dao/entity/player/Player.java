package com.panda.game.dao.entity.player;

import com.panda.game.core.jdbc.annotation.TableField;

@TableField
public class Player {
    /** 角色id */
    private long playerId;
    /** 角色名 */
    private String name;
    /** 等级 */
    private int lv;
    /** 经验 */
    private int exp;
    /** 头像 */
    private int head;
    /** userId */
    private String userId;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
