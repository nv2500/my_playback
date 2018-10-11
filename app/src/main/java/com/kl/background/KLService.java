package com.kl.background;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.kl.utils.Logger;

import androidx.core.app.NotificationCompat;

public class KLService extends Service {

//    /**
//     * A constructor is required, and must call the super IntentService(String)
//     * constructor with a name for the worker thread.
//     */
//    public KLService() {
//        super("Hello from IntentService");
//    }
//    /**
//     * The IntentService calls this method from the default worker thread with
//     * the intent that started the service. When this method returns, IntentService
//     * stops the service, as appropriate.
//     */
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Logger.getLogger().e("[INF] onHandleIntent <<<");
//
//        // Normally we would do some work here, like download a file.
//        // For our sample, we just sleep for 5 seconds.
//        /*try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            // Restore interrupt status.
//            Thread.currentThread().interrupt();
//        }*/
//    }

    private final IBinder mBinder = new LocalBinder();      // interface for clients that bind

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    private static final int MSG_HEARTBEAT = 119;

    private NotificationManager mNotificationManager;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                // Restore interrupt status.
//                Thread.currentThread().interrupt();
//            }
//            // Stop the service using the startId, so that we don't stop
//            // the service in the middle of handling another job
//            stopSelf(msg.arg1);

            switch (msg.arg1) {
                case MSG_HEARTBEAT:
                    Logger.getLogger().i("...tick...");
                    doHeartBeat();
                    break;
            }
        }
    }

    private void initializeNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public void onCreate() {
        // The service is being created
        // super.onCreate();

        initializeNotification();

        Logger.getLogger().d("[INF] onCreate <<<");

        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        doHeartBeat();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Logger.getLogger().e("[INF] onBind <<<");

        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        stopForeground(true);
        mChangingConfiguration = false;

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.getLogger().d("[INF] in onUnbind, changing configuration = "+mChangingConfiguration);
        // All clients have unbound with unbindService()
        //return mAllowRebind;

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration) {// && Utils.requestingLocationUpdates(this)) {
            /*
            // TODO(developer). If targeting O, use the following code.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                mNotificationManager.startServiceInForeground(new Intent(this,
                        LocationUpdatesService.class), NOTIFICATION_ID, getNotification());
            } else {
                startForeground(NOTIFICATION_ID, getNotification());
            }
             */
            Logger.getLogger().d("[INF] start foreground service again");
            startForeground(NOTIFICATION_ID, getNotification());
        }

        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Logger.getLogger().d("[INF] onRebind <<<");

        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        stopForeground(true);
        mChangingConfiguration = false;

        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Logger.getLogger().e("[INF] onStartCommand <<<");

        Toast.makeText(this, "Service starting...", Toast.LENGTH_SHORT).show();

        boolean startedFromNotification = intent.getBooleanExtra("EXTRA_STARTED_FROM_NOTIFICATION",
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            Logger.getLogger().e("stop service from notification");
            stopSelf();
        }

//        // For each start request, send a message to start a job and deliver the
//        // start ID so we know which request we're stopping when we finish the job
//        Message msg = mServiceHandler.obtainMessage();
//        msg.arg1 = startId;
//        mServiceHandler.sendMessage(msg);

        // Tells the system to not try to recreate the service after it has been killed.
        // return START_NOT_STICKY;

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void doHeartBeat() {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = MSG_HEARTBEAT;
        mServiceHandler.sendMessageDelayed(msg, 3000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mChangingConfiguration = true;
    }

//    private void startForegroundService() {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        Notification notification = buildForegroundNotification(pendingIntent);
//
//        startForeground(NOTIFICATION_ID, notification);
//    }

    private Notification getNotification() {
        //PendingIntent pendingIntent =
        //       PendingIntent.getActivity(this, 0, notificationIntent, 0);
        CharSequence text = "text content";//Utils.getLocationText(mLocation);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        Intent intent = new Intent(this, KLService.class);
        intent.putExtra("EXTRA_STARTED_FROM_NOTIFICATION", true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        builder
                .addAction(R.drawable.ic_home_black_24dp, "open activity",
                        activityPendingIntent)
                .addAction(R.drawable.ic_dashboard_black_24dp, "stop application",
                        servicePendingIntent)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentTitle("Title goes here!")
                .setContentText("Content goes hedore!")
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setPriority(Notification.PRIORITY_HIGH)
                //.setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    @Override
    public void onDestroy() {
        Logger.getLogger().e("[INF] onDestroy <<<");
        // The service is no longer used and is being destroyed

        Toast.makeText(this, "Service done!", Toast.LENGTH_SHORT).show();

        mServiceHandler.removeCallbacksAndMessages(null);

        stopForeground(true);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public KLService getService() {
            return KLService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
