package com.kl.background;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class PlaybackResultReceiver extends ResultReceiver {

    /*
     * This interface is implemented by the activity
     */
    private AppReceiver mAppReceiver;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public PlaybackResultReceiver(Handler handler, AppReceiver receiver) {
        super(handler);
        mAppReceiver = receiver;
    }

    /**
     *
     * @param resultCode Arbitrary result code delivered by the sender, as
     * defined by the sender.
     * @param resultData Any additional data provided by the sender.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        // super.onReceiveResult(resultCode, resultData);
        if (mAppReceiver != null) {
            /*
             * Pass the resulting data from the service to the activity
             * using the AppReceiver interface
             */
            mAppReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    public void releaseReceiver() {
        mAppReceiver = null;
    }

    public interface AppReceiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

}
