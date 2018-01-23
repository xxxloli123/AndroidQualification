package com.dgg.baselibrary.widget.sortlist;

public class SortModel {

    public String name;   //显示的数据
    public String sortLetters;  //显示数据拼音的首字母
    public int id;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}
