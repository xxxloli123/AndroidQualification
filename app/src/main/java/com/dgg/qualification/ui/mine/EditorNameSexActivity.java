package com.dgg.qualification.ui.mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.jingewenku.abrahamcaijin.commonutil.AppKeyBoardMgr;
import com.jingewenku.abrahamcaijin.commonutil.ToolAnimation;

public class EditorNameSexActivity extends KtBaseActivity implements View.OnClickListener {
    public static final int editorName = 12;
    public static final int editorSex = 13;
    public static final String type = "type";
    private LinearLayout typeName, typeSex, sexMen, sexMs;
    private EditText nameInput;
    private TextView completeAction;
    private CheckBox cbMen, cbMs;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_editor_name_sex;
    }

    @Override
    protected void initData() {
        typeName = (LinearLayout) findViewById(R.id.typeName);
        typeSex = (LinearLayout) findViewById(R.id.typeSex);
        sexMen = (LinearLayout) findViewById(R.id.sexMen);
        sexMen.setOnClickListener(this);
        CommonUtils.setOnTouch(sexMen);
        sexMs = (LinearLayout) findViewById(R.id.sexMs);
        sexMs.setOnClickListener(this);
        CommonUtils.setOnTouch(sexMs);
        completeAction = (TextView) findViewById(R.id.completeAction);
        completeAction.setOnClickListener(this);
        CommonUtils.setOnTouch(completeAction);
        nameInput = (EditText) findViewById(R.id.nameInput);
        cbMen = (CheckBox) findViewById(R.id.cbMen);
        cbMs = (CheckBox) findViewById(R.id.cbMs);
        cbMen.setOnClickListener(this);
        cbMs.setOnClickListener(this);
        int intentType = getIntent().getIntExtra(type, 0);
        if (intentType == editorName) {
            initTitle("修改姓名");
            typeName.setVisibility(View.VISIBLE);
            AppKeyBoardMgr.openKeybord(nameInput, this);
            nameInput.setText(getIntent().getStringExtra("name"));
        } else if (intentType == editorSex) {
            initTitle("修改性别");
            typeSex.setVisibility(View.VISIBLE);
            String sex = getIntent().getStringExtra("sex");
            if ("男".equals(sex)) {
                cbMen.setChecked(true);
            } else if ("女".equals(sex)) {
                cbMs.setChecked(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sexMen:
                sexAction("男");
                break;
            case R.id.sexMs:
                sexAction("女");
                break;
            case R.id.completeAction:
                String name = nameInput.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast(this, "请输入姓名");
                    return;
                }
                Intent intentName = new Intent();
                intentName.putExtra("name", name);
                setResult(RESULT_OK, intentName);
                finish();
                break;
            case R.id.cbMen:
                sexAction("男");
                break;
            case R.id.cbMs:
                sexAction("女");
                sexMs.performClick();
                break;
        }
    }

    private void sexAction(String sex) {
        Intent intentMen = new Intent();
        intentMen.putExtra("sex", sex);
        setResult(RESULT_OK, intentMen);
        finish();
    }

    @Override
    public void reLoadingData() {

    }
}
