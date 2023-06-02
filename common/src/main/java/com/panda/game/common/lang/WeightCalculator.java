package com.panda.game.common.lang;

@FunctionalInterface
public interface WeightCalculator<T> {

    int getWeight(T t);

}
