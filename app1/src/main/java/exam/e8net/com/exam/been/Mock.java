package exam.e8net.com.exam.been;

import java.io.Serializable;

/**
 * Created by qiqi on 17/9/12.
 */

public class Mock implements Serializable {
    public int id;
    public int duration;//考试时长
    public int examTypeId;//考试类型ID
    public String examTypeName;//考试类型名称
    public int sumScore;//总分数
    public int passScore;//及格分数
    public int scNum;//单选题数量
    public int scScore;//单选题分值
    public int mcNum;//多选题数量
    public int mcScore;//多选题分值
    public String subjectName;//科目名称
    public int subjectId;//科目id
    public String title;//标题

}

