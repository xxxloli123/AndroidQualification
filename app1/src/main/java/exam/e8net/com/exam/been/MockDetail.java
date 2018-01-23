package exam.e8net.com.exam.been;

import java.io.Serializable;

/**
 * Created by qiqi on 17/9/13.
 * 模拟考试 答题明细
 */

public class MockDetail implements Serializable {

    public int qId;//试题id
    public int status;//答题状态，1：正确；2：错误；3：未答；4：半对半错(多选题未选全)；
    public String answer;//答案

}
