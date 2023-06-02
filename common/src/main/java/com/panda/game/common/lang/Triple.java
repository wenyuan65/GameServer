package com.panda.game.common.lang;

import java.util.Objects;

public class Triple<L, M, R> {

    public L t1;
    public M t2;
    public R t3;

    public Triple() {
    }

    public Triple(L t1, M t2, R t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public L getT1() {
        return t1;
    }

    public void setT1(L t1) {
        this.t1 = t1;
    }

    public M getT2() {
        return t2;
    }

    public void setT2(M t2) {
        this.t2 = t2;
    }

    public R getT3() {
        return t3;
    }

    public void setT3(R t3) {
        this.t3 = t3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(getT1(), triple.getT1()) && Objects.equals(getT2(), triple.getT2()) && Objects.equals(getT3(), triple.getT3());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getT1(), getT2(), getT3());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(t1).append(", ").append(t2).append(", ").append(t3).append(']');
        return sb.toString();
    }
}
