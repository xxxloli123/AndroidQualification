package com.dgg.qualification.ui.mine;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.mine.server.MineServer;
import com.jingewenku.abrahamcaijin.commonutil.AppKeyBoardMgr;

import java.util.HashMap;

/**
 * Created by qiqi on 17/7/12.
 */

public class FeedbackActivity extends KtBaseActivity {

    private EditText content;
    private TextView addAction;

    @Override
    protected int contentLayoutId() {
        return R.layout.acticity_feedback;
    }

    @Override
    protected void initData() {
        initTitle("意见反馈");
        content = (EditText) findViewById(R.id.content);
        addAction = (TextView) findViewById(R.id.addAction);
        AppKeyBoardMgr.openKeybord(content, this);
        addAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TT = content.getText().toString();
                if (TextUtils.isEmpty(TT)) {
                    ToastUtils.showToast(FeedbackActivity.this, "请输入反馈意见");
                    return;
                }

                HashMap<String, Object> map = Api.getCommonData();
                map.put("feedBackContent", TT);
                MineServer.addFeedback(FeedbackActivity.this, CommonUtils.encryptDES(map)).
                        subscribe(new DefaultSubscriber<BaseJson>(FeedbackActivity.this) {
                            @Override
                            public void onNext(BaseJson baseJson) {
                                ToastUtils.showToast(FeedbackActivity.this, "谢谢反馈，我们会认真处理");
                                onBackPressed();
                            }
                        });

            }
        });
    }

    @Override
    public void reLoadingData() {

    }
}
