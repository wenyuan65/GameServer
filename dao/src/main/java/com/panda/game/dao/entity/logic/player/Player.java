package com.panda.game.dao.entity.logic.player;

import com.panda.game.core.jdbc.annotation.TableField;
import com.panda.game.core.jdbc.base.BaseEntity;
import com.panda.game.core.jdbc.base.Option;

import java.util.Date;

@TableField
public class Player extends BaseEntity {
    /** 角色id */
    private long playerId;
    /** 角色名 */
    private String playerName;
    /** 等级 */
    private int lv;
    /** 经验 */
    private int exp;
    /** 性别，1男2女 */
    private int male;
    /** 头像 */
    private String pic;
    /** userId */
    private long userId;
    /** 玩家联运标识 */
    private String yxUserId;
    /** 联运 */
    private String yx;
    /** 渠道 */
    private String channelId;
    /** 创建日期 */
    private Date createTime;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        if (this.playerId != playerId) {
            this.playerId = playerId;
            setOp(Option.Update);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        if (playerName != null && !playerName.equals(this.playerName)) {
            this.playerName = playerName;
            setOp(Option.Update);
        }
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        if (this.lv != lv) {
            this.lv = lv;
            setOp(Option.Update);
        }
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        if (this.exp != exp) {
            this.exp = exp;
            setOp(Option.Update);
        }
    }

    public int getMale() {
        return male;
    }

    public void setMale(int male) {
        if (this.male != male) {
            this.male = male;
            setOp(Option.Update);
        }
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        if (pic != null && !pic.equals(this.pic)) {
            this.pic = pic;
            setOp(Option.Update);
        }
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        if (this.userId != userId) {
            this.userId = userId;
            setOp(Option.Update);
        }
    }

    public String getYxUserId() {
        return yxUserId;
    }

    public void setYxUserId(String yxUserId) {
        if (yxUserId != null && !yxUserId.equals(this.yxUserId)) {
            this.yxUserId = yxUserId;
            setOp(Option.Update);
        }
    }

    public String getYx() {
        return yx;
    }

    public void setYx(String yx) {
        if (yx != null && !yx.equals(this.yx)) {
            this.yx = yx;
            setOp(Option.Update);
        }
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        if (channelId != null && !channelId.equals(this.channelId)) {
            this.channelId = channelId;
            setOp(Option.Update);
        }
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        if (createTime != null && !createTime.equals(this.createTime)) {
            this.createTime = createTime;
            setOp(Option.Update);
        }
    }
}
