package com.example.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kl.background.KLService;
import com.kl.ui.fragments.AFragment;
import com.kl.ui.fragments.BFragment;
import com.kl.ui.fragments.CFragment;
import com.kl.utils.Logger;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

    // A reference to the service used to get location updates.
    private KLService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.getLogger().e("[INF] my service connected!");
            KLService.LocalBinder binder = (KLService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.getLogger().e("[INF] my service disconnected!");
            mService = null;
            mBound = false;
        }

    };

    private TextView mTextMessage;
    FragmentManager mFragmentManager = getSupportFragmentManager();

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);

                    FragmentTransaction ts = mFragmentManager.beginTransaction();
                    ts.replace(R.id.holder, new AFragment());
                    ts.addToBackStack("A");
                    ts.commit();

                    return true;

                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);

                    FragmentTransaction ts1 = mFragmentManager.beginTransaction();
                    ts1.replace(R.id.holder, new BFragment());
                    ts1.addToBackStack("B");
                    ts1.commit();

                    return true;

                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);

                    FragmentTransaction ts2 = mFragmentManager.beginTransaction();
                    ts2.replace(R.id.holder, new CFragment());

                    ts2.addToBackStack("C");
                    ts2.commit();

                    return true;
            }

            return false;
        }
    };

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            //mFragmentManager.popBackStack();
            mFragmentManager.popBackStack("A", 0);//FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        Intent backgroundIntent = new Intent(this, KLService.class);
//        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        //    startForegroundService(backgroundIntent);
//        //} else {
//            startService(backgroundIntent);
//        //}
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent backgroundIntent = new Intent(this, KLService.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // NV: we need to start our service in other to trigger onStartCommand() on android 6.
            // This way ensure the flag START_STICKY will be used and our service not being killed by doze mode!
            startService(backgroundIntent);
        }

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, KLService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            Logger.getLogger().e("[INF] unbinding service...");
            unbindService(mServiceConnection);
            mBound = false;
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = Uri.parse("https://google.com");
        String uriText = uri.toString();
        Logger.getLogger().d("[INF] onResume");

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    private void testing() {
        Thread t = new Thread();
        try {
            t.join(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(null);
        //LocalBroadcastManager.getInstance(this).registerReceiver(null, null);

        sendStickyBroadcast(null);

    }
}
