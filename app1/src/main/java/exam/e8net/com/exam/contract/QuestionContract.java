package exam.e8net.com.exam.contract;

import com.dgg.baselibrary.mvp.BaseContract;

/**
 * Created by qiqi on 17/9/4.
 */

public interface QuestionContract {
    interface Model {
    }

    interface View extends BaseContract.BaseView {
        /*更新页面对错的数据*/
        void upDataRightAndError();
    }

    interface Presenter {
        void chaptersAction(String typeCodeName);
    }
}
