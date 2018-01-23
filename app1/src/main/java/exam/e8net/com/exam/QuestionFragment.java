package exam.e8net.com.exam;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.been.Options;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.db.been.TopicAction;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jingewenku.abrahamcaijin.commonutil.AppDateMgr;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import exam.e8net.com.exam.been.MockAnswer;
import exam.e8net.com.exam.contract.QuestionFragmentContract;
import exam.e8net.com.exam.presenter.QuestionFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment implements QuestionFragmentContract.View {

    public int position, modle;
    private View v;
    private ImageView titleImg;//图片题的图片
    private TextView title, topicType;//文字题内容、 题型的标志
    private RadioGroup radioGroup;
    private TextView explainTxt, correctAnswer, myAnswer,
            doneNumbre, wrongNumbrer, rightNumbrer, answerTitle, statisticalTitle;//解释、正确答案、我的答案\做过次数、做错次数\做对次数
    private LinearLayout checkLayout, answerLayout, statisticalLayout, ll_seting, ll_explain;//多选答案选项布局
    private CheckBox collection, explain, seting;

    List<AppCompatRadioButton> listRadio;
    List<AppCompatCheckBox> listCheck;
    int answer;
    Topic result;
    private int[] checkABCD = {R.drawable.check_a, R.drawable.check_b, R.drawable.check_c, R.drawable.check_d,
            R.drawable.check_e, R.drawable.check_f, R.drawable.check_g, R.drawable.check_h};
    private int[] radioABCD = {R.drawable.tiku_tibiao1, R.drawable.tiku_tibiao2, R.drawable.tiku_tibiao3,
            R.drawable.tiku_tibiao4, R.drawable.tiku_tibiao5, R.drawable.tiku_tibiao6, R.drawable.tiku_tibiao7, R.drawable.tiku_tibiao8};
    private String userPhone = "";
    private User crentUser;
    private String crent;
    private AppCompatButton appCompatButton;//多选确定按钮
    private QuestionFragmentPresenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        position = bundle.getInt("position");
        userPhone = (String) SharedPreferencesUtils.getParam(getActivity(), "userPhone", "");
        modle = bundle.getInt(QuestionActivity.TOPIC_MODLE);
        mPresenter = new QuestionFragmentPresenter(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listRadio = new ArrayList<>();
        listCheck = new ArrayList<>();
        v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_question, container, false);
        crentUser = DBManager.getInstance(getActivity()).getCrrentUser();
        if (crentUser != null)
            initView();
        crent = crentUser.TopicData;
        initData();
        initListner();
        return v;
    }

    private void initView() {
        titleImg = (ImageView) v.findViewById(R.id.que_img);
        topicType = (TextView) v.findViewById(R.id.topicType);
        title = (TextView) v.findViewById(R.id.que_title);
        radioGroup = (RadioGroup) v.findViewById(R.id.que_group);
        explainTxt = (TextView) v.findViewById(R.id.que_explain_txt);
        checkLayout = (LinearLayout) v.findViewById(R.id.que_check_layout);
        correctAnswer = (TextView) v.findViewById(R.id.correctAnswer);
        myAnswer = (TextView) v.findViewById(R.id.myAnswer);
        doneNumbre = (TextView) v.findViewById(R.id.doneNumbre);
        wrongNumbrer = (TextView) v.findViewById(R.id.wrongNumbrer);
        rightNumbrer = (TextView) v.findViewById(R.id.rightNumbrer);
        collection = (CheckBox) v.findViewById(R.id.collection);
        explain = (CheckBox) v.findViewById(R.id.explain);
        seting = (CheckBox) v.findViewById(R.id.seting);
        answerTitle = (TextView) v.findViewById(R.id.answerTitle);
        answerLayout = (LinearLayout) v.findViewById(R.id.answerLayout);
        statisticalTitle = (TextView) v.findViewById(R.id.statisticalTitle);
        statisticalLayout = (LinearLayout) v.findViewById(R.id.statisticalLayout);
        ll_seting = (LinearLayout) v.findViewById(R.id.ll_seting);
        ll_explain = (LinearLayout) v.findViewById(R.id.ll_explain);
    }

    private void initData() {
        result = Question.getResult().get(position);
        title.setText("            " + result.title);
        String ckjs = "参考解析: " + (TextUtils.isEmpty(result.exaplain) ? "暂无解析" : result.exaplain);
        explainTxt.setText(CommonUtils.modifyTextColor(ckjs, Color.parseColor("#3c7ef8"), 0, 5));
        long category = result.categoryCode;
        if (category == 3) {
            initRadioButton();
            topicType.setText("单选");
        } else if (category == 4) {
            initCheckBox();
            topicType.setText("多选");
        }
        correctAnswer.setText(result.successAnswer);
        if (!TextUtils.isEmpty(result.imgUrl))
            Glide.with(getContext()).load(result.imgUrl).placeholder(R.drawable.loading).error(R.drawable.load_fail).into(titleImg);
        else {
            titleImg.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(crent)) {
            ArrayList<TopicAction> newData = new Gson().fromJson(crent, new TypeToken<ArrayList<TopicAction>>() {
            }.getType());
            for (TopicAction ele : newData) {
                if (result.id == ele.id) {
                    doneNumbre.setText("做过" + ele.complete + "次");
                    wrongNumbrer.setText("做错" + ele.completeError + "次");
                    rightNumbrer.setText("做对" + ele.completeCorrect + "次");
                    collection.setChecked(ele.isCollection);
                }
            }
        }

//收藏模式隐藏设置
        switch (modle) {
            case QuestionActivity.ERROR_TOPIC_RETROSPECT:/*错题回顾 模式*/
            case QuestionActivity.COLLECT:/*收藏模式*/
                explain.setChecked(true);
                explain.setEnabled(false);
                ll_seting.setVisibility(View.GONE);
                //收藏选择答案不可点击0804）同时选中选项
                if (listRadio != null && listRadio.size() > 0) {
                    radioButtonClickEnable();
                    AppCompatRadioButton radioButton = listRadio.get(answer);
                    setRightDrable(radioButton);//设置样式
                }
                if (appCompatButton != null)
                    appCompatButton.setVisibility(View.GONE);
                if (listCheck != null && listCheck.size() > 0) {
                    //收藏选择答案不可点击0804）
                    checkBoxClickEnable();
                }
//                collection.setEnabled(false);
                upStatus(true);
                break;
            case QuestionActivity.PRACTICE:/*练习模式*/
                EventBus.getDefault().post(3, QuestionActivity.UP_ACTIVITY);
                //初始化 答题模式UI
                if (!result.finishAnswer)
                    setUiExplain();
                break;
            case QuestionActivity.PRACTICE_CORRECT:/*练习 正确模式(未做习题)*/
                //初始化 答题模式UI
                if (!result.finishAnswer)
                    setUiExplain();
                break;
            case QuestionActivity.SIMULATION_TEST:/*模拟考试 模式*/
                ll_seting.setVisibility(View.INVISIBLE);
                ll_explain.setVisibility(View.INVISIBLE);
                EventBus.getDefault().post(4, QuestionActivity.UP_ACTIVITY);
                break;
            case QuestionActivity.PRACTICE_CHAPTERS:/*练习 章节练习*/
                EventBus.getDefault().post(5, QuestionActivity.UP_ACTIVITY);
                //初始化 答题模式UI
                if (!result.finishAnswer)
                    setUiExplain();
                break;
        }
    }

    private void setUiExplain() {
         /*是否设置过背题模式 且解析可点击问题*/
        if (QuestionActivity.isRecite) {
            upStatus(true);
            explain.setEnabled(false);
            explain.setChecked(true);
        } else {
            upStatus(false);
            explain.setEnabled(true);
            explain.setChecked(false);
        }
        mPresenter.correctOne(listRadio, answer, QuestionActivity.isRecite);
        mPresenter.correctMulti(listCheck, result.successAnswer, appCompatButton, QuestionActivity.isRecite);
    }

    void initRadioButton() {
        Gson gson = CommonUtils.getGson();
        ArrayList<Options> db = gson.fromJson(result.optionList, new TypeToken<ArrayList<Options>>() {
        }.getType());

        for (int i = 0; i < db.size(); i++) {
            addRadioButtonView(db.get(i).text, radioABCD[i]);
        }
        if (result.finishAnswer) {//判断当前题目是否已经答题答过了，如果是的话，就恢复答题数据，并且设置不可点击（
            radioButtonClickEnable();
            if (!result.chooseResult) {//如果没有选择到正确答案的话，就要显示错误答案，否则不显示
                AppCompatRadioButton radio = listRadio.get(result.errorAnswer - 1);
                setErrorDrable(radio);
            }
            AppCompatRadioButton radio = listRadio.get(result.rightAnswer);
            setRightDrable(radio);
            upStatus(true);
            myAnswer.setText(result.myAnswer);
        }
        switch (result.successAnswer) {
            case "A":
                answer = 0;
                break;
            case "B":
                answer = 1;
                break;
            case "C":
                answer = 2;
                break;
            case "D":
                answer = 3;
                break;
        }
    }


    void initCheckBox() {
        Gson gson = CommonUtils.getGson();
        ArrayList<Options> db = gson.fromJson(result.optionList, new TypeToken<ArrayList<Options>>() {
        }.getType());
        checkLayout.removeAllViews();
//        String[] correctlist = result.successAnswer.split(",");//正确答案数组
        for (int i = 0; i < db.size(); i++) {
            addCheckBoxView(db.get(i).text, checkABCD[i], db.get(i).id);
        }
        if (result.finishAnswer) {//判断当前题目是否已经答题答过了，如果是的话，就恢复答题数据，并且设置不可点击
            checkBoxClickEnable();
            //遍历用户的选择
            for (int i = 0; i < result.resultList.size(); i++) {
                int a = result.resultList.get(i);//拿到答题的标号
                listCheck.get(a - 1).setTextColor(getContext().getResources().getColor(R.color.error));
                listCheck.get(a - 1).setChecked(true);
            }

            for (int i = 0; i < result.rightList.size(); i++) {
                int a = result.rightList.get(i);//拿到答题的标号
                listCheck.get(a - 1).setTextColor(getContext().getResources().getColor(R.color.bule_tt));
            }
            upStatus(true);
            myAnswer.setText(result.myAnswer);
        } else {
            //添加一个确定按钮
            appCompatButton = new AppCompatButton(getContext());
            appCompatButton.setText("确定");
            appCompatButton.setBackground(getResources().getDrawable(R.drawable.button_no_bj));
            appCompatButton.setTextColor(Color.parseColor("#ffffff"));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(40, 30, 40, 20);
            appCompatButton.setLayoutParams(params);
            checkLayout.addView(appCompatButton);
            appCompatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        doHandle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            /*答题完成过后的操作  或者是收藏模式*/
            if (result.finishAnswer && appCompatButton != null)
                appCompatButton.setVisibility(View.GONE);
        }
    }

    /*【多选项】不可点击*/
    void radioButtonClickEnable() {
        for (AppCompatRadioButton radioButton :
                listRadio) {
            radioButton.setClickable(false);
        }
    }

    /*【单选项】不可点击*/
    void checkBoxClickEnable() {
        for (AppCompatCheckBox checkbos :
                listCheck) {
            checkbos.setClickable(false);
        }
    }

    /*【单选题】的答案设置*/
    void myAnswerOne(int answerIndex, boolean isError) {
        String answerTT = "";
        switch (answerIndex) {
            case 0:
                answerTT = "A";
                break;
            case 1:
                answerTT = "B";
                break;
            case 2:
                answerTT = "C";
                break;
            case 3:
                answerTT = "D";
                break;
            case 4:
                answerTT = "E";
                break;
            case 5:
                answerTT = "F";
                break;
        }
        myAnswer.setText(answerTT);
        result.myAnswer = answerTT;
        mockErrorCorrect(false, isError, 0);
    }

    private void initListner() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rd, int id) {
                try {
                    for (int i = 0; i < listRadio.size(); i++) {
                        AppCompatRadioButton radioButton = listRadio.get(i);
                        //遍历查找找到当前点击的item
                        if (radioButton.getId() == id) {
                            if (i == answer) {//判断选择是否是正确答案
                                result.chooseResult = (true);//存储用户选择的答案为正确的
                                setRightDrable(radioButton);//设置样式
                                if (QuestionActivity.isAuttomatic)
                                    EventBus.getDefault().post(2, QuestionActivity.UP_ACTIVITY);
                                updateDBCompleteData(0, 0, 1);
                                myAnswerOne(i, false);
                            } else {//选择的是错误答案
                                setRightDrable(listRadio.get(answer));
                                result.errorAnswer = (i + 1);//设置选错题目的标识
                                result.chooseResult = (false);//存储用户选择的答案为错误的
                                setErrorDrable(radioButton);
                                updateDBCompleteData(0, 1, 0);
                                myAnswerOne(i, true);
                            }
                            result.rightAnswer = (answer);//设置选对题目的标识
                            result.finishAnswer = (true);//设置完成了答题
                            radioButtonClickEnable();//设置不可点击
                            //更新MainActivity
                            EventBus.getDefault().post(1, QuestionActivity.UP_ACTIVITY);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateDBCompleteData(1, 0, 0);
                upStatus(true);
            }
        });

        explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upStatus(explain.isChecked());
            }
        });

        collection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDBCompleteData(0, 0, 0);
                showToast(isChecked ? "收藏成功" : "取消收藏");
            }
        });
        seting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicSetingDialog dialog = new TopicSetingDialog(getActivity());
                dialog.builder();
                dialog.setCheck(QuestionActivity.isAuttomatic, QuestionActivity.isRecite, QuestionActivity.isCleanError);
                dialog.isErrorModle(modle == QuestionActivity.PRACTICE_ERROR);
                dialog.setActionListener(new TopicSetingDialog.DetermineAction() {
                    @Override
                    public void Determine(boolean action1, boolean action2, boolean action3) {
                        QuestionActivity.isAuttomatic = action1;
                        QuestionActivity.isRecite = action2;
                        QuestionActivity.isCleanError = action3;
                        upStatus(action2);
                        ((QuestionActivity) getActivity()).upFmData(action2);
                    }
                });
                dialog.show();
            }
        });
    }

    public void upStatus(boolean isChecked) {
        if (modle == QuestionActivity.SIMULATION_TEST) {//模拟考试模式不显示
            return;
        }
        explainTxt.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        answerTitle.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        answerLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        if (modle != QuestionActivity.ERROR_TOPIC_RETROSPECT) {
            statisticalTitle.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            statisticalLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        }

    }

    /*背题模拟式 解释按钮的更新*/
    public void upExplain(boolean action2) {
        explain.setChecked(action2);
        explain.setEnabled(!action2);
        LogUtils.d(position + "---跟新 内存 状态 " + action2);
        mPresenter.correctOne(listRadio, answer, action2);
        mPresenter.correctMulti(listCheck, result.successAnswer, appCompatButton, action2);
        result.finishAnswer = false;
    }


    public void addCheckBoxView(String question, int drawable, String id) {
        AppCompatCheckBox checkBox = new AppCompatCheckBox(getContext());
        checkBox.setText(question);
        checkBox.setTextSize(15);
        checkBox.setButtonDrawable(drawable);
        checkBox.setTextColor(getContext().getResources().getColor(R.color.black333));
        RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(0, 20, 0, 20);
        checkBox.setLayoutParams(param);
        checkBox.setTag(id);
        checkLayout.addView(checkBox);
        listCheck.add(checkBox);
//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.setBackgroundColor(Color.parseColor("#f2f2f2"));
//            }
//        });
    }


    public void addRadioButtonView(String question, int radioDrawable) {
        AppCompatRadioButton appCompatRadioButton = new AppCompatRadioButton(getContext());
        appCompatRadioButton.setTextSize(15);
        appCompatRadioButton.setText(question);
        Drawable drawable = getResources().getDrawable(radioDrawable);
        appCompatRadioButton.setButtonDrawable(drawable);
        appCompatRadioButton.setTextColor(getContext().getResources().getColor(R.color.black333));
        RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(10, 10, 10, 10);
        appCompatRadioButton.setLayoutParams(param);
        radioGroup.addView(appCompatRadioButton);
        listRadio.add(appCompatRadioButton);
    }

    /*单选【错误】的设置*/
    @Override
    public void setErrorDrable(AppCompatRadioButton appCompatRadioButton) {
        appCompatRadioButton.setTextColor(getContext().getResources().getColor(R.color.error));
        appCompatRadioButton.setTextSize(15);
        Drawable bitmapDrawable = getResources().getDrawable(R.drawable.tiku_xuanxiang_cuowu);
        appCompatRadioButton.setButtonDrawable(bitmapDrawable);
    }

    @Override
    public int[] getRadioABCD() {
        return radioABCD;
    }

    @Override
    public int[] getCheckABCD() {
        return checkABCD;
    }

    /*单选【正确】的设置*/
    @Override
    public void setRightDrable(AppCompatRadioButton appCompatRadioButton) {
        appCompatRadioButton.setTextColor(getContext().getResources().getColor(R.color.bule_tt));
        appCompatRadioButton.setTextSize(15);
        Drawable bitmapDrawable = getResources().getDrawable(R.drawable.tiku_xuanxiang_zhengque);
        appCompatRadioButton.setButtonDrawable(bitmapDrawable);
    }

    /*多选【错误】的设置*/
    public void setCheckError(AppCompatCheckBox view) {
        Drawable bitmapDrawable = getResources().getDrawable(R.drawable.tiku_xuanxiang_cuowu);
        view.setButtonDrawable(bitmapDrawable);
        view.setTextColor(getContext().getResources().getColor(R.color.error));
    }

    /*多选【正确】的设置*/
    @Override
    public void setCheckCorrect(AppCompatCheckBox view) {
        Drawable bitmapDrawable = getResources().getDrawable(R.drawable.tiku_xuanxiang_zhengque);
        view.setButtonDrawable(bitmapDrawable);
        view.setTextColor(getContext().getResources().getColor(R.color.bule_tt));
    }

    //处理点击事件
    private void doHandle() {
        String dd = "";
        //存储用户选择的答案
        for (int i = 0; i < listCheck.size(); i++) {
            if (listCheck.get(i).isChecked()) {  //遍历查询当前是否选中
                result.resultList.add(i + 1);
                switch (i) {
                    case 0:
                        dd += "A,";
                        break;
                    case 1:
                        dd += "B,";
                        break;
                    case 2:
                        dd += "C,";
                        break;
                    case 3:
                        dd += "D,";
                        break;
                    case 4:
                        dd += "E,";
                        break;
                }
            }
        }
        if (!TextUtils.isEmpty(dd) && dd.length() > 2) {
            String more = dd.substring(0, dd.length() - 1);
            myAnswer.setText(more);
            result.myAnswer = more;
        } else {
            showToast("至少选择两项答案");
            return;
        }
        mPresenter.getSuccessAnswer(result.successAnswer);
        //然后进行筛选
        commonJudge();
        upStatus(true);
    }


    @Override
    public void A() {
        result.rightList.add(1);
        setCheckCorrect(listCheck.get(0));
    }

    @Override
    public void B() {
        result.rightList.add(2);
        setCheckCorrect(listCheck.get(1));
    }

    @Override
    public void C() {
        result.rightList.add(3);
        setCheckCorrect(listCheck.get(2));
    }

    @Override
    public void D() {
        result.rightList.add(4);
        setCheckCorrect(listCheck.get(3));
    }

    @Override
    public void E() {
        result.rightList.add(5);
        setCheckCorrect(listCheck.get(4));
    }

    void commonJudge() {
        //先判断用户输入的和答案的选择个数是否相同
        if (result.resultList.size() == result.rightList.size()) {
            for (int i = 0; i < result.resultList.size(); i++) {
                if (result.resultList.get(i) != result.rightList.get(i)) {
                    //如果有错误的话就把错误答案显示出来【标记错误】
                    for (int j = 0; j < listCheck.size(); j++) {
                        if (listCheck.get(j).isChecked()) {  //遍历查询当前是否选中
                            setCheckError(listCheck.get(j));
//                            updateDBCompleteData(0, 1, 0);
                            answerTitle.setText("答题错误");
                            answerTitle.setTextColor(Color.parseColor("#FF4081"));
                            mockErrorCorrect(true, true, 0);
                            result.chooseResult = false;//答题错误
                        }
                    }
                    break;
                } else {
                    result.chooseResult = true;//答题正确
                }
            }
            if (result.chooseResult) {/*答对自动跳转*/
                if (QuestionActivity.isAuttomatic)
                    EventBus.getDefault().post(2, QuestionActivity.UP_ACTIVITY);
                updateDBCompleteData(0, 0, 1);
                answerTitle.setText("答题正确");
                answerTitle.setTextColor(Color.parseColor("#72bc38"));
                mockErrorCorrect(true, false, 0);
            } else {
                updateDBCompleteData(0, 1, 0);
                //如果有错误的话就把错误答案显示出来
                for (int j = 0; j < listCheck.size(); j++) {
                    if (listCheck.get(j).isChecked()) {  //遍历查询当前是否选中
                        setCheckError(listCheck.get(j));
                        answerTitle.setText("答题错误");
                        answerTitle.setTextColor(Color.parseColor("#FF4081"));
                        mockErrorCorrect(true, true, 0);
                        result.chooseResult = false;//答题错误
                    }
                }
            }
        } else {
            /*答案个不相等--错误答案显示出来*/
            for (int j = 0; j < listCheck.size(); j++) {
                if (listCheck.get(j).isChecked()) {  //遍历查询当前是否选中
                    setCheckError(listCheck.get(j));
                    answerTitle.setText("答题错误");
                    answerTitle.setTextColor(Color.parseColor("#FF4081"));
                    result.chooseResult = false;//答题错误
                }
            }
            updateDBCompleteData(0, 1, 0);
            mPresenter.getSuccessAnswer(result.successAnswer);
        }
        result.finishAnswer = (true);
        checkBoxClickEnable();
        EventBus.getDefault().post(1, QuestionActivity.UP_ACTIVITY);
        updateDBCompleteData(1, 0, 0);
    }

    /* 更新 【做过】 【做错】 【做对】 数据
    * 全部出 参数传入0就是手动收藏
    * */
    private void updateDBCompleteData(int complete, int completeError, int completeCorrect) {
        crentUser = DBManager.getInstance(getActivity()).getCrrentUser();
        if (crentUser != null)
            crent = crentUser.TopicData;
        ArrayList<TopicAction> newData = new ArrayList<>();
        if (TextUtils.isEmpty(crent)) {
            TopicAction dd = new TopicAction();
            if (complete != 0) {
                dd.complete += complete;
                doneNumbre.setText("做过" + dd.complete + "次");
            }
            dd.id = result.id;
            if (completeError != 0 && modle != QuestionActivity.SIMULATION_TEST) {
                dd.completeError += completeError;
                dd.isCorrect = false;
                wrongNumbrer.setText("做错" + dd.completeError + "次");
                errorAndCorrect(true);
            }
            if (completeCorrect != 0) {
                dd.completeCorrect += completeCorrect;
                dd.isCorrect = true;
                rightNumbrer.setText("做对" + dd.completeCorrect + "次");
                errorAndCorrect(false);
                if (QuestionActivity.isCleanError) {
                    dd.completeError = 0;
                    wrongNumbrer.setText("做错" + dd.completeError + "次");

                }
            }
            dd.isCollection = collection.isChecked();
            dd.typeCode = result.typeCode;
            dd.typeCodeName = result.typeCodeName;
            newData.add(dd);

        } else {
            newData = new Gson().fromJson(crent, new TypeToken<ArrayList<TopicAction>>() {
            }.getType());
            TopicAction dd2 = null;
            for (TopicAction ele : newData) {
                if (ele.id == result.id)
                    dd2 = ele;
            }
            if (dd2 == null) {
                dd2 = new TopicAction();
                if (complete != 0) {
                    dd2.complete += complete;
                    doneNumbre.setText("做过" + dd2.complete + "次");
                }
                dd2.id = result.id;
                if (completeError != 0 && modle != QuestionActivity.SIMULATION_TEST) {
                    dd2.completeError += completeError;
                    dd2.isCorrect = false;
                    wrongNumbrer.setText("做错" + dd2.completeError + "次");
                    errorAndCorrect(true);
                }
                if (completeCorrect != 0) {
                    dd2.completeCorrect += completeCorrect;
                    dd2.isCorrect = true;
                    rightNumbrer.setText("做对" + dd2.completeCorrect + "次");
                    errorAndCorrect(false);
                    if (QuestionActivity.isCleanError) {
                        dd2.completeError = 0;
                        wrongNumbrer.setText("做错" + dd2.completeError + "次");
                    }
                }
                dd2.isCollection = collection.isChecked();
                dd2.typeCode = result.typeCode;
                dd2.typeCodeName = result.typeCodeName;
                newData.add(dd2);
            } else {
                newData.remove(dd2);
                if (complete != 0) {
                    dd2.complete += complete;
                    doneNumbre.setText("做过" + dd2.complete + "次");
                }
                dd2.id = result.id;
                if (completeError != 0 && modle != QuestionActivity.SIMULATION_TEST) {
                    dd2.completeError += completeError;
                    dd2.isCorrect = false;
                    wrongNumbrer.setText("做错" + dd2.completeError + "次");
                    errorAndCorrect(true);
                }
                if (completeCorrect != 0) {
                    dd2.completeCorrect += completeCorrect;
                    dd2.isCorrect = true;
                    rightNumbrer.setText("做对" + dd2.completeCorrect + "次");
                    errorAndCorrect(false);
                    if (QuestionActivity.isCleanError) {
                        dd2.completeError = 0;
                        wrongNumbrer.setText("做错" + dd2.completeError + "次");
                    }
                }
                dd2.isCollection = collection.isChecked();
                dd2.typeCode = result.typeCode;
                dd2.typeCodeName = result.typeCodeName;
                newData.add(dd2);
            }
        }
        crentUser.TopicData = CommonUtils.getGson().toJson(newData);
        /*记录坚持天数*/
        HashSet<String> insistSet;
        insistSet = CommonUtils.getGson()
                .fromJson(crentUser.insistDay, new TypeToken<HashSet<String>>() {
                }.getType());
        if (insistSet == null)
            insistSet = new HashSet<>();
        insistSet.add(AppDateMgr.todayYyyyMmDd());
        crentUser.insistDay = CommonUtils.getGson().toJson(insistSet);

        DBManager.getInstance(getActivity()).refreshUser(userPhone, crentUser);
        if (position == Question.getResult().size() - 1) {
            ((QuestionActivity) getActivity()).topicComplete(result.typeCodeName);
        }
        if (appCompatButton != null && complete != 0 && completeError != 0 && completeCorrect != 0) {
            //收藏的时候除外
            appCompatButton.setVisibility(View.GONE);
        }
    }

    void errorAndCorrect(boolean isError) {
        if (isError) {
            answerTitle.setText("答题错误");
            answerTitle.setTextColor(Color.parseColor("#FF4081"));
        } else {
            answerTitle.setText("答题正确");
            answerTitle.setTextColor(Color.parseColor("#72bc38"));
        }
    }

    @Override
    public Context getMyContext() {
        return getContext();
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(getMyContext(), msg);
    }

    /*模拟考试 答题正确 还是错误
     * isMulti 是否是多选题  true（多选题）false（单选题）
     * isError 答题是否错误  true(答题错误) false（答题正确）
     * multiType  0(单选、多选正确错误)  1（多选没有选全）
     * */
    private void mockErrorCorrect(boolean isMulti, boolean isError, int multiType) {
        EventBus.getDefault().post(new MockAnswer(isMulti, isError, multiType), QuestionActivity.GRADE_COUNT);
    }

}
