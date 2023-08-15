package com.panda.game.logic.drop;

public class RewardManager {

    private static final RewardManager instance = new RewardManager();

    private RewardManager() {
    }

    public static RewardManager getInstance() {
        return instance;
    }



}
