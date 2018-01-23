package exam.e8net.com.exam;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.dgg.baselibrary.tools.LogUtils;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/2/13.
 */

public abstract class DefineTimer {
    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;
    private WeakReference<Activity> wrf;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public DefineTimer(Activity context, long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
        wrf = new WeakReference<Activity>(context);
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mHandler.removeMessages(MSG);
//        wrf.clear();
//        System.gc();
        mCancelled = true;
//        LogUtils.d("seh-------设置取消 " + mCancelled);
    }

    /**
     * Start the countdown.
     */
    public synchronized final DefineTimer start() {
        mCancelled = false;
//        LogUtils.d("开始1111111-------设置取消 " + mCancelled);
        if (mMillisInFuture < 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFinish();
                }
            });
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }


    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();


    private static final int MSG = 1;


    // handles counting down
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Activity activity = wrf.get();
            if (activity != null) {
//                LogUtils.d("是否取消 " + mCancelled);
                synchronized (DefineTimer.this) {
                    if (mCancelled) {
                        return;
                    }

                    final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                    if (millisLeft < 0) {
                        onFinish();
                    } else if (millisLeft < mCountdownInterval) {
                        // no tick, just delay until done
                        sendMessageDelayed(obtainMessage(MSG), millisLeft);
                    } else {
                        long lastTickStart = SystemClock.elapsedRealtime();
                        onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval;
//                        LogUtils.d("dao倒计时：" + delay);
                        sendMessageDelayed(obtainMessage(MSG), delay);
                    }
                }
            }
        }
    };
}
