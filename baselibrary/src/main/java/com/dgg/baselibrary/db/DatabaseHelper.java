package com.dgg.baselibrary.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dgg.baselibrary.db.been.Topic;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


/**/
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TABLE_NAME = "sqlite-test.db";
    private static final int DB_VERSION = 5;
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Article.class);
            TableUtils.createTable(connectionSource, Topic.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Article.class, true);
            TableUtils.dropTable(connectionSource, Topic.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            context = context.getApplicationContext();
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }
        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    public void uodateTopic() {
        deleteTopic();
        creatTopic();
    }

    public void deleteTopic() {
        try {
            TableUtils.dropTable(getConnectionSource(), Topic.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void creatTopic() {
        try {
            TableUtils.createTable(getConnectionSource(), Topic.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();

        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }

}
