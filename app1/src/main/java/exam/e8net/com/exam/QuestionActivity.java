package exam.e8net.com.exam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.loading.ldialog.LoadingDialog;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.jingewenku.abrahamcaijin.commonutil.AppDateMgr;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;

import exam.e8net.com.exam.been.Mock;
import exam.e8net.com.exam.been.MockAnswer;
import exam.e8net.com.exam.been.MockDetail;
import exam.e8net.com.exam.been.MockGrade;
import exam.e8net.com.exam.contract.QuestionContract;
import exam.e8net.com.exam.presenter.QuestionPresenter;

import static android.R.attr.duration;

public class QuestionActivity extends AppCompatActivity implements QuestionContract.View {
    public static final String TOPIC_MODLE = "topic_modle";//答题界面的模式
    public static final String IS_SHOW_DIALOG = "isshowdialog";/*结束作答  Event-tag*/
    public static final String SET_CURRENT_INDEX = "set_current_index"; /*退出练习记录当前题号  Event-tag*/
    public static final String UP_COLLECTIONNUMBER = "up_collectionNumber";/*收藏的题数 Event-tag*/
    public static final String UP_ACTIVITY = "upActivity";/*跟新做题界面数据 Event-tag*/
    public static final String GRADE_COUNT = "grade_count";/*模拟考试 分数计算 Event-tag*/
    public static final String UP_PERCENTAGE = "up_percentage";/*完成率更新 Event-tag*/
    public static final String UP_TOPIC_DATA = "up_topic_data";/*刷新数据  Event-tag*/
    public static final String UP_MOCK_GRADE = "up_mock_grade";/*上传模拟考试成绩  Event-tag*/
    public static final String UP_CHAPTER = "UP_chapter";/*更新 章节练习信息  Event-tag*/
    public static final String UP_MOCK_GRADE_COMPLETE = "up_mock_grade_complete";/*上传模拟考试成绩【完成】    Event-tag*/
    public static final String MOCK_DATA = "mock_data";/* 模拟考试  试卷的信息 */
    /*是否自动跳转、是否是背题模式、是否自动清理出题记录、是否提示过没有及格*/
    public static boolean isAuttomatic = true, isRecite = false, isCleanError = false, isNoPass = false;
    protected static ViewPager viewPager;
    private static TextView rightTxt, errorTxt, totalTxt, countdownTime, theirPapers;
    boolean isExam, isChapters;//练习模式有弹窗提示\是否是自己练习
    private DefineTimer countDownTimer;
    private LinearLayout ll_countdown;//倒计时布局
    private ImageView back;
    private int currentIndex = 0;
    private ArrayList<String> tagList = new ArrayList<>();//加载了fragment的tags
    public final static int COLLECT = 1 << 0;/*【收藏】模式*/
    public final static int PRACTICE = 1 << 1;/*【练习】模式*/
    public final static int PRACTICE_CORRECT = 1 << 4;/*【练习正确】模式*/
    public final static int PRACTICE_ERROR = 1 << 2;/*【练习错题】模式*/
    public final static int SIMULATION_TEST = 1 << 3;/*【模拟考试】模式*/
    public final static int PRACTICE_CHAPTERS = 1 << 5;/*【章节练习模式】模式*/
    public final static int ERROR_TOPIC_RETROSPECT = 1 << 6;/*【错题回顾模式】模式*/

//    @IntDef({COLLECT, PRACTICE, PRACTICE_ERROR, SIMULATION_TEST})
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface CheckType {
//    }

    private int modle;
    /*标准考试时间、考试实际用了多少时间、错误的成绩、正确的成绩、错误的数量*/
    private long currentTime = 0L, whenTime, errorGrade, correctGrade, errorNumber;
    private AlertDialog dialog = null;
    private Mock Mockdata;//模拟试题信息
    private LoadingDialog ld;
    private boolean isMock;//是否模拟试题过
    private BottomSheetDialog bottomSheetDialog;
    private float slideOffset = -1;
    private int firstVisibleItem = -1;
    private QuestionPresenter mPresentr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_main);
        mPresentr = new QuestionPresenter(this);
        Window wind = getWindow();
        wind.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        wind.setStatusBarColor(Color.TRANSPARENT);
//        wind.setNavigationBarColor(Color.TRANSPARENT);
        currentIndex = getIntent().getIntExtra("currentIndex", 0);
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        rightTxt = (TextView) findViewById(R.id.main_right_tx);
        errorTxt = (TextView) findViewById(R.id.main_error_tx);
        totalTxt = (TextView) findViewById(R.id.main_total_tx);
        countdownTime = (TextView) findViewById(R.id.countdownTime);
        ll_countdown = (LinearLayout) findViewById(R.id.ll_countdown);
        back = (ImageView) findViewById(R.id.question_back);
        theirPapers = (TextView) findViewById(R.id.theirPapers);
        theirPapers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer();
            }
        });
        totalTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetDialog();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExam)
                    backHandler();
                else if (modle == SIMULATION_TEST && isMock)
                    mockCompele("提示", "您还没有答题完成，是否交卷", "是", "否", false);
                else
                    finish();
            }
        });
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new MyPagerChangeListner());
        getMyIntent();
        viewPager.setCurrentItem(currentIndex);
        viewPager.setOffscreenPageLimit(1);//初始化的个数
        EventBus.getDefault().register(this);
    }

    private void getMyIntent() {
        Intent intent = getIntent();
        modle = intent.getIntExtra(TOPIC_MODLE, 0);
        Mockdata = (Mock) intent.getSerializableExtra(MOCK_DATA);
        if (Mockdata != null)
            currentTime = Mockdata.duration * 60 * 1000;
    }


    @Override
    protected void onPause() {
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (countDownTimer == null) {
            startTimer();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
        updateTopicData();
        EventBus.getDefault().unregister(this);
        isAuttomatic = true;
        isRecite = false;
        isCleanError = false;
    }

    public void nextQuestion() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (viewPager.getCurrentItem() <= Question.getResult().size()) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    public void openBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(this);
        GridView gridView = new GridView(this);
        gridView.setNumColumns(6);
        gridView.setVerticalSpacing(10);
        gridView.setHorizontalSpacing(10);
        gridView.setBackgroundColor(0xffffffff);
        gridView.setAdapter(new AnswerAdapter());
        gridView.setScrollBarStyle(GridView.SCROLLBARS_OUTSIDE_INSET);
        gridView.setPadding(20, 20, 20, 20);
        bottomSheetDialog.setContentView(gridView);
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                // Right here!
                final BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
                behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {//(newState == 1 && slideOffset == 1 && firstVisibleItem == 0)
                            dialog.dismiss();
                        }

                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            ((BottomSheetBehavior) behaviour).setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset2) {
                        slideOffset = slideOffset2;
                    }
                });
            }
        });
        bottomSheetDialog.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(position, false);
                bottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(this, msg);
    }

    class MyPagerChangeListner implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            totalTxt.setText((position + 1) + "/" + Question.getResult().size());
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            QuestionFragment questionFragment = new QuestionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putInt(TOPIC_MODLE, modle);//做题模式模式
            questionFragment.setArguments(bundle);
            return questionFragment;
        }

        @Override
        public int getCount() {
            return Question.getResult().size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            tagList.add(makeFragmentName2(container.getId(), getItemId(position)));
            return super.instantiateItem(container, position);
        }

        private String makeFragmentName2(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }

    }

    /*更新所以fragment的内容*/
    public void upFmData(boolean isCheck) {
        for (String ele : tagList) {
            QuestionFragment itemFragment = ((QuestionFragment) (getSupportFragmentManager().findFragmentByTag(ele)));
            itemFragment.upStatus(isCheck);
            itemFragment.upExplain(isCheck);
        }
    }

    static int rightCount;
    static int errorCount;

    @Override
    public void upDataRightAndError() {
        rightCount = 0;
        errorCount = 0;
        for (Topic result : Question.getResult()
                ) {
            if (result.finishAnswer) {
                if (result.chooseResult) {
                    rightCount++;
                } else {
                    errorCount++;
                }
            }
        }
        rightTxt.setText(rightCount + "");
        errorTxt.setText(errorCount + "");
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExam)
                backHandler();
            else if (modle == SIMULATION_TEST && isMock)
                mockCompele("提示", "您还没有答题完成，是否交卷", "是", "否", false);
            else
                finish();
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    /*练习模式 返回的提示*/
    private void backHandler() {
        int finishAnswer = 0;
        int rightAnswer = 0;
        for (Topic r : Question.getResult()) {
            if (r.chooseResult)
                rightAnswer++;
            if (r.finishAnswer)
                finishAnswer++;
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("确定要结束练习？")
                .setNegativeButton("下次继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EventBus.getDefault().post(viewPager.getCurrentItem(), SET_CURRENT_INDEX);
                        QuestionActivity.this.finish();
                    }
                }).setPositiveButton("结束作答", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EventBus.getDefault().post("ok", IS_SHOW_DIALOG);
                        cleanIndexNumber();
                        QuestionActivity.this.finish();
                    }
                }).create();
        dialog.show();
    }


    /*答题完成*/
    public void topicComplete(String typeCodeName) {
        if (isExam && dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("练习题已完成");
            builder.setMessage("确定要结束练习？？？");
            builder.setNegativeButton("确定提交", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EventBus.getDefault().post("ok", IS_SHOW_DIALOG);
                    cleanIndexNumber();
                    QuestionActivity.this.finish();
                }
            });
            builder.setPositiveButton("重新练习", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (Topic ele : Question.getResult()) {
                        ele.rightAnswer = 0;
                        ele.errorAnswer = 0;
                        ele.chooseResult = false;
                        ele.finishAnswer = false;
                        ele.rightList.clear();
                        ele.resultList.clear();
                    }
                    viewPager.setCurrentItem(0);
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
        if (isChapters) {/*章节练习最后一题的操作*/
            mPresentr.chaptersAction(typeCodeName);
        }
    }

    /*清除当前 题号*/
    void cleanIndexNumber() {
        User crentUser = DBManager.getInstance(QuestionActivity.this).getCrrentUser();
        crentUser.currentIndex = 0;
        UserDao ud = new UserDao(QuestionActivity.this);
        ud.refreshUser(crentUser);
    }

    /*通知刷新数据*/
    private void updateTopicData() {
        EventBus.getDefault().post("1", UP_TOPIC_DATA);
    }

    /*msg 1(更新页面对错) 2（自动跳转下一题）3（专题练习 有弹窗提示）4(模拟考试的操作)
    * 5(章节练习操作)
    * */
    @Subscriber(tag = UP_ACTIVITY)
    public void upActivity(int msg) {
        if (msg == 1) {
            this.upDataRightAndError();
        } else if (msg == 2) {
            this.nextQuestion();
        } else if (msg == 3) {
            isExam = true;
        } else if (msg == 4) {
            ll_countdown.setVisibility(View.VISIBLE);
        } else if (msg == 5) {
            isChapters = true;
        }
    }

    /*模拟试题 倒计时*/
    private void startTimer() {
        if (modle != SIMULATION_TEST) {
            return;
        }
        countDownTimer = new DefineTimer(this, currentTime, 1000) {//2700 45分钟
            @Override
            public void onTick(long l) {
                currentTime = l;
                int allSecond = (int) l / 1000;//秒
                int minute = allSecond / 60;
                int second = allSecond - minute * 60;
                countdownTime.setText(minute + ":" + second);
                whenTime += 1;
            }

            @Override
            public void onFinish() {
                mockCompele("时间到啦~", "考试时间已到，系统自动交卷", "确认交卷", "", true);
            }
        };
        countDownTimer.start();
    }

    //提交答案
    private void submitAnswer() {
        ld = new LoadingDialog(QuestionActivity.this);
        ld.setCustomMessage("保存考试记录");
        ld.show();
        MockGrade MG = new MockGrade();
        MG.paperId = Mockdata.id;
        MG.score = correctGrade;
        MG.time = whenTime;
        MG.detail = getMockDetail();
        MG.errorNumber = errorNumber;
        MG.passScore = Mockdata.passScore;
        EventBus.getDefault().post(MG, UP_MOCK_GRADE);
    }

    /*获取模拟式答题情况*/
    private ArrayList<MockDetail> getMockDetail() {
        ArrayList<MockDetail> dd = new ArrayList<>();
        ArrayList<Topic> mockData = Question.getResult();
        for (Topic ele : mockData) {
            MockDetail md = new MockDetail();
            md.qId = ele.id;
            md.answer = ele.myAnswer;
            if (ele.chooseResult) {
                md.status = 1;
            } else if (!ele.chooseResult) {
                md.status = 2;
                errorNumber += 1;
            } else if (TextUtils.isEmpty(ele.myAnswer)) {
                md.status = 3;
                errorNumber += 1;
            } else {
                md.status = 4;
                errorNumber += 1;
            }
            dd.add(md);
        }
        return dd;
    }

    /*模拟试题结束 后的操作*/
    private void mockCompele(final String title, String msg, String action1, String action2, boolean isTimeOut) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton(action1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                submitAnswer();
                dialog.dismiss();
            }
        });
        if (!isTimeOut)
            builder.setPositiveButton(action2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                    if (title.contains("再接"))
                        isNoPass = true;
                }
            });
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /*模拟考试 答题后的 计算得分情况*/
    @Subscriber(tag = GRADE_COUNT)
    private void gradeCount(MockAnswer mockAnswer) {
        isMock = true;
        if (!mockAnswer.isMulti) {//单选
            if (mockAnswer.isError)
                errorGrade += Mockdata.scScore;
            else
                correctGrade += Mockdata.scScore;
        } else if (mockAnswer.isMulti) {//多选
            if (mockAnswer.isError)
                errorGrade += Mockdata.mcScore;
            else
                correctGrade += Mockdata.mcScore;
        }
        if (errorGrade > (Mockdata.sumScore - Mockdata.passScore) && !isNoPass) {
            mockCompele("再接再厉！", "分数不及格，系统自动交卷", "确认交卷", "继续练习", false);
            isNoPass = true;
        }
    }

    /*模拟考试 提交完成的操作*/
    @Subscriber(tag = UP_MOCK_GRADE_COMPLETE)
    private void upGradeComplete(boolean isSuccess) {
        ld.dismiss();
        showToast(isSuccess ? "记录保存成功" : "记录保存失败");
        if (isSuccess)
            finish();
        else
            giveUpMock("提示", "此次模拟考试记录保存不成功，是否要放弃？", "放弃", "取消");
    }

    /*模拟试题结束 后的操作*/
    private void giveUpMock(String title, String msg, String action1, String action2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton(action1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(action2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
