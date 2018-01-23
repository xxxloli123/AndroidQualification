package exam.e8net.com.exam.been;

import java.io.Serializable;

/**
 * Created by qiqi on 17/9/14.
 */

public class MockHistoryGrade implements Serializable {
    public int id;
    public String title;//试卷名称
    public int sumScore;//总分数
    public int questionSum;//总的考题数量
    public int rightNum;//作对的题的数量
    public int errorNum;//做错的题的数量
    public String pa;//正确率
    public String examTypeName;//考试类型名称
    public String subjectName;//科目名称
    public long testTime;//考试时间
    public String passScore;//及格分数
    public long time;//总用时
}
