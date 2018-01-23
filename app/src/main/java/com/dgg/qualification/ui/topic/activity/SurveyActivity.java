package com.dgg.qualification.ui.topic.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dgg.baselibrary.loading.ldialog.LoadingDialog;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.recycle.CommonAdapter;
import com.dgg.baselibrary.recycle.ViewHolder;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.topic.server.TopicServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiqi on 17/8/4.
 */

public class SurveyActivity extends KtBaseActivity {

    private ArrayList<ItemData> datas;
    private RecyclerView list;
    private TextView addAction;
    private HashMap<String, Object> map;
    private LinearLayoutManager mLayoutManager;
    private ListAdapter adapter;
    private LoadingDialog ld;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_survey;
    }

    @Override
    protected void initData() {
        initTitle("个人培训意愿调查表");
        creatData();
        list = (RecyclerView) findViewById(R.id.list);
        addAction = (TextView) findViewById(R.id.addAction);
        mLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLayoutManager);
        adapter = new ListAdapter(this, R.layout.item_survey, datas);
        list.setAdapter(adapter);
        addAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < datas.size(); i++) {
                    ItemData ele = datas.get(i);
                    if (!ele.tag.equals("suggest") && map.get(ele.tag) == null) {
                        ToastUtils.showToast(SurveyActivity.this, "请选择" + ele.title);
                        /**准确定位到指定位置，并且将指定位置的item置顶，
                         若直接调用scrollToPosition(...)方法，则不会置顶。**/
                        mLayoutManager.scrollToPositionWithOffset(i, 0);
                        mLayoutManager.setStackFromEnd(true);
                        return;
                    }
                }
                sengAction();
            }
        });
        CommonUtils.setOnTouch(addAction);
        map = Api.getCommonData();
    }

    private void sengAction() {
        ld = new LoadingDialog(SurveyActivity.this);
        ld.setCustomMessage("处理中");
        TopicServer.sendSurvey(this, CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson>(this) {
                    @Override
                    public void onNext(BaseJson baseJson) {
                        if (baseJson.code == 0) {
                            ToastUtils.showToast(SurveyActivity.this, "提交成功");
                            onBackPressed();
                        } else {
                            ToastUtils.showToast(SurveyActivity.this, baseJson.msg);
                        }
                        ld.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ld.dismiss();
                    }
                });
    }

    @Override
    public void reLoadingData() {

    }

    class ItemData {
        public int type;
        public String title;
        public ArrayList<String> content;
        public String tag;
        public
        @IdRes
        int checkId;

        //        接口名称；新增问卷调查记录
//        接口路径：/appAuth/insertQuestionSurvey/v1
//        公共参数：不变
//        业务参数：
//        duty        职务
//        ageRange    年龄
//        education    学历
//        certType    考证类型
//        trainWay    培训方式
//        trainNeed    培训需求
//        heartPrice  心里价位
//        suggest      建议
        public ItemData(int type, String title, ArrayList<String> content, String tag) {
            this.type = type;
            this.title = title;
            this.content = content;
            this.tag = tag;
        }

        public ItemData(int type, String title, String tag) {
            this.type = type;
            this.title = title;
            this.tag = tag;
        }
    }

    class ListAdapter extends CommonAdapter<ItemData> {
        private Context context;
        private ArrayList<ItemData> datas;
        private String inputText = "";
        private Map<String, String> checkMap;

        public ListAdapter(Context context, int layoutId, ArrayList<ItemData> datas) {
            super(context, layoutId, datas);
            this.context = context;
            this.datas = datas;
            checkMap = new HashMap<>();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void convert(ViewHolder holder, final ItemData itemData, int position) {
            final TextView title = holder.getView(R.id.title);
            title.setText(itemData.title);
            title.setTag(itemData.tag);
            final RadioGroup rg = holder.getView(R.id.content);
            LinearLayout input = holder.getView(R.id.input);
            final EditText inputContent = holder.getView(R.id.inputContent);
            if (itemData.type == 1) {
                rg.removeAllViews();
                input.setVisibility(View.GONE);
                rg.setVisibility(View.VISIBLE);
                for (String ele : itemData.content) {
                    AppCompatRadioButton appCompatRadioButton = new AppCompatRadioButton(context);
                    appCompatRadioButton.setTextSize(14);
                    appCompatRadioButton.setText("  " + ele);
                    Drawable drawable = getResources().getDrawable(R.drawable.check_select);
                    appCompatRadioButton.setButtonDrawable(drawable);
                    appCompatRadioButton.setTextColor(context.getResources().getColor(exam.e8net.com.exam.R.color.black333));
                    RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    param.setMargins(0, 20, 0, 20);
                    appCompatRadioButton.setLayoutParams(param);
                    appCompatRadioButton.setTag(ele);
                    String value = (String) map.get(itemData.tag);
                    if (value != null) {
                        LogUtils.d(value + "---" + ele + "....." + ele.equals(value));
                        if (ele.equals(value)) {
                            appCompatRadioButton.setChecked(true);
                        }
                    }
                    appCompatRadioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LogUtils.d(v.getTag() + "");
                            String tt = (String) v.getTag();
                            map.put((String) title.getTag(), tt);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    rg.addView(appCompatRadioButton);
                }

//                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                        AppCompatRadioButton gb = (AppCompatRadioButton) group.findViewById(checkedId);
//                        if (gb != null) {
//                            String tt = (String) gb.getTag();
//                            map.put((String) title.getTag(), tt);
//                        }
//                    }
//                });
            } else if (itemData.type == 2) {
                input.setVisibility(View.VISIBLE);
                rg.setVisibility(View.GONE);
                inputContent.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        inputText = inputContent.getText().toString().trim();
                        map.put((String) title.getTag(), inputText);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                inputContent.setText(inputText);
            }
        }
    }

    public void creatData() {
        datas = new ArrayList<>();
        ArrayList<String> d1 = new ArrayList<>();
        d1.add("项目施工人员");
        d1.add("项目基层管理人员");
        d1.add("项目中级管理人员");
        d1.add("其他");
        datas.add(new ItemData(1, "您的职务：", d1, "duty"));
        ArrayList<String> d2 = new ArrayList<>();
        d2.add("20岁及以下");
        d2.add("21-35岁");
        d2.add("36-50岁");
        d2.add("51岁以上");
        datas.add(new ItemData(1, "您的年龄：", d2, "ageRange"));
        ArrayList<String> d3 = new ArrayList<>();
        d3.add("小学及以下");
        d3.add("初中");
        d3.add("高中/中职/中专");
        d3.add("大专");
        d3.add("本科及以上");
        datas.add(new ItemData(1, "您的学历：", d3, "education"));
        ArrayList<String> d4 = new ArrayList<>();
        d4.add("技工");
        d4.add("职称");
        d4.add("一二级建造师");
        d4.add("造价工程师");
        d4.add("电气工程师");
        d4.add("监理工程师");
        d4.add("一二级建筑师");
        d4.add("一级注册消防师");
        d4.add("特种工");
        d4.add("八大员");
        datas.add(new ItemData(1, "您现阶段需要考证的类型：", d4, "certType"));
        ArrayList<String> d5 = new ArrayList<>();
        d5.add("网授（线上）");
        d5.add("面授（线下）");
        datas.add(new ItemData(1, "您能接受培训方式：", d5, "trainWay"));
        ArrayList<String> d6 = new ArrayList<>();
        d6.add("增强理论知识");
        d6.add("提高实际操作水平");
        d6.add("理论和实际相结合");
        datas.add(new ItemData(1, "您需要培训需求有哪些：", d6, "trainNeed"));
        ArrayList<String> d7 = new ArrayList<>();
        d7.add("1000元以下");
        d7.add("1000-3000元");
        d7.add("3000-5000元");
        d7.add("5000元以上");
        datas.add(new ItemData(1, "您心理能接受的价位：", d7, "heartPrice"));
        datas.add(new ItemData(2, "除以上之外，您对我们还有哪些建议：", "suggest"));
    }
}
