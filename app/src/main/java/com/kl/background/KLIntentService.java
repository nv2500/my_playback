package com.kl.background;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.kl.utils.Logger;

public class KLIntentService extends IntentService {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public KLIntentService() {
        super("Hello from IntentService");
    }
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.getLogger().e("[INF] onHandleIntent <<<");

        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }*/
    }
    /*  If you decide to also override other callback methods, such as onCreate(), onStartCommand(),
        or onDestroy(), be sure to call the super implementation so that the IntentService can properly
        handle the life of the worker thread.*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }
}
