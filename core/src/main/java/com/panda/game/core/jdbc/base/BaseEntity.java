package com.panda.game.core.jdbc.base;

public class BaseEntity {

    private Option op = Option.None;

    public Option getOp() {
        return op;
    }

    public void setOp(Option op) {
        this.op = op;
    }

    public boolean isAddOption() {
        return this.op == Option.Insert;
    }

    public boolean isUpdateOption() {
        return this.op == Option.Update;
    }

    public boolean isAddOrUpdateOption() {
        return this.op == Option.Insert || this.op == Option.Update;
    }

    public boolean isDeleteOption() {
        return this.op == Option.Delete;
    }

    public void clearOption() {
        this.op = Option.None;
    }

}
