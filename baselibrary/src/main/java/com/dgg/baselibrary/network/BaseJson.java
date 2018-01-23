package com.dgg.baselibrary.network;


import java.io.Serializable;

/**
 * Created by HSAEE on 2016/11/9.
 */

public class BaseJson<T> implements Serializable {
    public int code;
    public String msg;
    public T data;


    /**
     * 请求是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        if (code == 0) {
            return true;
        } else {
            return false;
        }
    }
}
