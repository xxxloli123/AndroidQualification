package exam.e8net.com.exam;

import com.dgg.baselibrary.db.been.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 */
public class Question implements Serializable {
    private static ArrayList<Topic> result = new ArrayList<>();
    private int errorCode;
    private String reason;


    public static void setResult(ArrayList<Topic> result2) {
        result = result2;
    }

    public static ArrayList<Topic> getResult() {
        return result;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
