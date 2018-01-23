package exam.e8net.com.exam.contract;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;

import com.dgg.baselibrary.mvp.BaseContract;

import java.util.List;

/**
 * Created by qiqi on 17/9/4.
 */

public interface QuestionFragmentContract {
    interface Model {
    }

    interface View extends BaseContract.BaseView {
        void setRightDrable(AppCompatRadioButton appCompatRadioButton);

        void setErrorDrable(AppCompatRadioButton appCompatRadioButton);

        int[] getRadioABCD();

        int[] getCheckABCD();

        void setCheckCorrect(AppCompatCheckBox view);

//        /*多选答案是设置*/
//        void uiSetmulti(int answerIndex);

        void A();

        void B();

        void C();

        void D();

        void E();
    }

    interface Presenter {

        void correctOne(List<AppCompatRadioButton> listRadio, int answer, boolean isRecite);

        void correctMulti(List<AppCompatCheckBox> listCheck, String successAnswer, AppCompatButton appCompatButton, boolean isRecite);

        int getSuccessAnswer(String successAnswer);
    }
}
