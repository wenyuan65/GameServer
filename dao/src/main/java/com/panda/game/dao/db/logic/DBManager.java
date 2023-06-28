package com.panda.game.dao.db.logic;

import com.panda.game.dao.db.logic.common.GlobalValueDao;
import com.panda.game.dao.db.logic.common.PlayerDao;

public class DBManager {

    private static GlobalValueDao globalValueDao = new GlobalValueDao();

    public static GlobalValueDao getGlobalValueDao() {
        return globalValueDao;
    }

    private static PlayerDao playerDao = new PlayerDao();

    public static PlayerDao getPlayerDao() {
        return playerDao;
    }
}
