package com.dgg.baselibrary.db.been;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by qiqi on 17/7/6.
 * 题的选项
 */

public class Options {
    public String id;//答案序号
    public String text;//答案描述
    public int optionType;//答案类型 0文字 1图片
    public String imgUrl;

}
