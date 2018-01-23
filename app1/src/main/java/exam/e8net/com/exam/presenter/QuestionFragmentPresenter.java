package exam.e8net.com.exam.presenter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;

import com.dgg.baselibrary.tools.LogUtils;

import java.util.List;

import exam.e8net.com.exam.R;
import exam.e8net.com.exam.contract.QuestionFragmentContract;

/**
 * Created by qiqi on 17/9/4.
 */

public class QuestionFragmentPresenter implements QuestionFragmentContract.Presenter {

    private QuestionFragmentContract.View mView;

    public QuestionFragmentPresenter(QuestionFragmentContract.View mView) {
        this.mView = mView;
    }

    /*标注【单选正确】选项*/
    @Override
    public void correctOne(List<AppCompatRadioButton> listRadio, int answer, boolean isRecite) {
        if (listRadio != null && listRadio.size() > answer) {
            for (int i = 0; i < listRadio.size(); i++) {
                AppCompatRadioButton arb = listRadio.get(i);
                if (isRecite && answer == i)
                    mView.setRightDrable(arb);
                else {
                    Drawable drawable = mView.getMyContext().getResources().getDrawable(mView.getRadioABCD()[i]);
                    arb.setButtonDrawable(drawable);
                    arb.setTextColor(mView.getMyContext().getResources().getColor(R.color.black333));
                }
                arb.setEnabled(!isRecite);
            }
        }
    }

    /*标注【多选正确】选项*/
    @Override
    public void correctMulti(List<AppCompatCheckBox> listCheck, String successAnswer, AppCompatButton appCompatButton, boolean isRecite) {

        if (listCheck != null && listCheck.size() > 0)
            for (int i = 0; i < listCheck.size(); i++) {
                AppCompatCheckBox accb = listCheck.get(i);
                Drawable drawable = mView.getMyContext().getResources().getDrawable(mView.getCheckABCD()[i]);
                accb.setButtonDrawable(drawable);
                accb.setTextColor(mView.getMyContext().getResources().getColor(R.color.black333));
                accb.setChecked(false);
                LogUtils.d("恢复正常");
                if (isRecite) {
                    getSuccessAnswer(successAnswer);
                    LogUtils.d("恢复正常-------------");
                }
                accb.setEnabled(!isRecite);
            }
        if (appCompatButton != null)
            appCompatButton.setVisibility(isRecite ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getSuccessAnswer(String successAnswer) {
//        if (successAnswer.contains("A")) {
//            mView.A();
//        } else if (successAnswer.contains("B")) {
//            mView.B();
//        } else if (successAnswer.contains("C")) {
//            mView.C();
//        } else if (successAnswer.contains("D")) {
//            mView.D();
//        } else if (successAnswer.contains("E")) {
//            mView.E();
//        }


        int answerIndex = 0;
        switch (successAnswer) {
            case "A,B":
                mView.A();
                mView.B();
                answerIndex = 1;
                break;
            case "A,C":
                mView.A();
                mView.C();
                answerIndex = 2;
                break;
            case "A,D":
                mView.A();
                mView.D();
                answerIndex = 3;
                break;
            case "A,E":
                mView.A();
                mView.E();
                answerIndex = 4;
                break;
            case "B,C":
                mView.B();
                mView.C();
                answerIndex = 5;
                break;
            case "B,D":
                mView.B();
                mView.D();
                answerIndex = 5;
                break;
            case "B,E":
                mView.B();
                mView.E();
                answerIndex = 7;
                break;
            case "C,D":
                mView.C();
                mView.D();
                answerIndex = 8;
                break;
            case "C,E":
                mView.C();
                mView.E();
                answerIndex = 9;
                break;
            case "D,E":
                mView.D();
                mView.E();
                answerIndex = 10;
                break;
            case "A,B,C":
                mView.A();
                mView.B();
                mView.C();
                answerIndex = 11;
                break;
            case "A,B,D":
                mView.A();
                mView.B();
                mView.D();
                answerIndex = 12;
                break;
            case "A,B,E":
                mView.A();
                mView.B();
                mView.E();
                answerIndex = 13;
                break;
            case "A,C,D":
                mView.A();
                mView.C();
                mView.D();
                answerIndex = 14;
                break;
            case "A,C,E":
                mView.A();
                mView.C();
                mView.E();
                answerIndex = 15;
                break;
            case "A,D,E":
                mView.A();
                mView.D();
                mView.E();
                answerIndex = 16;
                break;
            case "B,C,D":
                mView.B();
                mView.C();
                mView.D();
                answerIndex = 16;
                break;
            case "B,C,E":
                mView.B();
                mView.C();
                mView.E();
                answerIndex = 17;
                break;
            case "C,D,E":
                mView.C();
                mView.D();
                mView.E();
                answerIndex = 18;
                break;
            case "A,B,C,D":
                mView.A();
                mView.B();
                mView.C();
                mView.D();
                answerIndex = 19;
                break;
            case "A,B,C,E":
                mView.A();
                mView.B();
                mView.C();
                mView.E();
                answerIndex = 20;
                break;
            case "A,B,D,E":
                mView.A();
                mView.B();
                mView.D();
                mView.E();
                answerIndex = 21;
                break;
            case "A,C,D,E":
                mView.A();
                mView.C();
                mView.D();
                mView.E();
                answerIndex = 22;
                break;
            case "B,C,D,E":
                mView.B();
                mView.C();
                mView.D();
                mView.E();
                answerIndex = 23;
                break;
            case "A,B,C,D,E":
                mView.A();
                mView.B();
                mView.C();
                mView.D();
                mView.E();
                answerIndex = 24;
                break;
        }
        return answerIndex;
    }

}
