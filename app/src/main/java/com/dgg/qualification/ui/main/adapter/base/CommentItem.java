package com.dgg.qualification.ui.main.adapter.base;


import static com.dgg.qualification.ui.main.adapter.base.ExpandableRecyclerAdapter.TYPE_HEADER;

/**
 * Created by Lzx on 2016/9/30.
 */

public class CommentItem extends ExpandableRecyclerAdapter.ListItem {
    public static final int TYPE_PERSON = 1001;
    public String Text;

    public CommentItem(String group) {
        super(TYPE_HEADER);
        Text = group;
    }

    public CommentItem(String first, String last) {
        super(TYPE_PERSON);
        Text = first + " " + last;
    }
}
