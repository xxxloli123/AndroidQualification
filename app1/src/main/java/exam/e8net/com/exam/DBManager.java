package exam.e8net.com.exam;

import android.content.Context;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;

/**
 * Created by qiqi on 17/7/24.
 */

public class DBManager {
    private static Context context;
    private UserDao ud;

    private static class Holder {
        private static DBManager element = new DBManager();
    }

    public static DBManager getInstance(Context myContext) {
        context = myContext;
        return Holder.element;
    }

    /*根据电话号码确定用户*/
    public User getCrrentUser() {
        String userPhone = (String) SharedPreferencesUtils.getParam(context, "userPhone", "");
        return getUser(userPhone);
    }

    /*根据电话号码确定用户*/
    public User getUser(String phone) {
        User user = null;
        try {
            if (ud == null)
                ud = new UserDao(context);
            for (User ele : ud.getUserList()) {
                if (phone.equals(ele.phone))
                    user = ele;
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
        return user;
    }

    /*根据电话号码  刷新用户*/
    public void refreshUser(String phone, User newUser) {
        if (ud == null)
            ud = new UserDao(context);
        //外面获取的是当前用户  所以跟新也是当前用户
//        User user = null;
//        for (User ele : ud.getUserList()) {
//            if (phone.equals(ele.phone))
//                user = ele;
//        }
//        user = newUser;
        ud.refreshUser(newUser);
    }

    /*根据电话号码  刷新用户 外面获取的是当前用户  所以跟新也是当前用户*/
    public void refreshUser(User newUser) {
        if (ud == null)
            ud = new UserDao(context);

        ud.refreshUser(newUser);
    }
}
