package com.dgg.baselibrary.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "tb_user")
public class User implements Serializable {

    @DatabaseField(id = true)
    public int id;
    @DatabaseField
    public String name;
    @DatabaseField
    public long userID;
    @DatabaseField
    public String TopicData;//【错题】ID集合
    @DatabaseField
    public int areaId;//地区ID
    @DatabaseField
    public String areaName;//地区名字
    @DatabaseField
    public int questionId;//题库ID
    @DatabaseField
    public String questionName;//题库名字
    @DatabaseField
    public String phone;//电话号码
    @DatabaseField
    public String sex;//性别
    @DatabaseField
    public String head;//头像
    @DatabaseField
    public String uid;//用户唯一编码id
    @DatabaseField
    public int currentIndex;//退出练习当前的题号
    @DatabaseField
    public String insistDay;//坚持天数
    @DatabaseField
    public boolean isLogin;//是否登录
    @DatabaseField
    public String chaptersNumber;//完成章节数

//    @DatabaseField
//    public String reservedString2;//
//    @DatabaseField
//    public String reservedString3;//
//    @DatabaseField
//    public String reservedString4;//
//    @DatabaseField
//    public int reservedInt1;//预留字段
//    @DatabaseField
//    public long reservedLong1;//
//    @DatabaseField
//    public long reservedLong2;//
//    @DatabaseField
//    public int reservedInt2;//

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User [id=" + id + "]";
    }


}