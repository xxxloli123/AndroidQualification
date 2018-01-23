package exam.e8net.com.exam.been;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by qiqi on 17/9/13.
 * 模拟考试成绩 提交
 */

public class MockGrade implements Serializable {
    public int paperId;//试卷id
    public long time;//考试所用时间
    public long score;//模拟考试 [总得分]
    public ArrayList<MockDetail> detail = new ArrayList<>();//答题明细
    public long passScore;//模拟考试   [及格分数]

    public int rId;//模拟考试上传的结果id
    public long errorNumber;//此次模拟考试的错误提数

}
