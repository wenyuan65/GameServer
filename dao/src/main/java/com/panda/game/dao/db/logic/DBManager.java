package com.panda.game.dao.db.logic;

import com.panda.game.dao.db.logic.common.GlobalValueDao;

public class DBManager {

    private static GlobalValueDao globalValueDao = new GlobalValueDao();

    public static GlobalValueDao getGlobalValueDao() {
        return globalValueDao;
    }

}
