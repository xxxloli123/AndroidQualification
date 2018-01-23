package com.dgg.baselibrary.db.been;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "tb_topic")
public class Topic implements Serializable {
//    @DatabaseField(generatedId = true)
//    public int dbId;//题的ID

    @DatabaseField(id = true)
    public int id;//题的ID
    @DatabaseField
    public String title;
    @DatabaseField
    public long categoryCode;//题型分类ID  3【单选】4【多选】
    @DatabaseField
    public String categoryCodeName;//题型分类名称
    @DatabaseField
    public long typeCode;//专业分类ID
    @DatabaseField
    public String typeCodeName;//专业分类名称
    @DatabaseField
    public String successAnswer;//正确答案
    @DatabaseField
    public String exaplain;//解释
    @DatabaseField
    public long areaId;//区划ID
    @DatabaseField
    public String areaName;//区划名称
    @DatabaseField
    public String createTime;
    @DatabaseField
    public String optionList;//题的答案 json 字符串
    @DatabaseField
    public String imgUrl;

//    @DatabaseField
//    public boolean isBooleanT;//试题是否收藏
//    @DatabaseField
//    public String reservedString1T;//预留字段
//    @DatabaseField
//    public String reservedString2T;//
//    @DatabaseField
//    public String reservedString3T;//
//    @DatabaseField
//    public String reservedString4T;//
//    @DatabaseField
//    public int reservedInt1T;//预留字段
//    @DatabaseField
//    public long reservedLong1T;//
//    @DatabaseField
//    public long reservedLong2T;//
//    @DatabaseField
//    public int reservedInt2T;//


    public int rightAnswer;  //存储正确答案
    public int errorAnswer;  //存储错误答案
    public boolean chooseResult;//存储最最终用户选择是否正确
    public boolean finishAnswer;//是否完成了答题
    public String myAnswer;//是否完成了答题

    //多选
    public List<Integer> rightList = new ArrayList<>();//存储正确的答案
    public List<Integer> resultList = new ArrayList<>();//存储用户答题的答案

}