package com.dgg.baselibrary.db;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;

import com.dgg.baselibrary.tools.LogUtils;
import com.j256.ormlite.dao.Dao;

public class UserDao {
    private Context context;
    private Dao<User, Integer> userDaoOpe;
    private DatabaseHelper helper;

    public UserDao(Context context) {
        this.context = context;
        try {
            helper = DatabaseHelper.getHelper(context);
            userDaoOpe = helper.getDao(User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个用户
     *
     * @param user
     */
    public void add(User user) {
        try {
            userDaoOpe.create(user);
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.d("创建用户表" + e.getMessage());
        }

    }

    public void cleanInfo() {
        userDaoOpe.clearObjectCache();
    }

    public User getUser() {
        User user = null;
        try {

            user = userDaoOpe.queryForAll().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public ArrayList<User> getUserList() {
        ArrayList<User> user = null;
        try {
            user = (ArrayList<User>) userDaoOpe.queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public void refreshUser(User user) {
        try {
            userDaoOpe.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*获取题在总数*/
    public long getUserCount() {
        long count = 0;
        try {
            count = userDaoOpe.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}