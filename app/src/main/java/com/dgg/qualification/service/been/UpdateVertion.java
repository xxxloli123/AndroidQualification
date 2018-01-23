package com.dgg.qualification.service.been;

/**
 * Created by qiqi on 17/7/31.
 */

public class UpdateVertion {

    public String updateLog;
    public String name;
    public int no;
    public String url;
    public String isUpdate;//1.强制更新 2.不 强制更新

    //    public String name;
    /*是否强制更新*/
    public boolean isForceUpdate() {
        boolean isforceupdate = false;
        if ("1".equals(isUpdate)) {
            isforceupdate = true;
        } else if ("2".equals(isUpdate)) {
            isforceupdate = false;
        }
        return isforceupdate;
    }
}
