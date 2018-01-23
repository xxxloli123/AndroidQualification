package exam.e8net.com.exam.presenter;

import android.content.Context;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.google.gson.reflect.TypeToken;
import com.jingewenku.abrahamcaijin.commonutil.AppDateMgr;

import org.simple.eventbus.EventBus;

import java.util.HashSet;

import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.QuestionActivity;
import exam.e8net.com.exam.contract.QuestionContract;

/**
 * Created by qiqi on 17/9/4.
 */

public class QuestionPresenter implements QuestionContract.Presenter {

    private QuestionContract.View mView;

    public QuestionPresenter(QuestionContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void chaptersAction(String typeCodeName) {
        Context context = mView.getMyContext();
        User crentUser = DBManager.getInstance(context).getCrrentUser();
        HashSet<String> insistSet;
        insistSet = CommonUtils.getGson()
                .fromJson(crentUser.chaptersNumber, new TypeToken<HashSet<String>>() {
                }.getType());
        if (insistSet == null)
            insistSet = new HashSet<>();
        insistSet.add(typeCodeName);
        crentUser.chaptersNumber = CommonUtils.getGson().toJson(insistSet);
        DBManager.getInstance(context).refreshUser(crentUser);
        EventBus.getDefault().post(false, QuestionActivity.UP_CHAPTER);
        LogUtils.d("存储了章节练习--" + insistSet.size());
    }
}
