package com.panda.game.dao.entity.login;

import com.panda.game.core.jdbc.annotation.TableField;
import com.panda.game.core.jdbc.base.BaseEntity;
import com.panda.game.core.jdbc.base.Option;

import java.util.Date;

@TableField
public class User  extends BaseEntity {

    // 玩家id
    private long userId;
    // 名称
    private String userName;
    // 密码
    private String password;
    // 玩家联运标识
    private String yxUserId;
    // 联运
    private String yx;
    // 渠道
    private String channelId;
    // 创建日期
    private Date createTime;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        if (this.userId != userId) {
            this.userId = userId;
            setOp(Option.Update);
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName != null && !userName.equals(this.userName)) {
            this.userName = userName;
            setOp(Option.Update);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && !password.equals(this.password)) {
            this.password = password;
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
