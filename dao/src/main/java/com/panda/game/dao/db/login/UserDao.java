package com.panda.game.dao.db.login;

import com.panda.game.core.jdbc.dao.BaseDao;
import com.panda.game.dao.entity.login.User;

import java.util.List;

public class UserDao extends BaseDao<User> {

    public User getUserByUserName(String userName) {
        String sql = "select * from user where user_name=" + userName;
        List<User> list = getAllBySql(sql);
        if (list == null || list.size() == 0) {
            return null;
        }

        return list.get(0);
    }

}
