package com.dgg.baselibrary.db.been;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by qiqi on 17/7/24.
 */

public class TopicAction implements Serializable {

    public int id;//题的ID
    public long complete;//完成次数
    public long completeError;//完成错误次数
    public long completeCorrect;//完成正确次数
    public boolean isCollection;//试题是否收藏
    public boolean isCorrect;//试题是否 最近一次做对 默认是错的

    public long typeCode;//专业分类ID
    public String typeCodeName;//专业分类名称
}
