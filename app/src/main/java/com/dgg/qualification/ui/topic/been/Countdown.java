package com.dgg.qualification.ui.topic.been;

import java.io.Serializable;

/**
 * Created by qiqi on 17/7/17.
 */

public class Countdown implements Serializable {
    public int id;
    public int areaId;
    public String areaName;
    public String testTime;//考试时间
    public String testName;//考试名称
    public String examComeTime;//倒计时
    public String type;//1精确 2.预计
}
