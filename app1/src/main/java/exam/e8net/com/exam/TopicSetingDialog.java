package exam.e8net.com.exam;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by qiqi on 17/4/18.
 */

public class TopicSetingDialog {

    private Context context;
    private Dialog dialog;
    private Display display;
    private TextView complete;
    private CheckBox checkbox1, checkbox2, checkbox3;
    private LinearLayout errorSeting;
    private DetermineAction listener;

    public TopicSetingDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public TopicSetingDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_topic_seting, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件F
        complete = (TextView) view.findViewById(R.id.complete);
        checkbox1 = (CheckBox) view.findViewById(R.id.checkbox1);
        checkbox2 = (CheckBox) view.findViewById(R.id.checkbox2);
        checkbox3 = (CheckBox) view.findViewById(R.id.checkbox3);
        errorSeting = (LinearLayout) view.findViewById(R.id.errorSeting);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (listener != null)
//                    listener.Determine(checkbox1.isChecked(), checkbox2.isChecked());
                dialog.dismiss();
            }
        });

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null)
                    listener.Determine(checkbox1.isChecked(), checkbox2.isChecked(), checkbox3.isChecked());
            }
        });
        return this;
    }


    public void show() {
        dialog.show();
    }

    public void setCheck(boolean ac1, boolean ac2, boolean ac3) {
        checkbox1.setChecked(ac1);
        checkbox2.setChecked(ac2);
        checkbox3.setChecked(ac3);
    }

    public void isErrorModle(boolean isErrorModle) {
        errorSeting.setVisibility(isErrorModle ? View.VISIBLE : View.GONE);
    }

    public void setActionListener(DetermineAction listener) {
        this.listener = listener;
    }

    public interface DetermineAction {
        void Determine(boolean action1, boolean action2, boolean action3);
    }
}
