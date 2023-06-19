package com.panda.game.dao.db.login;

public class LoginDBManager {

    private static UserDao userDao = new UserDao();

    public static UserDao getUserDao() {
        return userDao;
    }
}
