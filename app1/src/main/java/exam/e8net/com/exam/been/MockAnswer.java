package exam.e8net.com.exam.been;

/**
 * Created by qiqi on 17/9/13.
 */

public class MockAnswer {
    public boolean isMulti;//true（多选题）false（单选题）
    public boolean isError;//true(答题错误) false（答题正确）
    public int multiType; //0(单选、多选正确错误)  1（多选没有选全）

    public MockAnswer(boolean isMulti, boolean isError, int multiType) {
        this.isMulti = isMulti;
        this.isError = isError;
        this.multiType = multiType;
    }
}
