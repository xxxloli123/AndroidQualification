package com.dgg.qualification.ui.main.server;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by qiqi on 17/7/17.
 */

public class HomePage implements Serializable {

    public ArrayList<HomePageItem> bannerList = new ArrayList();
    public ArrayList<HomePageItem> icoList = new ArrayList();
    public ArrayList<HomePageItem> contentList = new ArrayList();
}
