package com.dgg.baselibrary.db;

import android.content.Context;

import com.dgg.baselibrary.db.been.Topic;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;

public class TopicDao {
    private Context context;
    private Dao<Topic, Integer> topicvDaoOpe;
    private DatabaseHelper helper;

    public TopicDao(Context context) {
        this.context = context;
        try {
            helper = DatabaseHelper.getHelper(context);
            topicvDaoOpe = helper.getDao(Topic.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isTableExists() throws SQLException {
        return topicvDaoOpe.idExists(1);
    }

    /**
     * 增加一个用户
     *
     * @param db
     */
    public void add(Topic db) {
        try {
            topicvDaoOpe.create(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }//...other operations

    public ArrayList<Topic> queryAllData() {
        ArrayList<Topic> data = new ArrayList<>();
        try {
            data.addAll(topicvDaoOpe.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void refreshData(Topic db) {
        try {
            topicvDaoOpe.update(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Topic queryById(Integer db) {
        Topic topic = null;
        try {
            topic = topicvDaoOpe.queryForId(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topic;
    }


//    /*获取收藏的数据*/
//    public ArrayList<Topic> getCollectionData() {
//        ArrayList<Topic> data = new ArrayList<>();
//        try {
//            for (Topic ele : topicvDaoOpe.queryForAll()) {
//                if (ele.isCollection)
//                    data.add(ele);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return data;
//    }

//    /*获取 【未做习题】数据*/
//    public ArrayList<Topic> getNoCompleteData() {
//        ArrayList<Topic> data = new ArrayList<>();
//        try {
//            for (Topic ele : topicvDaoOpe.queryForAll()) {
//                if (ele.complete == 0)
//                    data.add(ele);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return data;
//    }

//    /*获取 【【已经】做习题】数据*/
//    public ArrayList<Topic> getCompleteData() {
//        ArrayList<Topic> data = new ArrayList<>();
//        try {
//            for (Topic ele : topicvDaoOpe.queryForAll()) {
//                if (ele.complete != 0)
//                    data.add(ele);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return data;
//    }

//    /*获取 【错题】数据*/
//    public ArrayList<Topic> getErrorData() {
//        ArrayList<Topic> data = new ArrayList<>();
//        try {
//            for (Topic ele : topicvDaoOpe.queryForAll()) {
//                if (ele.completeError != 0)
//                    data.add(ele);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return data;
//    }

//    /*获取 【答题正确】数据*/
//    public ArrayList<Topic> getCorrectData() {
//        ArrayList<Topic> data = new ArrayList<>();
//        try {
//            for (Topic ele : topicvDaoOpe.queryForAll()) {
//                if (ele.completeCorrect != 0)
//                    data.add(ele);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return data;
//    }

    /*获取题在总数*/
    public long getTopicCount() {
        long count = 0;
        try {
            count = topicvDaoOpe.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

}