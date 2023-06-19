package com.panda.game.dao.entity.logic.common;

import com.panda.game.core.jdbc.annotation.TableField;
import com.panda.game.core.jdbc.base.BaseEntity;
import com.panda.game.core.jdbc.base.Option;

import java.util.Date;

@TableField
public class GlobalValue extends BaseEntity {
    
    private int id;
    private String value;
    private long param1;
    private long param2;
    private long param3;
    private Date time;
    private String intro;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != id) {
            this.id = id;
            setOp(Option.Update);
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null && !value.equals(this.value)) {
            this.value = value;
            setOp(Option.Update);
        }
    }

    public long getParam1() {
        return param1;
    }

    public void setParam1(long param1) {
        if (this.param1 != param1) {
            this.param1 = param1;
            setOp(Option.Update);
        }
    }

    public long getParam2() {
        return param2;
    }

    public void setParam2(long param2) {
        if (this.param2 != param2) {
            this.param2 = param2;
            setOp(Option.Update);
        }
    }

    public long getParam3() {
        return param3;
    }

    public void setParam3(long param3) {
        if (this.param3 != param3) {
            this.param3 = param3;
            setOp(Option.Update);
        }
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        if (time != null && !time.equals(this.time)) {
            this.time = time;
            setOp(Option.Update);
        }
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        if (intro != null && !intro.equals(this.intro)) {
            this.intro = intro;
            setOp(Option.Update);
        }
    }
}
