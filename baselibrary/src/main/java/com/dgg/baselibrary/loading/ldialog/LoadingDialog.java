package com.dgg.baselibrary.loading.ldialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dgg.baselibrary.R;
import com.dgg.baselibrary.tools.UIUtils;

/**
 * Created by qiqi on 17/7/31.
 */

public class LoadingDialog extends AlertDialog {
    private TextView message;

    public LoadingDialog(Context context) {
        this(context, 0);
    }

    public LoadingDialog(Context context, @StyleRes int themeResId) {
        this(context, true, null);

    }

    public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }


    public void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_loading, null, false);
        message = (TextView) view.findViewById(R.id.message);
        this.setView(view);
//        WindowManager.LayoutParams params = this.getWindow().getAttributes();
//        params.width = 200;
//        params.height = 200;
//        this.getWindow().setAttributes(params);
        this.show();
        this.getWindow().setLayout(UIUtils.pix2dip(1200), UIUtils.pix2dip(1200));
    }

    public void setCustomMessage(String msg) {
        message.setText(msg);
    }
}
